package com.sms.service.impl;

import com.sms.entity.Course;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.CourseRepository;
import com.sms.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

	private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

	private final CourseRepository courseRepository;

	public CourseServiceImpl(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Course> getAllCourses(Pageable pageable) {
		return courseRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Course> getAllCourses() {
		return courseRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Course> getActiveCourses() {
		return courseRepository.findByActiveTrue();
	}

	@Override
	@Transactional(readOnly = true)
	public Course getCourseById(Long id) {
		return courseRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Course not found with id: {}", id);
					return new ResourceNotFoundException("Course not found with id: " + id);
				});
	}

	@Override
	public Course saveCourse(Course course) {
		Course saved = courseRepository.save(course);
		log.info("Course saved successfully with id: {}", saved.getId());
		return saved;
	}

	@Override
	public Course updateCourse(Long id, Course course) {
		Course existing = getCourseById(id);
		existing.setCourseName(course.getCourseName());
		existing.setCourseCode(course.getCourseCode());
		existing.setCourseFee(course.getCourseFee());
		existing.setDuration(course.getDuration());
		existing.setDescription(course.getDescription());
		existing.setActive(course.isActive());
		Course updated = courseRepository.save(existing);
		log.info("Course updated successfully with id: {}", updated.getId());
		return updated;
	}

	@Override
	public void deleteCourse(Long id) {
		Course course = getCourseById(id);
		courseRepository.delete(course);
		log.info("Course deleted successfully with id: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public long countCourses() {
		return courseRepository.count();
	}

	@Override
	@Transactional(readOnly = true)
	public String getTopCourseCode() {
		List<Object[]> rows = courseRepository.findTopCourses(PageRequest.of(0, 1));
		if (rows.isEmpty() || rows.get(0)[0] == null) {
			return "N/A";
		}
		Course course = (Course) rows.get(0)[0];
		return course.getCourseCode();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isCodeTaken(String courseCode) {
		return courseRepository.existsByCourseCode(courseCode);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isCodeTakenByOther(Long id, String courseCode) {
		return courseRepository.existsByCourseCodeAndIdNot(courseCode, id);
	}
}
