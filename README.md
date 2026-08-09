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
*   Implemented Spring Data `JpaRepository` interfaces for abstracted data access, including custom query derivations (e.g., `findByRoleName`, `existsByEmail`).
*   Engineered a multi-stage startup configuration using `@Order` and `CommandLineRunner`.
    *   `DatabaseSeeder`: Safely initializes required lookup tables (Roles, Teams) to ensure referential integrity.
    *   `DummyDataSeeder`: Populates the development environment with hierarchical user data (Super Admin, Leads, Members).

### 📦 Data Transfer Objects (DTOs) & Validation
*   Implemented the DTO pattern (`UserRegistrationDTO`, `UserResponseDTO`) to strictly separate internal database entities from external API payloads.
*   Integrated `jakarta.validation` (`@NotBlank`, `@Email`) to ensure payload integrity and sanitize inputs at the Controller level before processing.
*   Enforced security best practices by completely isolating sensitive information (e.g., password hashes) from outgoing response DTOs.

### 🧠 Business Logic & Service Layer
*   Developed the `UserService` to handle complex business rules, such as data sanitization, duplicate entity checks, and automatic default role assignment.
*   Implemented `@Transactional` boundaries to guarantee atomicity and database integrity ("All or Nothing") during multi-step database operations.
*   Engineered custom runtime exceptions (e.g., `UserAlreadyExistsException`) for fail-fast data validation.
*   Created robust Entity-to-DTO mapping methods that safely handle null foreign-key relationships.

### 🌐 REST API (Controller Layer)
*   Built the `UserController` utilizing `@RestController` and `@RequestMapping`.
*   Exposed stateless API endpoints, including `POST /api/users/register` (Entity Creation) and `GET /api/users` (Data Retrieval) returning proper `ResponseEntity` objects with standard HTTP status codes (201 CREATED, 200 OK).

### 🛡️ Security Configuration
*   Overrode default Spring Security lockdowns via a custom `SecurityConfig` class.
*   Disabled CSRF (Cross-Site Request Forgery) protection to accommodate stateless REST API interactions (Postman/Mobile).
*   Configured targeted endpoint authorization (`permitAll()` for public registration paths) while securing the broader application context.

### 🧹 Clean Code Practices
*   Maintained strict **Constructor Injection** for immutable dependencies across all layers.
*   Implemented an `AppConstants` configuration to eliminate magic strings and prevent configuration typos.
*   Integrated `slf4j` for structured application logging.

---
*Developed by Sanjay Vinod K*
