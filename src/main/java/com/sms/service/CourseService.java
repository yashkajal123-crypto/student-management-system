package com.sms.service;

import com.sms.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {

	Page<Course> getAllCourses(Pageable pageable);

	List<Course> getAllCourses();

	List<Course> getActiveCourses();

	Course getCourseById(Long id);

	Course saveCourse(Course course);

	Course updateCourse(Long id, Course course);

	void deleteCourse(Long id);

	long countCourses();

	String getTopCourseCode();

	boolean isCodeTaken(String courseCode);

	boolean isCodeTakenByOther(Long id, String courseCode);
}
