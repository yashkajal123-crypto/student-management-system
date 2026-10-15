# Software Requirements Specification (SRS)

**Project:** Student Management System (SMS)
**Version:** 1.0
**Prepared by:** Developer
**Date:** October 2026

---

## 1. Introduction

### 1.1 Purpose
This SRS describes the functional and non-functional requirements of the
Student Management System - a web application used by training institutes /
colleges to manage students, courses and course enrollments from one
admin panel.

### 1.2 Scope
The system is a server-rendered web application (Spring Boot + Thymeleaf)
backed by a relational database (MySQL). It covers:

- Secure admin login
- Dashboard with live statistics
- Student master data (create, read, update, delete)
- Course master data (create, read, update, delete)
- Enrolling a student into one or more courses
- Enrollment reports per student (with fee totals)
- Pagination on every list screen
- Form validation and friendly error pages (404 / 500)
- Application logging (console + rolling file)

Out of scope for v1.0: student self-service portal, online fee payment,
email notifications, multi-role (teacher/student) access.

### 1.3 Definitions
| Term | Meaning |
|------|---------|
| SMS | Student Management System (this application) |
| Admin | The logged-in staff user who manages all data |
| Enrollment | A record linking one student to one course with date + fee |
| Fee snapshot | The course fee copied into the enrollment at join time |

## 2. Overall Description

### 2.1 Product Perspective
Standalone web application. Browser (client) <-> Spring Boot app (Tomcat)
<-> MySQL database. All screens are server-rendered with Thymeleaf and
styled with Bootstrap 5.

### 2.2 User Characteristics
Single user type: **Admin**. Basic computer and browser knowledge is enough.
No technical knowledge required.

### 2.3 Operating Environment
- Java 17+, Spring Boot 3.x
- MySQL 8.x (or the embedded H2 database for demos)
- Any modern browser (Chrome, Edge, Firefox)
- Windows / Linux / macOS

### 2.4 Constraints
- One admin role in v1.0 (kept simple on purpose)
- Session-based login (Spring Security form login)
- Server-side rendering only - no separate SPA frontend

### 2.5 Assumptions
- The admin manages a single institute
- Course fees are entered in INR as plain numbers (e.g. 3999)
- Deleting a student also removes that student's enrollments (cascade)

## 3. Functional Requirements

### FR1 - Authentication
| ID | Requirement |
|----|-------------|
| FR1.1 | System shall show a login page at `/login` for anonymous users. |
| FR1.2 | System shall authenticate email + password against Spring Security. |
| FR1.3 | On success the user is redirected to the dashboard. |
| FR1.4 | On failure the login page reloads with an error message. |
| FR1.5 | Logout shall end the session and return to the login page. |
| FR1.6 | All screens except login/static assets require authentication. |

### FR2 - Dashboard
| ID | Requirement |
|----|-------------|
| FR2.1 | Show total number of students. |
| FR2.2 | Show total number of courses. |
| FR2.3 | Show the top performing course (course code with most enrollments). |
| FR2.4 | Show number of students enrolled in the current month. |
| FR2.5 | Show a "Recent Student Registrations" table (latest 5 enrollments). |

### FR3 - Student Management
| ID | Requirement |
|----|-------------|
| FR3.1 | List students in a paginated table (id, name, email, phone). |
| FR3.2 | Add a new student (first name, last name, email, phone, address). |
| FR3.3 | Validate form input (required names, valid email) and show field errors. |
| FR3.4 | Prevent duplicate email addresses. |
| FR3.5 | View full details of one student. |
| FR3.6 | Edit an existing student (including active flag). |
| FR3.7 | Delete a student (with confirmation) together with their enrollments. |

### FR4 - Course Management
| ID | Requirement |
|----|-------------|
| FR4.1 | List courses in a paginated table (id, name, code, fee, duration). |
| FR4.2 | Add a new course (name, code, fee, duration, description). |
| FR4.3 | Validate form input and show field errors. |
| FR4.4 | Prevent duplicate course codes. |
| FR4.5 | View full details of one course. |
| FR4.6 | Edit an existing course (including active flag). |
| FR4.7 | Delete a course (with confirmation). |

### FR5 - Enrollment
| ID | Requirement |
|----|-------------|
| FR5.1 | On the enroll screen show all active courses with fee + multi-select. |
| FR5.2 | Let the admin pick exactly one student from a dropdown. |
| FR5.3 | Show a live summary: number of selected courses and total fee. |
| FR5.4 | Save one enrollment row per selected course with date + fee snapshot. |
| FR5.5 | Skip (not duplicate) courses the student already joined and inform the admin. |
| FR5.6 | List all enrolled students with course count + total fee (paginated). |
| FR5.7 | Show per-student enrollment details (enrolled courses, dates, fees, totals). |

