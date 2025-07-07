# Event Management Backend

This is a Spring Boot backend for an Event Management System. It provides RESTful APIs for user authentication, event management, attendance tracking, and user profile management, secured with JWT authentication.

## Features
- **User Authentication:** Register and log in with JWT-based authentication.
- **Event Management:** Create, update, delete, and list events.
- **Attendance Tracking:** RSVP to events and track attendance status.
- **User Profile:** Fetch user profile and their hosted/attending events.
- **Role-based Access:** Supports roles like USER and ADMIN.

## Tech Stack
- Java 17+
- Spring Boot 3.x
- Spring Security (JWT)
- JPA/Hibernate
- H2/PostgreSQL/MySQL (configurable)

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Setup
1. **Clone the repository:**
   ```sh
   git clone <your-repo-url>
   cd event-management-backend
   ```
2. **Configure application properties:**
   - Edit `src/main/resources/application.properties` for DB and JWT settings.
3. **Build and run:**
   ```sh
   ./mvnw spring-boot:run
   ```
   The server will start on `http://localhost:8080` by default.

## API Endpoints

### Authentication
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Log in and receive a JWT token

### User Profile
- `GET /api/users/me` — Get current user's profile (JWT required)
- `GET /api/users/me/events` — Get events the user is hosting and attending (JWT required)

### Events
- `GET /api/events` — List all events
- `POST /api/events` — Create a new event (JWT required)
- `PUT /api/events/{id}` — Update an event (JWT required)
- `DELETE /api/events/{id}` — Delete an event (JWT required)

### Attendance
- `POST /api/attendance` — RSVP to an event (JWT required)

## Authentication
- All protected endpoints require a JWT token in the `Authorization` header:
  ```
  Authorization: Bearer <your-jwt-token>
  ```

## Project Structure
- `controller/` — REST controllers
- `service/` — Business logic
- `repository/` — JPA repositories
- `model/` — Entity models
- `dto/` — Data transfer objects
- `security/` — JWT and security config
- `config/` — Spring configuration
- `util/` — Utility classes
