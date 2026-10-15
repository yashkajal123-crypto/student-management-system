# Learning Guide - How to Study This Project

This guide walks through the whole codebase module by module and maps every
feature to the concepts it teaches. Use it together with the source code -
open the files listed under each section while you read.

**Reference playlist:** [Spring Boot Student Management System - Complete Real Project](https://www.youtube.com/playlist?list=PLSIuN2hFKqGLG4VfEc0k-LjJtjW4YdKSo)
(15 videos, ~10 hours total). Each section below names the matching video so
you can watch it and compare with this implementation side by side.

---

## 0. Prerequisites

- Core Java (OOP, collections, generics)
- Basic JDBC / SQL (SELECT, JOIN, GROUP BY)
- HTML + a little CSS
- Spring Boot basics (what is IoC, what is `@Autowired`)

If any of these is weak, brush up first - the project will make 10x more sense.

## 1. Big picture - what the app is

A classic **3-layer web application**:

```
Browser  ->  Controller (URLs, forms)  ->  Service (business rules)  ->  Repository (SQL)  ->  MySQL
                |                            |
            Thymeleaf HTML              logging + transactions
```

Start here:

| File | What to notice |
|------|----------------|
| `pom.xml` | The 6 starters: web, thymeleaf, data-jpa, security, validation + mysql/h2 drivers |
| `StudentManagementSystemApplication.java` | The `@SpringBootApplication` entry point |
| `application.properties` | MySQL connection + JPA settings |
| `application-demo.properties` | Same app on embedded H2 - no MySQL needed |

> **Video 2 - Project Setup from Scratch** matches this section.

## 2. Entities and repositories (the data layer)

Read in this order:

1. `entity/Student.java` - `@Entity`, `@Id`, `@GeneratedValue`, Bean Validation
   annotations (`@NotBlank`, `@Email`), `@OneToMany(mappedBy = ...)`
2. `entity/Course.java` - `BigDecimal` for money, unique business key
   (`course_code`)
3. `entity/Enrollment.java` - the join entity, `@ManyToOne`, `@UniqueConstraint`,
   the **fee snapshot** idea (see [ER-Diagram.md](ER-Diagram.md))
4. `repository/*.java` - `JpaRepository` magic (free CRUD + pagination),
   `@Query` with `JOIN FETCH` (this is how you avoid the **N+1 problem**),
   `existsBy...` derived queries, the native aggregate query for the
   enrollment summary

Things to be able to explain in an interview:

- Why `FetchType.LAZY` + `JOIN FETCH` instead of `EAGER`
- Why `ddl-auto=update` in dev and what `validate` does in production
- What `unique = true` buys you that a UI check does not

## 3. Service layer (business rules + logging)

- `service/impl/StudentServiceImpl.java` - CRUD + `@Transactional`,
  `ResourceNotFoundException` on missing ids, SLF4J logging
  (`log.info` on writes, `log.warn` on missing data)
- `service/impl/CourseServiceImpl.java` - same pattern, plus "top course" query
- `service/impl/EnrollmentServiceImpl.java` - the interesting one:
  multi-course enrollment, duplicate detection, fee snapshot, aggregate mapping
  into `EnrollmentSummaryDto`

Rule of thumb to remember: **controllers stay thin, services hold the rules**.

## 4. Global exception handling

- `exception/ResourceNotFoundException.java` - a `RuntimeException` for missing ids
- `exception/GlobalExceptionHandler.java` - `@ControllerAdvice` + `@ExceptionHandler`
  returning the `error/404` and `error/500` views with the right HTTP status

Try it: open `/students/9999` while logged in -> friendly 404 page + a WARN
line in the log, no stack trace in the browser.

> **Video 3 - Global Exception Handling** matches this section.

## 5. Spring Security + login page

- `config/SecurityConfig.java` - `SecurityFilterChain`, `formLogin`, route rules
  (`/css/**` public, everything else authenticated), BCrypt `PasswordEncoder`,
  in-memory admin user
- `controller/LoginController.java` + `templates/login.html` - the custom login
  form Spring Security posts to (`/login` with `username`/`password` fields)

Things to notice:

- Why the logout link is a plain GET (`logoutRequestMatcher`)
- Where the password hash lives (`BCryptPasswordEncoder`, never plain text)
- How CSRF tokens are auto-added to every `th:action` form

> **Videos 4 & 5 - Security Implementation + Login Page with Thymeleaf** match
> this section.

## 6. Student module (CRUD + validation + pagination)

Follow one full request, e.g. "Add Student":

```
GET /students/new     -> StudentController.showAddForm() -> add-student.html
POST /students        -> @Valid binding -> BindingResult errors? re-show form
                       -> StudentServiceImpl.saveStudent() -> redirect:/students
GET /students         -> paginated list + flash message
```

Files: `StudentController.java`, `templates/add-student.html`,
`templates/edit-student.html`, `templates/view-student.html`,
`templates/students.html`, `util/PaginationUtil.java`.

Key concepts: `@Valid` + `BindingResult`, `@ModelAttribute`, `RedirectAttributes`
(flash messages), `Pageable`/`Page`, the pagination bar.

> **Videos 6, 9, 10, 11** (student CRUD + student pagination + update) match
> this section.

## 7. Course module

Same shape as the student module - once you understand one, the second is quick.
Read `CourseController.java` and the four course templates. Notice the
duplicate `course_code` check (`isCodeTakenByOther` on update).

> **Videos 7 & 8** (course listing pagination + course update) match this
> section.

## 8. Enrollment module (the most "real world" part)

- `EnrollmentController.java` - three screens (enroll form, enrolled list,
  per-student details)
- `templates/enroll-course.html` - multi-select checkboxes + small vanilla JS
  that recomputes the fee summary live
- `service/impl/EnrollmentServiceImpl.enrollStudent()` - loop with
  duplicate skip + fee snapshot
- The native `GROUP BY` query behind "Students Enrolled in Courses"

Discuss-worthy details: why the summary query is `nativeQuery = true`,
why `countQuery` is needed for correct page totals, why `fee` is a snapshot.

> **Videos 12, 13, 14** (enrollment part 1 & 2 + enrollment list with
> pagination) match this section.

## 9. Dashboard

`DashboardController.java` feeds 4 stats + the recent registrations table
(`templates/dashboard.html`). Pure composition of the service methods you
already know - a nice "glue code" reading session.

> **Video 15 - Build Your First Spring Boot Dashboard** matches this section.

## 10. Logging

`logback-spring.xml` - console appender + rolling file appender
(daily roll, 10 MB max, 30 days history). All application logs go through
SLF4J (`LoggerFactory.getLogger(...)`), never `System.out.println`.

Note: on Spring Boot 3.x the old `SizeAndTimeBasedFNATP` class (from the
course resources) was renamed to `SizeAndTimeBasedRollingPolicy` - the file
in this project already uses the new name.

---

## Suggested 7-day plan

| Day | What to do |
|-----|------------|
| 1 | Run the app (demo profile), click every screen, read README + SRS |
| 2 | Entities + repositories; draw the ER diagram from memory and compare |
| 3 | Services + exception handling; add a temporary `System.out` and replace it with a logger |
| 4 | Security: change the admin password, try accessing `/students` logged out |
| 5 | Student + course modules; break a form on purpose and watch validation |
| 6 | Enrollment module; trace `enrollStudent()` line by line; delete a course with enrollments and see what happens |
| 7 | Rebuild one module **from scratch** on a new branch without looking (the fastest way to truly own it) |

## Exercises to try (great interview talking points)

1. Add a **search box** on the students screen (`findByFirstNameContainingIgnoreCase`).
2. Add `@DateTimeFormat` + a date-of-birth field to the student form.
3. Replace the in-memory user with a `users` table + `UserDetailsService`.
4. Write a `@SpringBootTest` that enrolls a student and asserts the total fee.
5. Add a "Remove course" button on the enrollment details screen.
6. Package with Docker (`eclipse-temurin` + a `mysql:8` compose file).

## Where to look when you forget something

| I forgot... | Look at |
|-------------|---------|
| How a form is bound | `add-student.html` + `StudentController.saveStudent` |
| How pagination works | `PaginationUtil` + `students.html` bottom |
| How the 404 page appears | `GlobalExceptionHandler` + `templates/error/404.html` |
| How login works | `SecurityConfig` + `login.html` |
| How enrollment totals are computed | `EnrollmentRepository.findEnrollmentSummary` |
| The database shape | `docs/ER-Diagram.md` |
