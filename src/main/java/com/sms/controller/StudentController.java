package com.sms.controller;

import com.sms.entity.Student;
import com.sms.service.StudentService;
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
 * Student module - list, add, view, edit and delete students.
 */
@Controller
@RequestMapping("/students")
public class StudentController {

	private final StudentService studentService;

	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}

	@GetMapping
	public String listStudents(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
		Page<Student> studentPage = studentService.getAllStudents(pageable);

		model.addAttribute("students", studentPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("pageNumbers", PaginationUtil.getPageNumbers(page, studentPage.getTotalPages()));
		return "students";
	}

	@GetMapping("/new")
	public String showAddForm(Model model) {
		model.addAttribute("student", new Student());
		return "add-student";
	}

	@PostMapping
	public String saveStudent(@Valid @ModelAttribute("student") Student student,
			BindingResult result, Model model, RedirectAttributes ra) {

		if (studentService.isEmailTaken(student.getEmail())) {
			result.rejectValue("email", "error.student", "This email is already registered");
		}

		if (result.hasErrors()) {
			return "add-student";
		}

		studentService.saveStudent(student);
		ra.addFlashAttribute("successMessage", "Student saved successfully.");
		return "redirect:/students";
	}

	@GetMapping("/{id}")
	public String showStudent(@PathVariable Long id, Model model) {
		model.addAttribute("student", studentService.getStudentById(id));
		return "view-student";
	}

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model) {
		model.addAttribute("student", studentService.getStudentById(id));
		return "edit-student";
	}

	@PostMapping("/{id}")
	public String updateStudent(@PathVariable Long id,
			@Valid @ModelAttribute("student") Student student,
			BindingResult result, RedirectAttributes ra) {

		if (studentService.isEmailTakenByOther(id, student.getEmail())) {
			result.rejectValue("email", "error.student", "This email is already registered");
		}

		if (result.hasErrors()) {
			return "edit-student";
		}

		studentService.updateStudent(id, student);
		ra.addFlashAttribute("successMessage", "Student updated successfully.");
		return "redirect:/students";
	}

	@PostMapping("/{id}/delete")
	public String deleteStudent(@PathVariable Long id, RedirectAttributes ra) {
		studentService.deleteStudent(id);
		ra.addFlashAttribute("successMessage", "Student deleted successfully.");
		return "redirect:/students";
	}
}
