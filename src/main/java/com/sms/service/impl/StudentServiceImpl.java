package com.sms.service.impl;

import com.sms.entity.Student;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.StudentRepository;
import com.sms.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

	private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

	private final StudentRepository studentRepository;

	public StudentServiceImpl(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Student> getAllStudents(Pageable pageable) {
		return studentRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Student> getAllStudents() {
		return studentRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Student getStudentById(Long id) {
		return studentRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Student not found with id: {}", id);
					return new ResourceNotFoundException("Student not found with id: " + id);
				});
	}

	@Override
	public Student saveStudent(Student student) {
		Student saved = studentRepository.save(student);
		log.info("Student saved successfully with id: {}", saved.getId());
		return saved;
	}

	@Override
	public Student updateStudent(Long id, Student student) {
		Student existing = getStudentById(id);
		existing.setFirstName(student.getFirstName());
		existing.setLastName(student.getLastName());
		existing.setEmail(student.getEmail());
		existing.setPhone(student.getPhone());
		existing.setAddress(student.getAddress());
		existing.setActive(student.isActive());
		Student updated = studentRepository.save(existing);
		log.info("Student updated successfully with id: {}", updated.getId());
		return updated;
	}

	@Override
	public void deleteStudent(Long id) {
		Student student = getStudentById(id);
		studentRepository.delete(student);
		log.info("Student deleted successfully with id: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public long countStudents() {
		return studentRepository.count();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmailTaken(String email) {
		return studentRepository.existsByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmailTakenByOther(Long id, String email) {
		return studentRepository.existsByEmailAndIdNot(email, id);
	}
}
