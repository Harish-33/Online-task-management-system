# Online Task Management System

A production-grade backend RESTful task management service built with **Java 17**, **Spring Boot 3**, **Spring Data JPA**, and **SQL** (H2 for instant zero-configuration development/testing, and MySQL for production persistence).

---

## Highlights & Features

- **Layered Enterprise Architecture**: Strict separation of concerns across Controller (`@RestController`), Service (`@Service`), Data Access (`@Repository`), Domain Entities (`@Entity`), and Data Transfer Objects (`DTOs`).
- **Full CRUD RESTful APIs**: Create, retrieve, update, soft/hard delete, and track tasks with status lifecycles.
- **Dynamic Filtering & Pagination**: Filter tasks by status, priority, category, assignee, date range, or free-text search across titles and descriptions using Spring Data JPA Specifications.
- **Robust Input Validation**: Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Size`, `@Pattern`) with detailed field-level error feedback.
- **Centralized Exception Handling**: Global `@RestControllerAdvice` returning standardized RFC-compliant error payloads (`400 Bad Request`, `404 Not Found`, `409 Conflict`, `500 Internal Error`).
- **Interactive API Documentation**: Swagger UI / OpenAPI 3 integration accessible directly from the browser.
- **Dual SQL Support**: Out-of-the-box in-memory **H2 Database** with Web Console, plus pre-configured **MySQL** profile for production deployments.
- **Automated Startup Seeding**: Automatically populates realistic sample users, categories, and tasks with various priorities and due dates on initial run.
- **Test Suite**: Unit testing using **JUnit 5** and **Mockito**, plus integration testing using **Spring MockMvc**.

---

## System Architecture

```
                                    +------------------------------+
                                    |     Client / Swagger UI      |
                                    +------------------------------+
                                                   |
                                                   v
+--------------------------------------------------------------------------------------------------+
| Presentation Layer (@RestController)                                                             |
|   - TaskController (/api/v1/tasks)                                                               |
|   - UserController (/api/v1/users)                                                               |
|   - CategoryController (/api/v1/categories)                                                      |
|   - GlobalExceptionHandler (@RestControllerAdvice)                                               |
+--------------------------------------------------------------------------------------------------+
                                                   |
                                                   v
+--------------------------------------------------------------------------------------------------+
| Service Layer (@Service, @Transactional)                                                         |
|   - TaskService & TaskServiceImpl (Business rules, status transitions, statistics)               |
|   - UserService & UserServiceImpl (Unique constraints, user queries)                             |
|   - CategoryService & CategoryServiceImpl (Categories and tags)                                  |
|   - Mappers (Entity <-> DTO conversions, computed overdue calculations)                          |
+--------------------------------------------------------------------------------------------------+
                                                   |
                                                   v
+--------------------------------------------------------------------------------------------------+
| Data Persistence Layer (Spring Data JPA)                                                         |
|   - TaskRepository & TaskSpecification (CriteriaBuilder dynamic queries)                         |
|   - UserRepository, CategoryRepository                                                           |
+--------------------------------------------------------------------------------------------------+
                                                   |
                                                   v
+--------------------------------------------------------------------------------------------------+
| Relational Database (SQL)                                                                        |
|   - H2 Database (In-memory, default profile)                                                     |
|   - MySQL 8.0+ (Production profile)                                                             |
+--------------------------------------------------------------------------------------------------+
```

---

## Entity Relationship Overview

- **`User`**: `id`, `username`, `email`, `fullName`, `createdAt`.
- **`Category`**: `id`, `name`, `description`, `colorCode`.
- **`Task`**:
  - `id`, `title`, `description`
  - `status` (`TODO`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`)
  - `priority` (`LOW`, `MEDIUM`, `HIGH`, `URGENT`)
  - `dueDate`, `createdAt`, `updatedAt`
  - `assignee` -> Many-to-One with `User`
  - `category` -> Many-to-One with `Category`

---

## REST API Endpoints

### 1. Task Management (`/api/v1/tasks`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/tasks` | Create a new task |
| `GET` | `/api/v1/tasks/{id}` | Get task details by ID |
| `GET` | `/api/v1/tasks` | Filter & paginate tasks (`?status=&priority=&categoryId=&assigneeId=&search=&page=&size=&sort=`) |
| `PUT` | `/api/v1/tasks/{id}` | Update task details |
| `PATCH` | `/api/v1/tasks/{id}/status` | Update task status lifecycle (`TODO`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`) |
| `DELETE` | `/api/v1/tasks/{id}` | Delete a task |
| `GET` | `/api/v1/tasks/stats` | Retrieve task metrics (counts by status, priority, and overdue tasks) |

### 2. User Management (`/api/v1/users`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/users` | Register a new user |
| `GET` | `/api/v1/users` | Retrieve list of all users |
| `GET` | `/api/v1/users/{id}` | Get user details by ID |

### 3. Category Management (`/api/v1/categories`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/categories` | Create a new task category |
| `GET` | `/api/v1/categories` | Retrieve all categories |
| `GET` | `/api/v1/categories/{id}` | Get category details by ID |

---

## Quick Start Guide

### Prerequisites
- **Java 17** or **Java 21**
- **Apache Maven 3.8+**
- (Optional) **MySQL 8.0+**

### 1. Run with H2 Database (Default - Zero Configuration)
Clone the repository and run:
```bash
mvn spring-boot:run
```
The server will start at: `http://localhost:8081`
- **Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)
- **H2 Web Console**: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
  - JDBC URL: `jdbc:h2:mem:taskdb`
  - Username: `sa`
  - Password: *(leave blank)*

### 2. Run with MySQL Database
To use MySQL, activate the `mysql` profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```
Or execute the packaged JAR:
```bash
java -jar target/online-task-management-system-1.0.0.jar --spring.profiles.active=mysql
```

---

## Running Tests

Execute the full suite of unit and integration tests:
```bash
mvn clean test
```

Package the project:
```bash
mvn clean package
```

---

## Sample cURL Requests

### Create a Task
```bash
curl -X POST http://localhost:8081/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Build Spring Boot Microservice",
    "description": "Implement authentication and task service",
    "priority": "HIGH",
    "dueDate": "2026-10-15",
    "assigneeId": 1,
    "categoryId": 1
  }'
```

### Filter Tasks (Pagination + Status Filter)
```bash
curl "http://localhost:8081/api/v1/tasks?status=IN_PROGRESS&page=0&size=5&sort=dueDate,asc"
```

### Update Task Status
```bash
curl -X PATCH http://localhost:8081/api/v1/tasks/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}'
```

### Get Task Statistics
```bash
curl http://localhost:8081/api/v1/tasks/stats
```
