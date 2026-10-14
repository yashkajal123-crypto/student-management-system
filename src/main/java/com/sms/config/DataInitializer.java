package com.sms.config;

import com.sms.entity.Course;
import com.sms.entity.Enrollment;
import com.sms.entity.Student;
import com.sms.repository.CourseRepository;
import com.sms.repository.EnrollmentRepository;
import com.sms.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Fills the database with sample data the first time the application
 * starts on an empty schema. Makes it easy to explore the screens
 * right away (and can be deleted safely once real data exists).
 */
@Component
public class DataInitializer implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	private final StudentRepository studentRepository;
	private final CourseRepository courseRepository;
	private final EnrollmentRepository enrollmentRepository;

	public DataInitializer(StudentRepository studentRepository,
			CourseRepository courseRepository,
			EnrollmentRepository enrollmentRepository) {
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
		this.enrollmentRepository = enrollmentRepository;
	}

	@Override
	public void run(String... args) {
		if (studentRepository.count() > 0) {
			return; // already seeded
		}

		log.info("Seeding sample data...");

		// ----- courses -----
		Course cs101 = new Course("Introduction to Computer Science", "CS101", new BigDecimal("3999"), "4 Months");
		cs101.setDescription("A fundamental course covering the basics of algorithms, data structures, and computer architecture. This course is designed for beginners with no prior programming experience.");

		Course wdb = new Course("Web Development Bootcamp", "WDB", new BigDecimal("7999"), "6 Months");
		wdb.setDescription("Build responsive websites and web apps using HTML, CSS, JavaScript and modern frameworks.");

		Course dsa = new Course("Data Structures & Algorithms", "DSA", new BigDecimal("5999"), "5 Months");
		dsa.setDescription("Learn efficient data handling, problem solving patterns and interview focused algorithm practice.");

		Course cld = new Course("Cloud Computing", "CLD", new BigDecimal("4999"), "3 Months");
		cld.setDescription("Infrastructure, services and deployment models on major cloud platforms.");

		Course pdn = new Course("Product Design", "PDN", new BigDecimal("4499"), "3 Months");
		pdn.setDescription("UI/UX fundamentals, design thinking and hands-on practice with industry standard tools.");

		Course dbms = new Course("Database Management Systems", "DBMS", new BigDecimal("4999"), "4 Months");
		dbms.setDescription("SQL, normalization, transactions and real world database design techniques.");

		courseRepository.save(cs101);
		courseRepository.save(wdb);
		courseRepository.save(dsa);
		courseRepository.save(cld);
		courseRepository.save(pdn);
		courseRepository.save(dbms);

		// ----- students -----
		Student john = student("John", "Doe", "john.doe@example.com", "+1 234 567 8900", "123 Main St, Springfield, IL 62704");
		Student jane = student("Jane", "Smith", "jane.smith@example.com", "+1 234 567 8901", "456 Oak Avenue, Austin, TX 73301");
		Student mike = student("Mike", "Johnson", "mike.j@example.com", "+1 234 567 8902", "789 Pine Road, Denver, CO 80201");
		Student emily = student("Emily", "Clark", "emily.clark@example.com", "+1 234 567 8903", "321 Maple Street, Seattle, WA 98101");
		Student alex = student("Alex", "Johnson", "alex.johnson@example.com", "+1 234 567 8904", "654 Cedar Lane, Boston, MA 02101");
		Student priya = student("Priya", "Sharma", "priya.sharma@example.com", "+91 98765 43210", "221B Residency Road, Bhopal, MP 462001");
		Student rahul = student("Rahul", "Verma", "rahul.verma@example.com", "+91 98765 43211", "14 Lake View Colony, Indore, MP 452001");
		Student sarah = student("Sarah", "Wilson", "sarah.wilson@example.com", "+1 234 567 8907", "987 Elm Boulevard, Chicago, IL 60601");

		studentRepository.save(john);
		studentRepository.save(jane);
		studentRepository.save(mike);
		studentRepository.save(emily);
		studentRepository.save(alex);
		studentRepository.save(priya);
		studentRepository.save(rahul);
		studentRepository.save(sarah);

		// ----- enrollments -----
		enroll(john, cs101, 12);
		enroll(john, dsa, 12);
		enroll(jane, wdb, 26);
		enroll(jane, pdn, 26);
		enroll(jane, cld, 9);
		enroll(mike, cs101, 20);
		enroll(mike, dbms, 20);
		enroll(emily, wdb, 6);
		enroll(emily, dsa, 6);
		enroll(emily, cld, 2);
		enroll(alex, cs101, 30);
		enroll(alex, wdb, 30);
		enroll(alex, dsa, 15);
		enroll(alex, dbms, 15);
		enroll(priya, wdb, 4);
		enroll(priya, pdn, 4);
		enroll(rahul, dbms, 8);
		enroll(rahul, cs101, 1);
		enroll(sarah, pdn, 18);

		log.info("Sample data seeded - {} students, {} courses, {} enrollments",
				studentRepository.count(), courseRepository.count(), enrollmentRepository.count());
	}

	private Student student(String firstName, String lastName, String email, String phone, String address) {
		Student s = new Student(firstName, lastName, email);
		s.setPhone(phone);
		s.setAddress(address);
		return s;
	}

	private void enroll(Student student, Course course, int daysAgo) {
		Enrollment e = new Enrollment(student, course, LocalDate.now().minusDays(daysAgo), course.getCourseFee());
		enrollmentRepository.save(e);
	}
}
