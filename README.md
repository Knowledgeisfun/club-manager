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

## 🚀 Current Progress: Core API, IAM Security & Communication Features
The application has evolved into a fully secured, stateless, role-aware REST API implementing the complete request lifecycle from Controller to Database, backed by cryptographic authentication, strict channel security, and a testing suite.

### 🗄️ The Data Layer
*   Designed relational database tables using `@Entity` with strict data constraints (`nullable = false`), establishing correct cardinality (`@ManyToOne`) between `Users`, `Roles`, `Teams`, and `Messages`.
*   Implemented Spring Data `JpaRepository` interfaces for abstracted data access, including custom query derivations (e.g., `findByRoleName`, `findByChannel_ChannelIdOrderBySentAtAsc`).
*   Engineered a multi-stage startup configuration using `@Order` and `CommandLineRunner` (e.g., `DatabaseSeeder`, `DummyDataSeeder`).
*   Synchronized the physical MySQL schema with Hibernate JPA entities, proactively dropping obsolete foreign key constraints and legacy columns to prevent schema drift and insertion errors.

### 📦 Data Transfer Objects (DTOs) & Validation
*   Implemented the DTO pattern (`UserRegistrationDTO`, `AuthRequestDTO`, `ErrorResponseDTO`) to strictly separate internal database entities from external API payloads.
*   Designed robust communication DTOs (`ChannelResponseDTO`, `MessageRequestDTO`, `MessageResponseDTO`) that embed contextual metadata (like team names and numerical IDs) for seamless frontend rendering.
*   Integrated `jakarta.validation` (`@NotBlank`, `@Size`, `@Email`) to ensure payload integrity and sanitize inputs at the Controller level before processing.

### 🧠 Business Logic & Service Layer
*   Developed complex business rules for data sanitization, duplicate entity checks, and user lookup.
*   **The Security Bouncer:** Engineered a robust, dynamic Role-Based Access Control (RBAC) bouncer within `MessageService` to govern communication streams:
    *   **Global Channels:** Restricted to `ADMIN` broadcast only.
    *   **Leadership Lounges:** Locked down to `ADMIN`, `LEAD`, and `CO-LEAD` roles.
    *   **Team Channels:** Strictly isolated so members can only post in their explicitly assigned team chats.
*   Implemented `@Transactional` boundaries to guarantee atomicity and database integrity ("All or Nothing") during multi-step database operations.

### 🚨 Global Exception Handling
*   Implemented a centralized `@RestControllerAdvice` component to intercept thrown exceptions across all controllers.
*   Standardized all API errors into a strict JSON contract (`ErrorResponseDTO`), eliminating server stack traces in favor of actionable frontend HTTP responses (e.g., 400 Bad Request, 401 Unauthorized, 404 Not Found).
*   Integrated `SecurityException` handling to cleanly map service-layer permission rejections (from the Bouncer) to formatted `403 Forbidden` API responses.

### 🛡️ Identity & Access Management (IAM)
*   **Stateless Authentication Engine:** Built an `AuthController` (`POST /api/auth/login`) that intercepts credentials, verifies them against BCrypt password hashes, and returns a signed JSON Web Token (JWT).
*   **Cryptographic Utility (`JwtUtil`):** Engineered HMAC-SHA256 token generation and validation logic, securely embedding custom claims (User Roles, Team ID) directly into the JWT payload for stateless authorization checks.
*   **Stateless JWT Bouncer (`JwtAuthenticationFilter`):** Implemented a custom `OncePerRequestFilter` to intercept HTTP requests, extract `Bearer` tokens, validate signatures, and populate the `SecurityContextHolder`.
*   **Role-Based Access Control (RBAC):** Enabled `@EnableMethodSecurity` and configured method-level protection alongside service-level dynamic role evaluations.

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
| `GET` | `/api/channels` | Bearer Token | Fetch available channels for navigation |
| `GET` | `/api/channels/{channelId}/messages` | Bearer Token | Fetch message history for a specific channel |
| `POST` | `/api/channels/{channelId}/messages` | Bearer Token | Post a message (Protected by Service Bouncer) |

---

## ⏭️ Next Steps: Backend Optimizations & Features
*   **Global CORS Configuration:** Centralize Cross-Origin Resource Sharing (CORS) in `SecurityConfig` to replace Controller-level `@CrossOrigin` annotations.
*   **Real-time Messaging (WebSockets):** Upgrade the current REST-polling chat implementation with Spring WebSockets and STOMP to enable true real-time, bi-directional communication.
*   **API Documentation:** Integrate `springdoc-openapi` (Swagger UI) to automatically generate interactive, visual API documentation for external client testing.
*   **Pagination & Sorting:** Implement Spring Data `Pageable` on GET endpoints to handle large data sets (e.g., fetching 100+ users or massive message histories efficiently).
*   **Containerization:** Package the Spring Boot application using a `Dockerfile` for seamless cloud deployment.

---
*Developed by Sanjay Vinod K*
