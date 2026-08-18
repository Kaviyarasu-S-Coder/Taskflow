# TaskFlow Enterprise Backend

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-blue.svg)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

An enterprise-grade, multi-tenant Project & Task Management backend system built with **Java 21**, **Spring Boot 3.2.5**, and **Spring Security 6**, architected using **Hexagonal (Ports & Adapters) Architecture** and **Domain-Driven Design (DDD)** principles.

---

## 🌟 Key Features

- **Multi-Tenant IAM**: Organization-scoped tenancy, secure BCrypt password hashing, stateless JWT authentication with access (15m) and refresh (7d) tokens.
- **Role-Based Access Control (RBAC)**: Fine-grained permissions for `ROLE_SUPER_ADMIN`, `ROLE_ORG_ADMIN`, `ROLE_PROJECT_MANAGER`, `ROLE_DEVELOPER`, and `ROLE_VIEWER`.
- **Workspaces & Projects**: Multi-workspace management with custom project key prefixes (e.g. `CORE`, `TSK`), automatic incremental task sequence keys (e.g. `CORE-101`), and lead assignments.
- **Advanced Task Management**: Status progression (`BACKLOG`, `IN_PROGRESS`, `DONE`), priorities, story points, estimation & logged hours, parent-child task hierarchies, and predecessor/successor DAG dependencies (`BLOCKS`).
- **Collaboration**: Task comment threads and attachment metadata tracking.
- **In-App Notifications**: Real-time ready notification dispatches with read/unread tracking.
- **Database & Auditing**: Versioned Flyway database migrations (`V1` to `V4`), JPA Auditing (`created_at`, `updated_at`, `created_by`, `updated_by`), optimistic locking, and soft deletes.
- **Standardized Error Responses**: RFC 7807 `ProblemDetail` schemas across all exception boundaries.
- **Interactive Documentation**: SpringDoc OpenAPI 3.0 UI with Swagger authorization support.

---

## 🏗️ Architecture

```
com.taskflow
├── common/                     # Cross-cutting infrastructure & security
│   ├── domain/                 # BaseAuditEntity (soft deletes, versioning, timestamps)
│   ├── exception/              # GlobalExceptionHandler (RFC 7807 ProblemDetail)
│   └── security/               # JwtTokenProvider, JwtAuthenticationFilter, UserDetailsService
├── config/                     # Spring Configuration (Security, CORS, OpenAPI, JPA, Seeds)
└── modules/                    # Isolated domain modules (Hexagonal Ports & Adapters)
    ├── iam/                    # Authentication, users, roles, organizations
    ├── workspace/              # Multi-tenant workspaces
    ├── project/                # Projects, prefixes, members, role assignments
    ├── task/                   # Tasks, hierarchies, estimations, dependencies
    ├── collaboration/          # Task comments, file attachments
    └── notification/           # In-app notifications & read tracking
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 21 SDK**
- **Maven 3.9+** (or Docker)
- **MySQL 8.0** (optional; in-memory H2 is preconfigured for local development)

### Running Locally (with In-Memory H2)

```bash
# Set active profile to local
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
- **API Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **H2 Web Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taskflow_local`)

### Running with Docker Compose (Production MySQL + Mailpit)

```bash
docker-compose up --build -d
```
- **Backend API**: `http://localhost:8080`
- **Mailpit Web UI (Email Testing)**: `http://localhost:8025`
- **MySQL Database**: `localhost:3306`

---

## 📚 API Endpoints Summary

### Authentication (`/api/v1/auth`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Register new user + auto-provision organization |
| `POST` | `/api/v1/auth/login` | Authenticate user & return JWT token pair |
| `POST` | `/api/v1/auth/refresh` | Exchange refresh token for new access token |
| `POST` | `/api/v1/auth/logout` | Client-side session invalidation |

### Workspaces (`/api/v1/workspaces`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/workspaces` | Create new workspace |
| `GET` | `/api/v1/workspaces/{id}` | Get workspace by ID |
| `GET` | `/api/v1/workspaces/org/{orgId}` | List workspaces by organization ID |

### Projects (`/api/v1/projects`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/projects` | Create new project with key prefix |
| `GET` | `/api/v1/projects/{id}` | Get project details and members |
| `GET` | `/api/v1/projects/workspace/{wksId}`| List projects in workspace |
| `POST` | `/api/v1/projects/{id}/members` | Add member to project |

### Tasks (`/api/v1/tasks`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/tasks` | Create task with auto-generated key (e.g. `CORE-1`) |
| `GET` | `/api/v1/tasks/{id}` | Get task details by ID |
| `GET` | `/api/v1/tasks/project/{projectId}` | List all tasks in a project |
| `GET` | `/api/v1/tasks/assignee/{assigneeId}`| List all tasks assigned to user |
| `PUT` | `/api/v1/tasks/{id}` | Update task status, priority, hours, or assignee |
| `POST` | `/api/v1/tasks/{successorId}/dependencies` | Add task dependency |

### Collaboration & Notifications
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/comments` | Post comment on a task |
| `GET` | `/api/v1/comments/task/{taskId}` | List comments for a task |
| `GET` | `/api/v1/notifications/user/{userId}` | List notifications for user |
| `PUT` | `/api/v1/notifications/{id}/read` | Mark notification as read |

---

## 🧪 Testing

Execute full automated test suite:
```bash
mvn clean test
```

Build production JAR package:
```bash
mvn clean package
```
Artifact output location: `target/taskflow-backend-1.0.0-SNAPSHOT.jar`
