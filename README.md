Authentication Service
This is a Spring Boot-based authentication service that supports OAuth2, security, role-based access control (RBAC), and integration with a MySQL database. The service can be easily deployed using Docker and Docker Compose.

Features
OAuth2 Authentication: Supports OAuth2 for user authentication using third-party providers like Google, GitHub, etc.
Security: Implements JWT (JSON Web Tokens) for securing API endpoints.
Role-Based Access Control (RBAC): Allows assigning roles (e.g., ADMIN, USER) to users, with different levels of access to resources.
User Registration & Login: Handles user registration, login, and token-based authentication.
MySQL Integration: Uses MySQL for persistent storage of user data, roles, and authentication tokens.
Dockerized Deployment: The service is containerized for easy deployment using Docker and Docker Compose.
