# Club Manager (Backend)

A robust, monolithic Spring Boot REST API designed to manage university club operations, team hierarchies, and internal communications. 

## 🏗️ Architecture & Design
This project follows a strict layered monolithic architecture, emphasizing clean code, maintainability, and enterprise Java standards. 

*   **Framework:** Spring Boot (Java)
*   **Data Persistence:** Spring Data JPA & Hibernate
*   **Database:** MySQL

## 🚀 Current Progress: The Data Layer
The foundation of the application has been established with a focus on robust database design and automated configuration:

*   **Entity Mapping (JPA/Hibernate):** Designed relational database tables using `@Entity`, establishing correct cardinality (`@ManyToOne`) between `Users`, `Roles`, `Teams`, and `Messages`.
*   **Repository Pattern:** Implemented Spring Data `JpaRepository` interfaces for abstracted data access, including custom query derivations (e.g., `findByRoleName`).
*   **Automated Database Seeding:** Engineered a multi-stage startup configuration using `@Order` and `CommandLineRunner`.
    *   `DatabaseSeeder`: Safely initializes required lookup tables (Roles, Teams) to ensure referential integrity.
    *   `DummyDataSeeder`: Populates the development environment with hierarchical user data (Super Admin, Leads, Members).
*   **Clean Code Practices:** 
    *   Transitioned from field injection to **Constructor Injection** for immutable dependencies.
    *   Implemented an `AppConstants` configuration to eliminate magic strings and prevent configuration typos.
    *   Integrated `slf4j` for structured application logging.

*Developed by Sanjay Vinod K*  
