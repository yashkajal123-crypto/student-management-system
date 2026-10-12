package com.sms.service.impl;

import com.sms.dto.EnrollmentSummaryDto;
import com.sms.entity.Course;
import com.sms.entity.Enrollment;
import com.sms.entity.Student;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.EnrollmentRepository;
import com.sms.service.CourseService;
import com.sms.service.EnrollmentService;
import com.sms.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

	private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

	private final EnrollmentRepository enrollmentRepository;
	private final StudentService studentService;
	private final CourseService courseService;

	public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
			StudentService studentService, CourseService courseService) {
		this.enrollmentRepository = enrollmentRepository;
		this.studentService = studentService;
		this.courseService = courseService;
	}

	@Override
	public String enrollStudent(Long studentId, List<Long> courseIds) {
		Student student = studentService.getStudentById(studentId);

		int newCount = 0;
		int skipped = 0;

		for (Long courseId : courseIds) {
			Course course = courseService.getCourseById(courseId);

			if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
				skipped++;
				continue;
			}

			// fee snapshot - taken from the course at the time of enrollment
			Enrollment enrollment = new Enrollment(student, course, LocalDate.now(), course.getCourseFee());
			enrollmentRepository.save(enrollment);
			newCount++;
		}

		log.info("Enrollment done for student {} -> {} new course(s), {} skipped", studentId, newCount, skipped);

		if (newCount == 0) {
			return "Student is already enrolled in the selected course(s).";
		}
		if (skipped > 0) {
			return newCount + " course(s) enrolled successfully. " + skipped
					+ " course(s) skipped because the student was already enrolled.";
		}
		return "Student enrolled in " + newCount + " course(s) successfully.";
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EnrollmentSummaryDto> getEnrollmentSummary(Pageable pageable) {
		Page<Object[]> rows = enrollmentRepository.findEnrollmentSummary(pageable);
		List<EnrollmentSummaryDto> content = new ArrayList<>();
		for (Object[] row : rows.getContent()) {
			Long studentId = ((Number) row[0]).longValue();
			String name = row[1] + " " + row[2];
			String email = (String) row[3];
			long coursesCount = ((Number) row[4]).longValue();
			BigDecimal totalFee = (BigDecimal) row[5];
			content.add(new EnrollmentSummaryDto(studentId, name, email, coursesCount, totalFee));
		}
		return new org.springframework.data.domain.PageImpl<>(content, pageable, rows.getTotalElements());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
		// make sure the student really exists (shows the 404 page otherwise)
		studentService.getStudentById(studentId);
		return enrollmentRepository.findByStudentIdWithDetails(studentId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Enrollment> getRecentEnrollments(int limit) {
		return enrollmentRepository.findRecentEnrollments(PageRequest.of(0, limit));
	}

	@Override
	@Transactional(readOnly = true)
	public long countEnrollmentsThisMonth() {
		LocalDate today = LocalDate.now();
		LocalDate start = today.withDayOfMonth(1);
		LocalDate end = today.withDayOfMonth(today.lengthOfMonth());
		return enrollmentRepository.countByEnrolledDateBetween(start, end);
	}

	@Override
	@Transactional(readOnly = true)
	public String getTopCourseCode() {
		List<Object[]> rows = enrollmentRepository.findTopCourseCodes(PageRequest.of(0, 1));
		if (rows.isEmpty() || rows.get(0)[0] == null) {
			return "N/A";
		}
		return (String) rows.get(0)[0];
	}
}