### FR6 - Pagination
| ID | Requirement |
|----|-------------|
| FR6.1 | Every list screen accepts `page` and `size` request parameters. |
| FR6.2 | Show Previous / page numbers / Next controls (hidden when only one page). |
| FR6.3 | Keep the current page highlighted and disable Previous/Next at the edges. |

### FR7 - Error Handling
| ID | Requirement |
|----|-------------|
| FR7.1 | Unknown student/course ids show a friendly 404 page. |
| FR7.2 | Unexpected server errors show a friendly 500 page. |
| FR7.3 | All handled exceptions are logged with context. |

### FR8 - Logging
| ID | Requirement |
|----|-------------|
| FR8.1 | Log to console and to a rolling file (10 MB / 30 days retention). |
| FR8.2 | Log save/update/delete operations at INFO level. |
| FR8.3 | Log unknown-id warnings at WARN and unexpected errors at ERROR. |

## 4. Non-Functional Requirements

| Category | Requirement |
|----------|-------------|
| Performance | Common list screens load in well under 2 seconds with thousands of rows (queries are paginated at the database, `JOIN FETCH` avoids N+1). |
| Security | All screens behind form login; passwords BCrypt-hashed; CSRF protection enabled (Spring Security default); SQL injection impossible (JPA prepared statements). |
| Usability | Responsive Bootstrap 5 UI; tables become label/value cards on phones; validation errors shown next to the field. |
| Reliability | Database constraints (unique email/code, unique student+course) protect data even under race conditions. |
| Maintainability | Classic layered architecture (controller -> service -> repository); one package per responsibility; logging instead of `System.out`. |
| Portability | Runs on Windows/Linux/macOS; database switchable via Spring profiles (MySQL default, H2 demo). |

## 5. Use Cases (summary)

| Actor | Use case |
|-------|----------|
| Admin | Login / Logout |
| Admin | View dashboard statistics |
| Admin | Add / edit / view / delete student |
| Admin | Add / edit / view / delete course |
| Admin | Enroll student to one or more courses |
| Admin | View enrolled students list |
| Admin | View enrollment details of a student |

## 6. Data Model

See [ER-Diagram.md](ER-Diagram.md) for the full diagram and DDL.
Three tables: `students`, `courses`, `enrollments` (associative entity
with `enrolled_date` + fee snapshot, unique on student+course).

## 7. Architecture

```
Browser (Bootstrap 5 UI)
   |  HTTP
Spring MVC Controllers  (com.sms.controller)
   |  calls
Service layer           (com.sms.service + impl)   <- business rules, logging, @Transactional
   |  uses
Spring Data JPA         (com.sms.repository)       <- queries, pagination
   |  JDBC
MySQL / H2              (students, courses, enrollments)
```

Cross-cutting pieces:
- `SecurityConfig` - form login, route protection
- `GlobalExceptionHandler` - `@ControllerAdvice` -> 404 / 500 pages
- `logback-spring.xml` - console + rolling file appenders
- `DataInitializer` - seeds sample data on an empty database

## 8. UI Description

| Screen | Route | Description |
|--------|-------|-------------|
| Login | `/login` | Centered card on gradient background |
| Dashboard | `/dashboard` | 4 stat cards + recent registrations table |
| Students | `/students` | Paginated table + Add button + actions |
| Add / Edit Student | `/students/new`, `/students/{id}/edit` | Validated form |
| View Student | `/students/{id}` | Read-only detail card |
| Courses | `/courses` | Paginated table + Add button + actions |
| Add / Edit Course | `/courses/new`, `/courses/{id}/edit` | Validated form |
| View Course | `/courses/{id}` | Read-only detail card |
| Enroll to Course | `/enrollments/new` | Course multi-select + student picker + live fee summary |
| Enrolled Students | `/enrollments` | Paginated list with course count + total fee |
| Enrollment Details | `/enrollments/{studentId}` | Student info + enrolled courses |
| Error pages | 404 / 500 | Friendly full-screen messages |

## 9. Acceptance Criteria

1. Fresh install works end-to-end following the README (MySQL or demo profile).
2. Anonymous users are always redirected to `/login`.
3. All functional requirements FR1-FR8 pass a manual test walkthrough.
4. Duplicate email / course code / enrollment attempts are rejected with a clear message.
5. Invalid forms do not save anything and show field-level errors.
6. Unknown ids show the 404 page (HTTP status 404), not a stack trace.

## 10. Future Scope

- Role-based access (ADMIN / STAFF / STUDENT self-service)
- Email confirmations on enrollment
- Fee payment tracking and receipts (PDF export)
- Search + filter on student/course lists
- REST API layer (for a mobile app)
- Docker compose setup for one-command deployments
