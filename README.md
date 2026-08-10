# Club Manager (Backend)

A robust, monolithic Spring Boot REST API designed to manage university club operations, team hierarchies, and internal communications. 

## 🏗️ Architecture & Design
This project follows a strict layered monolithic architecture, emphasizing clean code, maintainability, and enterprise Java standards. 

*   **Framework:** Spring Boot (Java)
*   **Data Persistence:** Spring Data JPA & Hibernate
*   **Database:** MySQL

## 🚀 Current Progress: Core API & Security
The application has evolved from a foundational data layer to a fully functional, secure REST API, implementing the complete request lifecycle from Controller to Database.

### 🗄️ The Data Layer
*   Designed relational database tables using `@Entity` with strict data constraints (`nullable = false`), establishing correct cardinality (`@ManyToOne`) between `Users`, `Roles`, `Teams`, and `Messages`.
*   Implemented Spring Data `JpaRepository` interfaces for abstracted data access, including custom query derivations (e.g., `findByRoleName`, `existsByEmail`, `existsByRegistrationNumber`).
*   Engineered a multi-stage startup configuration using `@Order` and `CommandLineRunner`.
    *   `DatabaseSeeder`: Safely initializes required lookup tables (Roles, Teams) to ensure referential integrity.
    *   `DummyDataSeeder`: Populates the development environment with hierarchical user data (Super Admin, Leads, Members).

### 📦 Data Transfer Objects (DTOs) & Validation
*   Implemented the DTO pattern (`UserRegistrationDTO`, `UserResponseDTO`, `ErrorResponseDTO`) to strictly separate internal database entities from external API payloads.
*   Integrated `jakarta.validation` (`@NotBlank`, `@Email`) to ensure payload integrity and sanitize inputs at the Controller level before processing.
*   Enforced security best practices by completely isolating sensitive information (e.g., password hashes) from outgoing response DTOs.

### 🧠 Business Logic & Service Layer
*   Developed the `UserService` to handle complex business rules, such as data sanitization, duplicate entity checks, and automatic default role assignment.
*   Implemented `@Transactional` boundaries to guarantee atomicity and database integrity ("All or Nothing") during multi-step database operations, such as linking users to teams.
*   Engineered custom runtime exceptions (e.g., `UserAlreadyExistsException`, `ResourceNotFoundException`) to carry dynamic, context-aware error details for fail-fast data validation.

### 🚨 Global Exception Handling
*   Implemented a centralized `@RestControllerAdvice` component to intercept thrown exceptions across all controllers.
*   Standardized all API errors into a strict JSON contract (`ErrorResponseDTO`), eliminating ugly server stack traces in favor of actionable frontend HTTP responses (e.g., 400 Bad Request, 404 Not Found, 409 Conflict).

### 🌐 REST API (Controller Layer)
*   Built the `UserController` and `TeamController` utilizing `@RestController` and `@RequestMapping`.
*   Exposed stateless API endpoints for entity creation (`POST /api/users/register`, `POST /api/teams`) and data retrieval (`GET /api/users`).
*   Implemented RESTful relationship endpoints using `@PutMapping` and `@PathVariable` (e.g., `PUT /api/users/{userId}/team/{teamId}`) to dynamically map foreign keys.

### 🛡️ Security Configuration
*   Overrode default Spring Security lockdowns via a custom `SecurityConfig` class.
*   Integrated `BCryptPasswordEncoder` to perform one-way cryptographic hashing on user passwords prior to database persistence, neutralizing database-breach vulnerabilities.
*   Disabled CSRF (Cross-Site Request Forgery) protection to accommodate stateless REST API interactions (Postman/Mobile).
*   Configured targeted endpoint authorization (`permitAll()` for public registration and team paths) while securing the broader application context.

---

## ⏭️ Next Steps: Identity & Access Management
*   **Authentication Engine:** Build a `/login` endpoint that intercepts credentials and validates them against the BCrypt-hashed database records.
*   **JWT (JSON Web Tokens):** Implement stateless authentication by generating secure, time-limited JWTs for verified users.
*   **Role-Based Access Control (RBAC):** Lock down administrative endpoints (like team assignments) so only users with the `CLUB_ADMIN` role can execute them, utilizing Spring Security filters.

---
*Developed by Sanjay Vinod K*
