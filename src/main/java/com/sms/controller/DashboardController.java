package com.sms.controller;

import com.sms.service.CourseService;
import com.sms.service.EnrollmentService;
import com.sms.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Dashboard screen - quick statistics + recent registrations.
 */
@Controller
public class DashboardController {

	private final StudentService studentService;
	private final CourseService courseService;
	private final EnrollmentService enrollmentService;

	public DashboardController(StudentService studentService,
			CourseService courseService,
			EnrollmentService enrollmentService) {
		this.studentService = studentService;
		this.courseService = courseService;
		this.enrollmentService = enrollmentService;
	}

	@GetMapping({ "/", "/dashboard" })
	public String showDashboard(Model model) {
		model.addAttribute("totalStudents", studentService.countStudents());
		model.addAttribute("totalCourses", courseService.countCourses());
		model.addAttribute("topCourse", enrollmentService.getTopCourseCode());
		model.addAttribute("enrolledThisMonth", enrollmentService.countEnrollmentsThisMonth());
		model.addAttribute("recentEnrollments", enrollmentService.getRecentEnrollments(5));
		return "dashboard";
	}
}
