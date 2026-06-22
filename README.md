## Sensitive Words API 🛡️
A high-performance, production-ready Spring Boot microservice designed to sanitize chat messages by detecting and redacting sensitive words (e.g., SQL keywords, profanity, or custom company-defined lists).

## 🚀 Features
- Real-time Sanitization: High-performance, in-memory compiled Regex caching to ensure minimal latency on the business logic endpoint.

- Role-Based Access Control (RBAC): Secured with stateless JSON Web Tokens (JWT).

- ADMIN role required for dictionary management (CRUD).

- USER or ADMIN role required for sanitizing messages.

- Dynamic Dictionary: Database-backed CRUD endpoints to update the sensitive words list on the fly without restarting the service.

- API Documentation: Interactive Swagger UI / OpenAPI 3 integration.

- Containerized: Multi-stage Dockerfile and docker-compose.yml for seamless deployment alongside an MSSQL database.

- Robust Testing: H2 in-memory database configured for fast, isolated unit and integration tests.

## 🛠️ Tech Stack
- Language: Java 17+

- Framework: Spring Boot 3.x

- Security: Spring Security + jjwt (JWT Authentication)

- Database: Microsoft SQL Server (Production) / H2 (Testing)

- ORM: Spring Data JPA / Hibernate

- Documentation: SpringDoc OpenAPI (Swagger)

- Containerization: Docker & Docker Compose

## ⚙️ Prerequisites
Before running this application, ensure you have the following installed:

- Java 17 JDK or higher

- Maven 3.8+

- Docker & Docker Compose (For containerized execution)

## 🏃 Getting Started
### Option 1: Run via Docker Compose (Recommended)
This is the easiest way to start the application, as it automatically spins up an MSSQL database container and wires it to the Spring Boot API.

1. Clone the repository and navigate to the project root.

2. Run the following command:

       bash        
       docker-compose up --build

3. The API will be available at http://localhost:8080

### Option 2: Run Locally (Manual DB Setup)
If you prefer to run the app via Maven, you must have an MSSQL instance running.

1. Update your application.yml or export the required environment variables:

-  DB_HOST, DB_NAME, DB_USER, DB_PASS

2. Build and run the application:

       Bash       
       mvn clean install
       mvn spring-boot:run

## 📖 API Documentation & Swagger
Once the application is running, navigate to the Swagger UI to interact with the API directly from your browser:

👉 http://localhost:8080/swagger-ui.html

## Authentication Flow (How to test via Swagger)
This API is secured. To access the protected endpoints:

1. Go to the Authentication API section in Swagger.

2. Use the /api/v1/auth/register endpoint to create an Admin user:
               
         JSON
         {
           "username": "admin_user",
           "password": "SecurePassword123!",
           "role": "ADMIN"
         }
3. Copy the token string returned in the response.
4. Scroll to the top of the Swagger UI page, click the green Authorize button.
5. Paste your token (Swagger will automatically prepend Bearer ) and click Authorize. You can now test the internal and external endpoints.


## 🔌 Core Endpoints Overview

### 1. Authentication (Public)

       POST	/api/v1/auth/register	Register a new user (ADMIN or USER). Returns JWT.
       POST	/api/v1/auth/login	Authenticate an existing user. Returns JWT.

### 2. Admin API(Internal-Requirements ADMIN Role)

        GET	  /api/v1/internal/words	Retrieve all sensitive words.
        POST      /api/v1/internal/words	Add a new sensitive word.
        PUT	  /api/v1/internal/words/{id}	Update an existing word.
        DELETE	  /api/v1/internal/words/{id}	Delete a sensitive word.

### 3. Sanitization API (External - Requires USER or ADMIN Role)

       POST	/api/v1/external/messages/sanitize	Receives a raw message and returns it with sensitive words replaced by asterisks (e.g., ******).

## 🧪 Running Tests
The application uses an H2 in-memory database for isolated testing, ensuring your tests don't pollute your local MSSQL database.

To run the unit and integration tests:
               
    Bash
    mvn test

(Ensure the @ActiveProfiles("test") annotation is present on your test classes so Spring Boot knows to use the application-test.yml configuration).

## 🏗️ Architecture Notes
-  Performance: Querying the database for every incoming chat message would create a massive bottleneck. Instead, the SensitiveWordService caches a compiled Regex Pattern in memory. This pattern is strictly rebuilt only when an Admin creates, updates, or deletes a word.

-  Security: Passwords are never stored in plaintext. The application uses BCryptPasswordEncoder to hash credentials before saving them to the database. Sessions are completely stateless.