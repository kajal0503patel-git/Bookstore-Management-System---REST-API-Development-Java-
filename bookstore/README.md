# Bookstore Management API

Spring Boot REST API for managing books, users, and orders.

## Setup

### 1. Configure the database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_mysql_user
spring.datasource.password=your_mysql_password
jwt.secret=replace-with-a-long-random-string-at-least-32-chars
```

### 2. Run the app
```bash
mvn spring-boot:run
```

The database tables are created automatically on first run (`ddl-auto=update`).

### 3. Explore the API
Open Swagger UI: http://localhost:8080/swagger-ui.html

---

## API Overview

### Auth
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |

### Books
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/api/books` | Public |
| GET | `/api/books?search=java` | Public |
| GET | `/api/books?genre=fiction` | Public |
| GET | `/api/books/{id}` | Public |
| POST | `/api/books` | Admin |
| PUT | `/api/books/{id}` | Admin |
| DELETE | `/api/books/{id}` | Admin |

### Orders
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/api/orders` | Admin |
| GET | `/api/orders/my` | Customer |
| GET | `/api/orders/{id}` | Authenticated |
| POST | `/api/orders` | Customer |
| PUT | `/api/orders/{id}/status?status=SHIPPED` | Admin |

---

## Authentication Flow

1. Register: `POST /api/auth/register` → receive JWT token
2. Login: `POST /api/auth/login` → receive JWT token
3. Include token in all protected requests:
   ```
   Authorization: Bearer <your-token>
   ```

---

## Example Requests

**Register**
```json
POST /api/auth/register
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "secret123",
  "role": "CUSTOMER"
}
```

**Place an order**
```json
POST /api/orders
Authorization: Bearer <token>
{
  "items": [
    { "bookId": 1, "quantity": 2 },
    { "bookId": 3, "quantity": 1 }
  ]
}
```

**Update order status (admin)**
```
PUT /api/orders/5/status?status=SHIPPED
Authorization: Bearer <admin-token>
```

---

## Tech Stack
- Java 17, Spring Boot 3.2
- Spring Security + JWT (jjwt)
- Spring Data JPA + MySQL
- Swagger / OpenAPI 3
- JUnit 5 + Mockito
