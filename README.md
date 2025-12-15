# **Product Order Management**

A Spring boot application for product management and users to place orders. The application uses the JWT token,role based access to the services, relational database to manage the products, user and Order information.

## Features
  * RESTful API with Swagger documentation
  * JWT Authentication with Role based access control. Roles: ADMIN, USER, PREMIUM_USER
  * Discount strategy using Strategy Pattern
  * Postgres database for Storing the User and Inventory Information
  * Unit and Integration Testing to cover the scenarios 
  * Database migration with Flyway 

## Table of Contents
  * Setup Instructions
  * Design Decisions
  * API Documentation

## Setup Instructions
  * Java 17+
  * Maven 3.9+
  * Postgres 18+

  Take the postgres image from docker and start the posgres using the below command.
      `docker run -d -e POSTGRES_PASSWORD=your-password -p 5432:5432 --name my-postgres postgres`
  
  Run the Application using the below command
  `mvn spring-boot:run "-Dspring-boot.run.arguments=--JWT_SECRET_KEY=your-secret --DB_PASSWORD=your-db-password --DB_USERNAME=your-db-username"`
  
  #### URLs to access the Application
  * Application Base URL: http://localhost:8080
  * Swagger URL:          http://localhost:8080/swagger-ui.html

  Application uses the H2 in-memory database for Test profile

## Design Decisions
* Application uses the Layered Architecture. Controller -> Service -> Repository
* DTO and Entity separation to avoid returning unnecessary field in the API response
* Strategy Pattern to apply the discount based on the Role and the total amount. 
     The Discount logic iterates through all the discount strategies and applies based on the rule applicability. 
      If the user's role is PREMIUM_USER then discount of 10% is applied 
      If the User's order amount exceeds 500$ then discount of 5% is applied
      If the user's role is PREMIUM_USER and the Order amount exceeds 500$ then a combined discount of 15% is applied.
    This way it helps in adding a new discount strategy easily to the code without breaking any existing discount applicability logic.
* Database index for optimized queries


## API Documentation
  Three Users are already created using the Flyway Database Migration scripts

    | User    | Password     | Role         |
    |---------|--------------|--------------|
    | admin   | $hellyM@9    | ADMIN        |
    | p_user1 | K#ngPremium2 | PREMIUM_USER |
    | user1   | Au$tenJane3  | USER         |

  ### AUTHENTICATION API: LOGIN
User can login using the below API with any of the UserId and password mentioned in the above table

      `curl --location 'http://localhost:8080/api/v1/auth/login' \
      --header 'Content-Type: application/json' \
      --data '{
      "userName":"p_user1",
      "password":"K#ngPremium2"
      }'`
        
    
  ### Product Management APIs: PRODUCT MANAGEMENT
1. Get ALL Products:
    Based on the role the products will be returned. 
        If the User is ADMIN all the products which are deleted also returned so that ADMIN can make it active again.
        If the User is USER and PREMIUM_USER only the active products are returned.

    `curl --location 'http://localhost:8080/api/v1/products/' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwX3VzZXIxIiwiaWF0IjoxNzY1NjQ0Mzc0LCJleHAiOjE3NjU2NDQ5NzR9.9SApO1MOea3AFSxd91D1gsM3fjYhbKy4NXZ0JjcAsLw'`

2. Create-Product (ADMIN ROLE only)
      
    `curl --location 'http://localhost:8080/api/v1/products/create-products' \
    --header 'Content-Type: application/json' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc2NTY5NTc1OSwiZXhwIjoxNzY1Njk2MzU5fQ.ZMS2GWWOF4cFDUu9o19EPcrcEx1Tmpf8DHWyU9GacRs' \
    --data '[
    {
    "name": "Violin",
    "description": "String instrument",
    "price": 1000.00,
    "quantity": 12
    }
    ]'`
3. Update-product (ADMIN ROLE only)
    
    `curl --location --request PUT 'http://localhost:8080/api/v1/products/update-product/6' \
    --header 'Content-Type: application/json' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwX3VzZXIxIiwiaWF0IjoxNzY1NjE2NTkzLCJleHAiOjE3NjU2MTcxOTN9.n9xtcY1twpTOLN5gr9Z1zPISEvllSYXA6zRWJCvMJ1Q' \
    --data '{
    "name": "Violins",
    "description": "One of the String instrument",
    "price": 199.90,
    "quantity": 10
    }'`
4. Delete-Product (ADMIN ROLE only)
    `curl --location --request DELETE 'http://localhost:8080/api/v1/products/3' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwX3VzZXIxIiwiaWF0IjoxNzY1NjE2NTkzLCJleHAiOjE3NjU2MTcxOTN9.n9xtcY1twpTOLN5gr9Z1zPISEvllSYXA6zRWJCvMJ1Q' \
    --data ''`
5. Filter-Products (All Users)
   `curl --location 'http://localhost:8080/api/v1/products/search?minPrice=120&maxPrice=9000' \
   --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwX3VzZXIxIiwiaWF0IjoxNzY1NjI4MDE4LCJleHAiOjE3NjU2Mjg2MTh9.6fFoNO76JLE8AR0gDNAf2sR3Fy-8R_BZWTASh0MsD4c'`

### Order Management APIs: Place Order
User can place the order (Any user)
    `curl --location --request DELETE 'http://localhost:8080/api/v1/products/3' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwX3VzZXIxIiwiaWF0IjoxNzY1NjE2NTkzLCJleHAiOjE3NjU2MTcxOTN9.n9xtcY1twpTOLN5gr9Z1zPISEvllSYXA6zRWJCvMJ1Q' \
    --data ''`
    