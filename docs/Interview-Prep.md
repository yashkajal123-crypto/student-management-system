# Interview Prep - Talking About This Project

Quick-reference answers for the Accenture (or any) technical interview.
Practice saying the "elevator pitch" out loud until it flows naturally.

---

## 1. Elevator pitch (30 seconds)

> "It's a Student Management System I built with Spring Boot, Thymeleaf and
> MySQL. An admin logs in through Spring Security and manages students and
> courses - full CRUD with validation and pagination - and can enroll a
> student into multiple courses. The enrollment module keeps a fee snapshot
> per enrollment and shows per-student totals. There's a dashboard with live
> stats, global exception handling with custom 404/500 pages, and file-based
> logging. The code follows a layered architecture: controller -> service ->
> repository."

## 2. Walkthrough (2 minutes) - follow one request

"Let me walk you through what happens when I add a student:"
1. Browser posts the form to `/students` (with the CSRF token Spring Security injects).
2. `StudentController.saveStudent` receives it with `@Valid` - Bean Validation
   checks `@NotBlank`, `@Email` etc. on the entity.
3. `BindingResult` collects errors; if any, the form re-renders with messages
   next to the fields and nothing is saved.
4. The service checks the duplicate-email rule, then saves through
   `StudentRepository` (Spring Data JPA) inside a transaction.
5. A flash message is added and we redirect to the list (`POST-redirect-GET`)
   so a page refresh never re-submits the form."

## 3. Likely questions & crisp answers

**Q: Why Spring Boot over plain Spring?**
Auto-configuration, embedded Tomcat, starters instead of 50 XML lines, external
config via profiles. Same Spring core underneath - I still wrote explicit
`SecurityFilterChain` and `@Query` where it matters.

**Q: Explain the architecture / layers.**
Controller (HTTP, binding, view names) -> Service (business rules,
transactions, logging) -> Repository (data access). Entities cross the layers;
DTO only where a screen needs a custom projection (`EnrollmentSummaryDto`).
Benefits: testable, swappable persistence, thin controllers.

**Q: What is the difference between `@Controller` and `@RestController`?**
`@Controller` returns view names resolved by Thymeleaf. `@RestController` =
`@Controller` + `@ResponseBody`, returns JSON. This app is server-rendered so
it uses `@Controller`.

**Q: How does form validation work?**
JSR-380 annotations on the entity (`@NotBlank`, `@Email`, `@Size`,
`@PositiveOrZero`) + `@Valid` in the controller + `BindingResult` for errors +
`th:errors` in the template. Hibernate Validator is the runtime implementation.
I also add cross-field rules in the service layer (duplicate email / code).

**Q: How is pagination implemented?**
`Pageable`/`Page` from Spring Data. `PageRequest.of(page, size, sort)` is passed
to `findAll`. The DB applies LIMIT/OFFSET, so only one page is in memory. The
Thymeleaf bar shows a 5-page window (`PaginationUtil`) and the total pages come
from `Page.getTotalPages()`.

**Q: What is the N+1 problem and how did you avoid it?**
Loading a list of N enrollments and then lazily touching `student`/`course`
fires 1 + 2N queries. I used `JOIN FETCH` in `@Query` so each list is one SQL
statement. Associations are `FetchType.LAZY` on purpose.

**Q: How does Spring Security work here?**
A `SecurityFilterChain` protects everything except `/login`, `/css/**`, `/js/**`
and `/error/**`. Form login posts to `/login`; `UserDetailsService` (in-memory)
returns a BCrypt-hashed password; on success we land on `/dashboard`. Logout is
mapped as GET to keep the design's plain links working. CSRF is on by default -
Thymeleaf auto-injects the token into every `th:action` form.

**Q: Why BCrypt and not MD5/SHA for passwords?**
BCrypt is a slow, salted adaptive hash designed for passwords. Fast hashes are
rainbow-table friendly; BCrypt's work factor can be increased as hardware
improves.

**Q: Explain `@Transactional`.**
Spring wraps the method in a DB transaction - all-or-nothing. On unchecked
exceptions it rolls back. `readOnly = true` on query methods is an optimization
hint for Hibernate/driver. Here service methods carry the annotation (the right
place), not controllers.

**Q: `@ManyToMany` vs a join entity - why did you pick the join entity?**
A pure `@ManyToMany` join table can only hold the two FKs. We needed
`enrolled_date` and a fee snapshot on every relationship, so the join table is a
full `Enrollment` entity (`@ManyToOne` both ways) - the standard answer when the
relationship itself has attributes.

**Q: Global exception handling?**
`@ControllerAdvice` with `@ExceptionHandler` methods: `ResourceNotFoundException`
-> 404 view + HTTP 404, anything else -> 500 view + HTTP 500. Logs go WARN/ERROR
with context. Users never see a whitelabel stack trace.

**Q: What is `ddl-auto`? What do you use and why?**
`update` in dev - Hibernate syncs the schema without dropping data.
`validate` for production (migrations own the schema, e.g. Flyway),
`create-drop` only for throwaway tests.

**Q: Why a service layer when repositories already have CRUD?**
Repositories don't know business rules (duplicate email, fee snapshot,
"already enrolled"). Services keep controllers thin and hold transactions +
logging in one place. You can also swap the data source without touching web code.

**Q: How do you prevent SQL injection?**
All queries go through JPA/Hibernate with prepared statements - user input is
never concatenated into SQL. (Never write string-built SQL.)

**Q: MySQL vs H2 - what's the deal?**
MySQL is the real database (default profile). H2 is only a convenience profile
(`demo`) so the app runs without installing a DB, with `MODE=MySQL` so behavior
stays close. Same code, different datasource property.

**Q: What does the fee snapshot solve?**
If a course fee changes later, historical enrollment totals must not change.
Copying the fee at enrollment time keeps old reports correct - an accounting
principle applied to the schema.

**Q: How is logging set up?**
SLF4J + Logback via `logback-spring.xml`: console appender for dev and a rolling
file appender (daily roll, 10 MB size cap, 30-day retention). Writes are logged
at INFO, missing data at WARN, unexpected errors at ERROR with stack trace.

**Q: POST-redirect-GET - why?**
After a successful POST we redirect so refreshing the page re-runs the GET, not
the POST. Prevents duplicate students from double refresh. Flash attributes
carry the success message across the redirect.

**Q: What would you improve next?**
Role-based access (admin vs staff), search/filters, Flyway migrations, REST API
for a mobile client, tests around the enrollment rules, Docker compose, and
moving the admin user to a `users` table.

**Q: What was the hardest part?**
The enrollment summary with pagination - a `GROUP BY` query needs a separate
`countQuery` so Spring Data can compute total pages. That, and keeping the fee
snapshot semantics consistent in totals.

## 4. Things NOT to say

- "AI wrote it for me." (You built and understand every file - that's the point of this guide.)
- "I copied the tutorial." Say: "I followed a structured learning path and then rebuilt it" - true and professional.
- Do not claim production scale ("millions of users"). Keep claims realistic: "built to production conventions at small scale."

## 5. Whiteboard checklist

If asked to draw the project, draw in this order:
1. Browser box -> Tomcat/Spring Boot box
2. 3 layers inside (Controller / Service / Repository)
3. 3 tables (students, courses, enrollments) with the two 1:N lines
4. Side note boxes: Security (login), @ControllerAdvice (404/500), Logback
