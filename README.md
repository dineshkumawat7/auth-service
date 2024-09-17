
# Authentication Service

This is a Spring Boot-based authentication service that supports OAuth2, security, role-based access control (RBAC), and integration with a MySQL database. The service can be easily deployed using Docker and Docker Compose.

## Features

1. OAuth2 Authentication: Supports OAuth2 for user authentication using third-party providers like Google, GitHub, etc.
2. Security: Implements JWT (JSON Web Tokens) for securing API endpoints.
3. Role-Based Access Control (RBAC): Allows assigning roles (e.g., ADMIN, USER) to users, with different levels of access to resources.
4. User Registration & Login: Handles user registration, login, and token-based authentication.
5. MySQL Integration: Uses MySQL for persistent storage of user data, roles, and authentication tokens.
6. Dockerized Deployment: The service is containerized for easy deployment using Docker and Docker Compose.

## Prerequisites

-> Java 17 or above                                                                                                                                                                                                  
-> Maven (for building the application)                                                        
-> Docker and Docker Compose (for running the application in containers)                                                                   
-> MySQL Client (optional, for database access)                                                         

## Docker Setup

The service is dockerized for easy deployment. Follow the steps below to run it using Docker and Docker Compose.


