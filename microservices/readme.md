# Quantity Measurement Microservices

This is the microservices-based version of the Quantity Measurement Application.

## Architecture

The project consists of 4 microservices:

1.  **API Gateway (Port 8090)**: 
    - The single entry point for all client requests.
    - Routes requests to appropriate downstream services.
    - Path: `http://localhost:8090/api/v1/...`
    - Validates JWT tokens on all protected routes.

2.  **User Service (Port 8081)**:
    - Handles user registration (signup) and authentication (login).
    - Issues JWT tokens upon successful login.
    - Public Endpoints: `/api/v1/auth/signup`, `/api/v1/auth/login`.
    - Protected Endpoints: `/api/v1/users/me`, `/api/v1/users/{id}`, etc.

3.  **Measurement Service (Port 8082)**:
    - Core logic for quantity measurement operations.
    - Endpoints: `/api/v1/quantities/compare`, `/compare/convert`, `/add`, `/subtract`, `/divide`.
    - Persists every operation record for auditing.

4.  **History Service (Port 8083)**:
    - Provides historical data and audit trails for all operations.
    - Endpoints: `/api/v1/history`, `/history/user`, `/history/operation/{op}`, etc.
    - Can be used for analytics and reporting.

## How to Run

1.  Navigate to the `microservices` directory.
2.  Build the entire project: `mvn clean install`.
3.  Run each microservice independently (or using an IDE):
    - `mvn spring-boot:run` in each service directory.
4.  Ensure the Angular frontend (if any) points to the Gateway at `http://localhost:8090`.

## Security

- JWT-based authentication is enforced at the Gateway level.
- Downstream services trust the headers (`X-Auth-User-Email`, `X-Auth-User-Role`) injected by the gateway after validation.
- The shared JWT secret must be consistent across `api-gateway` and `user-service`.

## Technical Stack

- Spring Boot 3.2.3
- Spring Cloud Gateway
- Spring Data JPA
- Spring Security
- H2 In-Memory Database
- Lombok
- SpringDoc / Swagger
