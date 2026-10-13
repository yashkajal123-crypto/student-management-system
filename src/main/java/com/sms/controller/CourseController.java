package com.sms.controller;

import com.sms.entity.Course;
import com.sms.service.CourseService;
import com.sms.util.PaginationUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Course module - list, add, view, edit and delete courses.
 */
@Controller
@RequestMapping("/courses")
public class CourseController {

	private final CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping
	public String listCourses(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by("courseName").ascending());
		Page<Course> coursePage = courseService.getAllCourses(pageable);

		model.addAttribute("courses", coursePage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("pageNumbers", PaginationUtil.getPageNumbers(page, coursePage.getTotalPages()));
		return "courses";
	}

	@GetMapping("/new")
	public String showAddForm(Model model) {
		model.addAttribute("course", new Course());
		return "add-course";
	}

	@PostMapping
	public String saveCourse(@Valid @ModelAttribute("course") Course course,
			BindingResult result, RedirectAttributes ra) {

		if (courseService.isCodeTaken(course.getCourseCode())) {
			result.rejectValue("courseCode", "error.course", "This course code is already used");
		}

		if (result.hasErrors()) {
			return "add-course";
		}

		courseService.saveCourse(course);
		ra.addFlashAttribute("successMessage", "Course saved successfully.");
		return "redirect:/courses";
	}

	@GetMapping("/{id}")
	public String showCourse(@PathVariable Long id, Model model) {
		model.addAttribute("course", courseService.getCourseById(id));
		return "view-course";
	}

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model) {
		model.addAttribute("course", courseService.getCourseById(id));
		return "edit-course";
	}

	@PostMapping("/{id}")
	public String updateCourse(@PathVariable Long id,
			@Valid @ModelAttribute("course") Course course,
			BindingResult result, RedirectAttributes ra) {

		if (courseService.isCodeTakenByOther(id, course.getCourseCode())) {
			result.rejectValue("courseCode", "error.course", "This course code is already used");
		}

		if (result.hasErrors()) {
			return "edit-course";
		}

		courseService.updateCourse(id, course);
		ra.addFlashAttribute("successMessage", "Course updated successfully.");
		return "redirect:/courses";
	}

	@PostMapping("/{id}/delete")
	public String deleteCourse(@PathVariable Long id, RedirectAttributes ra) {
		courseService.deleteCourse(id);
		ra.addFlashAttribute("successMessage", "Course deleted successfully.");
		return "redirect:/courses";
	}
}
