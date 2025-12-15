# **Product Order Management**

A Spring boot application for product management and users to place orders. The application uses the JWT token,role based access to the services, relational database to manage the products, user and Order information.

## Features
  * RESTful API with Swagger documentation
  * JWT Authentication with Role based access control
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
  * Postgres

    Take the postgres image from docker and start the posgres using the below command
      docker run -d -e POSTGRES_PASSWORD=root -p 5432:5432 --name my-postgres postgres
  
