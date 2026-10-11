package com.sms.dto;

import java.math.BigDecimal;

/**
 * Read-only row used on the "Students Enrolled in Courses" screen.
 * One row per student with the number of courses and the total fee.
 */
public class EnrollmentSummaryDto {

	private Long studentId;
	private String studentName;
	private String email;
	private long coursesCount;
	private BigDecimal totalFee;

	public EnrollmentSummaryDto(Long studentId, String studentName, String email, long coursesCount, BigDecimal totalFee) {
		this.studentId = studentId;
		this.studentName = studentName;
		this.email = email;
		this.coursesCount = coursesCount;
		this.totalFee = totalFee;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getCoursesCount() {
		return coursesCount;
	}

	public void setCoursesCount(long coursesCount) {
		this.coursesCount = coursesCount;
	}

	public BigDecimal getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
	}
}
