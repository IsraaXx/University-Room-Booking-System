# 🎓 University Room Booking System

The **University Room Booking System (Back-End)** is a secure, modular, and testable RESTful API built with **Spring Boot** to manage classroom and lab reservations.  
It supports browsing room availability, booking date/time slots, admin approval workflows, cancellations, history views, and robust role-based access control with JWT.

---

📌 **About the Project**  
This project was developed as part of the **Banque Misr Summer Training (Back-End Track)**.  
It was a **team project** aimed at practicing and strengthening hands-on skills in **Spring Boot** and related technologies.  
Throughout the project, we applied many core concepts such as:
- RESTful API design  
- Authentication & Authorization with JWT  
- Role-based access control (Student, Faculty, Admin)  
- DTO validation & exception handling  
- JPA/Hibernate for database persistence  
- Unit & Integration testing  
- Logging & error handling  

The goal was to simulate a real-world backend system while consolidating the knowledge gained during training.

---

## 👥 Users & Roles

- **Student** → Authenticated user allowed to search availability, request bookings (for student activities), view/cancel own bookings.
- **Faculty Member** → Authenticated user allowed to request bookings (for lectures or departmental events), view/cancel own bookings.
- **Admin** → Manages rooms, schedules, booking policies; approves/rejects bookings.

---

## 📌 Requirements & Features

### 1. Project Setup
- Spring Boot project initialized with:  
  `Spring Web`, `Validation`, `Spring Data JPA`, `Security`, `JWT`, `Lombok`, `MySQL`.

### 2. Configuration & Profiles
- `application.yml` for base config.  
- Environment-specific profiles: `application-dev.yml`, `application-prod.yml`.

### 3. Domain Model (Entities & Relationships)
- Entities: `User`, `Role`, `Building`, `Room`, `RoomFeature`, `Booking`, `BookingHistory`, `Holiday`, `Department`, etc.
- JPA annotations: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`, `@JoinTable`.

### 4. Repositories & Queries
- Repositories: `RoomRepository`, `BookingRepository`, `UserRepository`, `FeatureRepository`, `BuildingRepository`, `RoleRepository`, `BookingHistoryRepository`.
- Custom queries with `@Query` (JPQL & native SQL) for availability checks.
- Admin operations with `@Modifying` for soft deletes/cancellations.

### 5. Validation
- DTO validation: `@NotNull`, `@NotBlank`, `@Email`, `@Future`, `@FutureOrPresent`, `@Min`, `@Max`.
- Custom validator: `@NoOverlap` → prevents booking slot collisions.

### 6. Service Layer
- Implemented as **interfaces + implementations**.
- Use `@Primary`/`@Qualifier` for strategy injection.

### 7. Web Layer (REST Endpoints)
- Controllers such as `AuthController`, `RoomController`, `BookingController`, `BuildingController`, etc.
- REST mappings: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, `@DeleteMapping`.
- Returns `ResponseEntity<?>` with proper HTTP status codes.

### 8. Security & JWT
- Authentication endpoints under: `/api/v1/auth/**`
- Roles: `STUDENT`, `FACULTY`, `ADMIN`
- Stateless authentication with JWT.
- JWT filter parses tokens and attaches authentication to context.
- Handles expired/invalid tokens with `401/403`.

### 9. Business Rules
- Booking constraints:
  - No past bookings.
  - Max horizon: 90 days.
  - No bookings on holidays.
  - No double-booking for the same room/time.
- Workflow:
  - Booking starts as `PENDING`.
  - Admin approves → `APPROVED`  
  - Admin rejects → `REJECTED`
- Cancellation policy:
  - User can cancel own `PENDING/APPROVED` before start time.
  - Admin can cancel any booking.
- History tracking: all changes stored in `BookingHistory`.

### 10. Logging
- Uses `SLF4J + Logback`.
- Levels configured via `application.yml`.
- Request logging for debugging.

### 11. Exception Handling
- Global exception handler with `@ControllerAdvice`.
- Custom exceptions: `ResourceNotFoundException`, `UnauthorizedActionException`, etc.
- Returns structured JSON errors.

### 12. Testing
- **Unit Tests**: (JUnit 5 + Mockito) → services, validators, booking overlap logic.
- **Integration Tests**: `@SpringBootTest`, `@WebMvcTest`, MockMvc, H2 in-memory DB.

---

## ✅ Example User Stories

### Student – View Room Availability
- **Given** a room and date range → API returns free slots.
- **Invalid request** (end before start, past date) → 400 Bad Request.

### Faculty – Request Booking
- **POST** `/bookings` with roomId, startTime, endTime, purpose.
- Validations → no overlaps, start in future.
- Returns `201 Created`.

### Admin – Approve/Reject Booking
- **PATCH** `/bookings/{id}/approve` → status set to `APPROVED`.
- **PATCH** `/bookings/{id}/reject` → status set to `REJECTED`.
- Writes entry in `BookingHistory`.

### Student/Faculty – Cancel Booking
- Allowed if current time < startTime AND status = `PENDING`/`APPROVED`.
- Returns `200 OK` and logs in `BookingHistory`.

### Admin – Manage Rooms
- Full CRUD on rooms and features.
- Reject delete if room has future `APPROVED` bookings → `409 Conflict`.

---

## 🧪 Testing Plan

- **Unit Tests**:  
  - Services → overlap logic, approval/cancel rules.  
  - Validators → `@NoOverlap`.  

- **Integration Tests**:  
  - Controllers with `@WebMvcTest + MockMvc`.  
  - Repositories with `@SpringBootTest + H2 + TestEntityManager`.

---

## 📝 Logging & Error Handling

- **Logging**:
  - `INFO` for transitions.
  - `WARN` for policy violations.
  - `ERROR` for unexpected exceptions.  

- **Error Responses**:
  - JSON schema → `{ timestamp, path, status, error, message, traceId }`.

---

## 🚧 Next Steps

- Add more controllers (`HolidayController`, `RoomFeatureController`, `DepartmentController`, etc.)  
- Extend DTOs with request/response validation.  
- Improve documentation with API examples (using Swagger/OpenAPI).  
