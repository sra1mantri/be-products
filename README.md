# **Product Order Management**

A Spring Boot application for product management and order placement.  
The application uses JWT authentication, role-based access control, and a relational database to manage products, users, and orders.

---

## Features
- RESTful APIs with Swagger documentation
- JWT Authentication with Role-Based Access Control  
  Roles: `ADMIN`, `USER`, `PREMIUM_USER`
- Discount strategy using the Strategy Pattern
- PostgreSQL database for storing user, inventory, and order information
- Unit and Integration testing
- Database migration using Flyway

---

## Table of Contents
- [Setup Instructions](#setup-instructions)
- [Design Decisions](#design-decisions)
- [API Documentation](#api-documentation)
  - [Authentication API](#authentication-api)
  - [Product Management APIs](#product-management-apis)
  - [Order Management APIs](#order-management-apis)

---

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL 18+

### Start PostgreSQL using Docker
```bash
docker run -d \
-e POSTGRES_PASSWORD=your-password \
-p 5432:5432 \
--name my-postgres postgres
```

### Run the Application
```bash
mvn spring-boot:run \
"-Dspring-boot.run.arguments=--JWT_SECRET_KEY=your-secret \
--DB_PASSWORD=your-db-password \
--DB_USERNAME=your-db-username"
```

### Application URLs
- Application Base URL: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

> The application uses an **H2 in-memory database** for the `test` profile.

---

## Design Decisions
- Layered Architecture: **Controller → Service → Repository**
- DTO–Entity separation to avoid exposing unnecessary fields
- Strategy Pattern for discount calculation:
  - `PREMIUM_USER` → 10% discount
  - Order amount > $500 → 5% discount
  - `PREMIUM_USER` + Order amount > $500 → 15% combined discount
- Discount logic iterates through all strategies and applies based on rule applicability
- Easily extensible discount strategy without breaking existing logic
- Database indexing for optimized queries

---

## API Documentation

Three users are pre-created using Flyway database migration scripts:

| User     | Password     | Role          |
|----------|--------------|---------------|
| admin    | $hellyM@9    | ADMIN         |
| p_user1  | K#ngPremium2 | PREMIUM_USER  |
| user1    | Au$tenJane3  | USER          |

---

## Authentication API

### Login
Users can log in using any of the credentials listed above.

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "userName": "p_user1",
  "password": "K#ngPremium2"
}'
```

---

## Product Management APIs

### 1. Get All Products
Products returned depend on the user role:
- **ADMIN** → All products (including deleted ones)
- **USER / PREMIUM_USER** → Only active products

```bash
curl --location 'http://localhost:8080/api/v1/products/' \
--header 'Authorization: Bearer <JWT_TOKEN>'
```

---

### 2. Create Product (ADMIN only)

```bash
curl --location 'http://localhost:8080/api/v1/products/create-products' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--data '[
  {
    "name": "Violin",
    "description": "String instrument",
    "price": 1000.00,
    "quantity": 12
  }
]'
```

---

### 3. Update Product (ADMIN only)

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/products/update-product/6' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--data '{
  "name": "Violins",
  "description": "One of the String instrument",
  "price": 199.90,
  "quantity": 10
}'
```

---

### 4. Delete Product (ADMIN only)

```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/products/3' \
--header 'Authorization: Bearer <JWT_TOKEN>'
```

---

### 5. Filter Products (All Users)

```bash
curl --location 'http://localhost:8080/api/v1/products/search?minPrice=120&maxPrice=9000' \
--header 'Authorization: Bearer <JWT_TOKEN>'
```

---

## Order Management APIs

### Place Order (Any User)

```bash
curl --location --request POST 'http://localhost:8080/api/v1/orders/place-order' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--header 'Content-Type: application/json' \
--data '{
  "productId": 3,
  "quantity": 2
}'
```

---

---

## Testing Strategy

The application includes a comprehensive **Unit and Integration Testing** strategy to ensure correctness, security, and reliability of the APIs.

A total of **18 test cases** are implemented to cover critical business and technical scenarios.

---

### Unit Tests

Unit tests focus on validating individual components in isolation using mocks.

**Technologies used**
- JUnit 5
- Mockito
- Spring Boot Test

---

### Integration Tests

Integration tests validate the complete request flow from **Controller → Service → Repository** using an embedded database.

**Technologies used**
- Spring Boot Test
- MockMvc
- H2 in-memory database

---

### Test Coverage Summary

| Test Type        | Count  |
|------------------|--------|
| Unit Tests       | 8      |
| Integration Tests| 10    |
| **Total Tests**  | **18** |

---

### How to Run Tests

Run all unit and integration tests using:

```bash
mvn test
```

---

## Notes
- Replace `<JWT_TOKEN>` with the token received from the **Login API**
- Do not commit real JWT tokens to source control
- Base URL: http://localhost:8080
