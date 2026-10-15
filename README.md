# Student Management System

A complete web application to manage **students**, **courses** and **course
enrollments** for a training institute / college. Built with **Spring Boot**,
**Thymeleaf** and **MySQL** as a hands-on, industry-style project.

![Java](https://img.shields.io/badge/Java-17-e76f00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6db33f?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479a1?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-005f0f?style=for-the-badge&logo=spring&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952b3?style=for-the-badge&logo=bootstrap&logoColor=white)

---

## Live Demo

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/yashkajal123/student-management-system)

Click the button above (or follow [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md)) to get a
permanent public URL in ~5 minutes - free Render plan, no credit card.

> Demo URL: `https://student-management-system-xxxx.onrender.com` *(update this line after deploying)*
> Login: `admin@sms.com` / `admin123`

## Features

- **Secure admin login** - Spring Security form login with BCrypt-hashed password
- **Dashboard** - total students, total courses, top performing course,
  enrollments this month, recent registrations
- **Student management** - add / view / edit / delete with form validation
  (duplicate email protection)
- **Course management** - add / view / edit / delete with validation
  (duplicate course code protection)
- **Course enrollment** - enroll one student into multiple courses at once with
  a live fee summary (multi-select + running total)
- **Enrollment reports** - "Students Enrolled in Courses" list with course
  counts and total fees, plus per-student enrollment details
- **Pagination** on every list screen (Spring Data `Pageable`)
- **Global exception handling** - friendly 404 / 500 screens, no whitelabel errors
- **Logging** - SLF4J + Logback, console + rolling file (daily, 10 MB, 30 days)
- **Responsive UI** - Bootstrap 5 + custom CSS, tables turn into cards on phones

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.3 (Spring MVC, Spring Data JPA, Spring Security) |
| View | Thymeleaf + Bootstrap 5 + Bootstrap Icons |
| Database | MySQL 8 (default) / H2 embedded (demo profile) |
| Build | Maven |
| Testing | JUnit 5, Spring Boot Test |

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+ (or use the included `./mvnw` wrapper - no Maven install needed)
- MySQL 8 (skip this with the demo profile)

### 1. Create the database (MySQL)

```sql
CREATE DATABASE student_management_db;
```

Check the username/password in `src/main/resources/application.properties`
(defaults: `root` / `root`) and change them to match your machine.

### 2. Run

```bash
# with MySQL (default)
mvn spring-boot:run

# without MySQL - embedded H2 demo database
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

Open <http://localhost:8080> and log in.

### Default login

| Email | Password |
|-------|----------|
| `admin@sms.com` | `admin123` |

> Change this before deploying anywhere public.

On the first start the app seeds some sample students, courses and enrollments
so every screen has data to show. It is a single class
(`config/DataInitializer.java`) - delete it once you add your own data.

## Project Structure

```
student-management-system/
├── pom.xml
├── docs/
│   ├── SRS.md                 # software requirements specification
│   ├── ER-Diagram.md / .svg   # database design
│   ├── Learning-Guide.md      # module by module study guide
│   └── Interview-Prep.md      # Q&A for interviews
└── src/main/
    ├── java/com/sms/
    │   ├── config/            # SecurityConfig, DataInitializer
    │   ├── controller/        # Dashboard, Student, Course, Enrollment, Login
    │   ├── dto/               # EnrollmentSummaryDto
    │   ├── entity/            # Student, Course, Enrollment
    │   ├── exception/         # ResourceNotFoundException, GlobalExceptionHandler
    │   ├── repository/        # Spring Data JPA repositories
    │   ├── service/           # business logic (impl package)
    │   └── util/              # PaginationUtil
    └── resources/
        ├── application.properties            # MySQL (default)
        ├── application-demo.properties       # H2 (demo profile)
        ├── logback-spring.xml                # logging config
        ├── static/css/style.css              # custom styles
        └── templates/                        # Thymeleaf pages (15 screens)
```

## Documentation

| Document | What's inside |
|----------|---------------|
| [docs/SRS.md](docs/SRS.md) | Full requirements spec (functional + non-functional) |
| [docs/ER-Diagram.md](docs/ER-Diagram.md) | ER diagram, relationships, DDL |
| [docs/Learning-Guide.md](docs/Learning-Guide.md) | How to study this project module by module |
| [docs/Interview-Prep.md](docs/Interview-Prep.md) | Interview questions & answers about this project |
| [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) | How to put this app online (Render / Railway / free VM) |

## Main Screens

| Screen | URL |
|--------|-----|
| Login | `/login` |
| Dashboard | `/dashboard` |
| Students (paginated list) | `/students` |
| Add / Edit / View student | `/students/new`, `/students/{id}/edit`, `/students/{id}` |
| Courses (paginated list) | `/courses` |
| Add / Edit / View course | `/courses/new`, `/courses/{id}/edit`, `/courses/{id}` |
| Enroll student to courses | `/enrollments/new` |
| Students enrolled in courses | `/enrollments` |
| Enrollment details | `/enrollments/{studentId}` |

## What I Learned Building This

- Designing a small database (3 tables, one associative entity with a fee
  snapshot) and mapping it cleanly with JPA
- Layered architecture and why the service layer exists
- Form validation end to end (annotations -> BindingResult -> Thymeleaf errors)
- Spring Security filter chains, BCrypt, CSRF, form login flows
- Pagination with Spring Data (`Pageable`) and building a pagination bar
- The N+1 problem and fixing it with `JOIN FETCH`
- `@ControllerAdvice` for global error pages instead of whitelabel errors
- Real logging setup (Logback rolling files) instead of `System.out.println`

## Roadmap ideas

- [ ] Search and filters on student/course lists
- [ ] Database migrations with Flyway
- [ ] Role-based access (ADMIN / STAFF)
- [ ] Email confirmation on enrollment
- [ ] Docker compose (app + MySQL)
- [ ] REST API version for a mobile app

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE).
