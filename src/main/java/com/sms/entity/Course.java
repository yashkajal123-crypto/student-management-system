package com.sms.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Course entity - stores the courses offered by the institute.
 * A course can have many enrolled students (see {@link Enrollment}).
 */
@Entity
@Table(name = "courses")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Course name is required")
	@Size(max = 100, message = "Course name must be at most 100 characters")
	@Column(name = "course_name", nullable = false, length = 100)
	private String courseName;

	@NotBlank(message = "Course code is required")
	@Size(max = 20, message = "Course code must be at most 20 characters")
	@Column(name = "course_code", nullable = false, unique = true, length = 20)
	private String courseCode;

	@NotNull(message = "Course fee is required")
	@PositiveOrZero(message = "Course fee cannot be negative")
	@Column(name = "course_fee", nullable = false, precision = 10, scale = 2)
	private BigDecimal courseFee;

	@NotBlank(message = "Duration is required")
	@Size(max = 30, message = "Duration must be at most 30 characters")
	@Column(name = "duration", nullable = false, length = 30)
	private String duration;

	@Size(max = 1000, message = "Description must be at most 1000 characters")
	@Column(name = "description", length = 1000)
	private String description;

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Enrollment> enrollments = new ArrayList<>();

	public Course() {
	}

	public Course(String courseName, String courseCode, BigDecimal courseFee, String duration) {
		this.courseName = courseName;
		this.courseCode = courseCode;
		this.courseFee = courseFee;
		this.duration = duration;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public BigDecimal getCourseFee() {
		return courseFee;
	}

	public void setCourseFee(BigDecimal courseFee) {
		this.courseFee = courseFee;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Enrollment> getEnrollments() {
		return enrollments;
	}

	public void setEnrollments(List<Enrollment> enrollments) {
		this.enrollments = enrollments;
	}
}
