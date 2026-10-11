package com.sms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Enrollment entity - join table between Student and Course.
 * It records WHEN a student enrolled and the fee that was applicable
 * at that moment (fee snapshot), so later course fee changes do not
 * corrupt old enrollment totals.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = { "student_id", "course_id" }))
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id", nullable = false)
	private Student student;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(name = "enrolled_date", nullable = false)
	private LocalDate enrolledDate;

	@Column(name = "fee", nullable = false, precision = 10, scale = 2)
	private BigDecimal fee;

	public Enrollment() {
	}

	public Enrollment(Student student, Course course, LocalDate enrolledDate, BigDecimal fee) {
		this.student = student;
		this.course = course;
		this.enrolledDate = enrolledDate;
		this.fee = fee;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public LocalDate getEnrolledDate() {
		return enrolledDate;
	}

	public void setEnrolledDate(LocalDate enrolledDate) {
		this.enrolledDate = enrolledDate;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
}
