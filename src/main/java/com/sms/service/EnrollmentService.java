package com.sms.service;

import com.sms.dto.EnrollmentSummaryDto;
import com.sms.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnrollmentService {

	/**
	 * Enrolls one student into the given courses.
	 *
	 * @return a short success message
	 */
	String enrollStudent(Long studentId, List<Long> courseIds);

	Page<EnrollmentSummaryDto> getEnrollmentSummary(Pageable pageable);

	List<Enrollment> getEnrollmentsByStudent(Long studentId);

	List<Enrollment> getRecentEnrollments(int limit);

	long countEnrollmentsThisMonth();

	String getTopCourseCode();
}
