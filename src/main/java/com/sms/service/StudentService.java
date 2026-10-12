package com.sms.service;

import com.sms.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {

	Page<Student> getAllStudents(Pageable pageable);

	List<Student> getAllStudents();

	Student getStudentById(Long id);

	Student saveStudent(Student student);

	Student updateStudent(Long id, Student student);

	void deleteStudent(Long id);

	long countStudents();

	boolean isEmailTaken(String email);

	boolean isEmailTakenByOther(Long id, String email);
}
