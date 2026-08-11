# Club Manager (Backend)

A robust, monolithic Spring Boot REST API designed to manage university club operations, team hierarchies, and internal communications. 

## 🏗️ Architecture & Design
This project follows a strict layered monolithic architecture, emphasizing clean code, maintainability, and enterprise Java standards. 

*   **Framework:** Spring Boot 3 (Java 17+)
*   **Security:** Spring Security 6 (JWT, Stateless Authentication, RBAC)
*   **Data Persistence:** Spring Data JPA & Hibernate
*   **Database:** MySQL (Production) / H2 (In-Memory Testing)
*   **Testing Stack:** JUnit 5, Mockito, & Spring MockMvc
*   **Build Tool:** Maven

## 🚀 Current Progress: Core API, IAM Security & Testing
The application has evolved into a fully secured, stateless, role-aware REST API implementing the complete request lifecycle from Controller to Database, backed by cryptographic authentication and a testing suite.

### 🗄️ The Data Layer
*   Designed relational database tables using `@Entity` with strict data constraints (`nullable = false`), establishing correct cardinality (`@ManyToOne`) between `Users`, `Roles`, `Teams`, and `Messages`.
*   Implemented Spring Data `JpaRepository` interfaces for abstracted data access, including custom query derivations (e.g., `findByRoleName`, `existsByEmail`).
*   Engineered a multi-stage startup configuration using `@Order` and `CommandLineRunner`.
    *   `DatabaseSeeder`: Safely initializes required lookup tables (Roles, Teams) to ensure referential integrity.
    *   `DummyDataSeeder`: Populates the development environment with hierarchical user data (Super Admin, Leads, Members).

### 📦 Data Transfer Objects (DTOs) & Validation
*   Implemented the DTO pattern (`UserRegistrationDTO`, `AuthRequestDTO`, `ErrorResponseDTO`) to strictly separate internal database entities from external API payloads.
*   Integrated `jakarta.validation` (`@NotBlank`, `@Email`) to ensure payload integrity and sanitize inputs at the Controller level before processing.
*   Enforced security best practices by completely isolating sensitive information (e.g., password hashes) from outgoing response DTOs.

### 🧠 Business Logic & Service Layer
*   Developed the `UserService` and `CustomUserDetailsService` to handle complex business rules, such as data sanitization, duplicate entity checks, and user lookup.
*   Implemented `@Transactional` boundaries to guarantee atomicity and database integrity ("All or Nothing") during multi-step database operations.
*   Engineered custom runtime exceptions (e.g., `UserAlreadyExistsException`, `ResourceNotFoundException`) to carry dynamic, context-aware error details for fail-fast data validation.

### 🚨 Global Exception Handling
*   Implemented a centralized `@RestControllerAdvice` component to intercept thrown exceptions across all controllers.
*   Standardized all API errors into a strict JSON contract (`ErrorResponseDTO`), eliminating server stack traces in favor of actionable frontend HTTP responses (e.g., 400 Bad Request, 401 Unauthorized, 403 Forbidden).

### 🛡️ Identity & Access Management (IAM)
*   **Stateless Authentication Engine:** Built an `AuthController` (`POST /api/auth/login`) that intercepts credentials, verifies them against BCrypt password hashes, and returns a signed JSON Web Token (JWT).
*   **Cryptographic Utility (`JwtUtil`):** Engineered HMAC-SHA256 token generation and validation logic with expiration enforcement.
*   **Stateless JWT Bouncer (`JwtAuthenticationFilter`):** Implemented a custom `OncePerRequestFilter` to intercept HTTP requests, extract `Bearer` tokens from the `Authorization` header, validate signatures, and populate the `SecurityContextHolder`.
*   **Security Architecture (`SecurityConfig`):** Configured Spring Security to be strictly stateless (`SessionCreationPolicy.STATELESS`), disabled CSRF for REST interactions, and whitelisted public routes.
*   **Role-Based Access Control (RBAC):** Enabled `@EnableMethodSecurity` and configured method-level protection (`@PreAuthorize("hasRole('CLUB_ADMIN')")`) to lock down administrative operations.

### 🧪 Testing & Quality Assurance
*   **Service Layer Isolation:** Utilized **Mockito** (`@Mock`, `@InjectMocks`) to test business logic in isolation. Verified successful state changes and expected failure scenarios without relying on a live database.
*   **Web Layer Simulation:** Leveraged Spring Boot's `@WebMvcTest` and **MockMvc** to simulate HTTP requests and test controllers.
*   **API Contract Verification:** Verified correct endpoint routing, JSON payload serialization, proper HTTP status codes, and validated that `@Valid` constraints properly trigger 400 Bad Request responses.

---

## 🌐 API Endpoint Reference

| Method | Endpoint | Authorization | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/users/register` | Bearer Token  | Register a new user |
| `POST` | `/api/auth/login` | Public | Authenticate credentials & return JWT |
| `GET` | `/api/users` | Bearer Token | Fetch all registered users |
| `GET` | `/api/teams` | Bearer Token | List all teams |
| `POST` | `/api/teams` | `ROLE_CLUB_ADMIN` | Create a new team |
| `PUT` | `/api/users/{userId}/team/{teamId}` | `ROLE_CLUB_ADMIN` | Assign a user to a team |

---

## ⏭️ Next Steps: Backend Optimizations & Features
*   **CORS Configuration:** Configure Cross-Origin Resource Sharing (CORS) in `SecurityConfig` to securely accept HTTP requests from external client domains.
*   **Real-time Messaging:** Integrate Spring WebSockets and STOMP to enable real-time, bi-directional chat channels for intra-team communication.
*   **API Documentation:** Integrate `springdoc-openapi` (Swagger UI) to automatically generate interactive, visual API documentation for external client testing.
*   **Pagination & Sorting:** Implement Spring Data `Pageable` on GET endpoints to handle large data sets (e.g., fetching 100+ users or message histories efficiently).
*   **Containerization:** Package the Spring Boot application using a `Dockerfile` for seamless cloud deployment.

---
*Developed by Sanjay Vinod K*
