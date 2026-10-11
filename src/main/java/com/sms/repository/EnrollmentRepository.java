package com.sms.repository;

import com.sms.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

	/**
	 * Loads enrollments of one student together with student + course
	 * in a single query (JOIN FETCH avoids the N+1 problem).
	 */
	@Query("SELECT e FROM Enrollment e JOIN FETCH e.student s JOIN FETCH e.course c WHERE s.id = :studentId")
	List<Enrollment> findByStudentIdWithDetails(@Param("studentId") Long studentId);

	/**
	 * Latest enrollments (used on the dashboard).
	 */
	@Query("SELECT e FROM Enrollment e JOIN FETCH e.student s JOIN FETCH e.course c ORDER BY e.enrolledDate DESC, e.id DESC")
	List<Enrollment> findRecentEnrollments(Pageable pageable);

	/**
	 * One row per enrolled student: [studentId, firstName, lastName, email, coursesCount, totalFee].
	 * The separate countQuery lets Spring Data build the page total correctly.
	 */
	@Query(value = "SELECT s.id, s.first_name, s.last_name, s.email, COUNT(e.id) AS courses_count, COALESCE(SUM(e.fee), 0) AS total_fee "
			+ "FROM students s JOIN enrollments e ON e.student_id = s.id "
			+ "GROUP BY s.id, s.first_name, s.last_name, s.email "
			+ "ORDER BY s.first_name, s.last_name",
			countQuery = "SELECT COUNT(DISTINCT e.student_id) FROM enrollments e",
			nativeQuery = true)
	Page<Object[]> findEnrollmentSummary(Pageable pageable);

	long countByEnrolledDateBetween(LocalDate start, LocalDate end);

	/**
	 * Course code with the highest number of enrollments.
	 * Each row is [courseCode, enrollmentCount].
	 */
	@Query("SELECT c.courseCode, COUNT(e) FROM Course c LEFT JOIN c.enrollments e GROUP BY c.courseCode ORDER BY COUNT(e) DESC")
	List<Object[]> findTopCourseCodes(Pageable pageable);
}
