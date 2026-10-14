package com.sms.controller;

import com.sms.dto.EnrollmentSummaryDto;
import com.sms.entity.Enrollment;
import com.sms.entity.Student;
import com.sms.service.CourseService;
import com.sms.service.EnrollmentService;
import com.sms.service.StudentService;
import com.sms.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Enrollment module - enroll a student into one or more courses,
 * list enrolled students and show the enrollment details per student.
 */
@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {

	private final EnrollmentService enrollmentService;
	private final StudentService studentService;
	private final CourseService courseService;

	public EnrollmentController(EnrollmentService enrollmentService,
			StudentService studentService,
			CourseService courseService) {
		this.enrollmentService = enrollmentService;
		this.studentService = studentService;
		this.courseService = courseService;
	}

	/**
	 * "Students Enrolled in Courses" screen (with pagination).
	 */
	@GetMapping
	public String listEnrolledStudents(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) {
		PageRequest pageable = PageRequest.of(page, size);
		Page<EnrollmentSummaryDto> summaryPage = enrollmentService.getEnrollmentSummary(pageable);

		model.addAttribute("summaryPage", summaryPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("pageNumbers", PaginationUtil.getPageNumbers(page, summaryPage.getTotalPages()));
		return "enrolled-students";
	}

	/**
	 * "Enroll Student to Courses" screen.
	 */
	@GetMapping("/new")
	public String showEnrollForm(Model model) {
		model.addAttribute("students", studentService.getAllStudents());
		model.addAttribute("courses", courseService.getActiveCourses());
		return "enroll-course";
	}

	@PostMapping
	public String enrollStudent(@RequestParam Long studentId,
			@RequestParam(value = "courseIds", required = false) List<Long> courseIds,
			RedirectAttributes ra) {

		if (courseIds == null || courseIds.isEmpty()) {
			ra.addFlashAttribute("errorMessage", "Please select at least one course.");
			return "redirect:/enrollments/new";
		}

		String message = enrollmentService.enrollStudent(studentId, courseIds);

		if (message.contains("already")) {
			ra.addFlashAttribute("errorMessage", message);
			return "redirect:/enrollments/new";
		}

		ra.addFlashAttribute("successMessage", message);
		return "redirect:/enrollments";
	}

	/**
	 * Enrollment details for one student.
	 */
	@GetMapping("/{studentId}")
	public String showEnrollmentDetails(@PathVariable Long studentId, Model model) {
		Student student = studentService.getStudentById(studentId);
		List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);

		BigDecimal totalFee = BigDecimal.ZERO;
		for (Enrollment enrollment : enrollments) {
			totalFee = totalFee.add(enrollment.getFee());
		}

		model.addAttribute("student", student);
		model.addAttribute("enrollments", enrollments);
		model.addAttribute("totalCourses", enrollments.size());
		model.addAttribute("totalFee", totalFee);
		return "enrollment-details";
	}
}
