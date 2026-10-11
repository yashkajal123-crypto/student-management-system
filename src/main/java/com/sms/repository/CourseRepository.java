package com.sms.repository;

import com.sms.entity.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

	Optional<Course> findByCourseCode(String courseCode);

	boolean existsByCourseCode(String courseCode);

	boolean existsByCourseCodeAndIdNot(String courseCode, Long id);

	List<Course> findByActiveTrue();

	/**
	 * Returns the most enrolled courses first.
	 * Each row is [Course, enrollmentCount].
	 */
	@Query("SELECT c, COUNT(e) AS cnt FROM Course c LEFT JOIN c.enrollments e GROUP BY c ORDER BY cnt DESC")
	List<Object[]> findTopCourses(Pageable pageable);
}
