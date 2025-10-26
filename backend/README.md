# Real Estate Backend - Spring Boot Application

## Overview
This is the backend API for the Real Estate Listing Platform built with Spring Boot, PostgreSQL, and JWT authentication.

## Features
- 🔐 JWT Authentication & Authorization
- 👥 Role-based Access Control (USER, ADMIN)
- 🏠 Property CRUD operations
- 🔍 Advanced property search and filtering
- ⭐ Favorites system
- 🧑‍💼 Admin dashboard for property and user management
- 🇱🇰 Sri Lanka property data support

## Technologies
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** PostgreSQL
- **Security:** Spring Security + JWT
- **ORM:** JPA/Hibernate
- **Build Tool:** Maven

## Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6+

## Database Setup

1. Install PostgreSQL and create database:
```sql
CREATE DATABASE realestate_db;
```

2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/realestate_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

## Running the Application

1. Clone the repository and navigate to backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Login user

### Properties
- `GET /api/properties` - Get all approved properties (paginated)
- `GET /api/properties/{id}` - Get property by ID
- `GET /api/properties/search?keyword={keyword}` - Search properties
- `GET /api/properties/filter` - Filter properties by multiple criteria
- `GET /api/properties/latest` - Get latest 10 properties
- `GET /api/properties/featured` - Get featured properties
- `POST /api/properties` - Create property (authenticated)
- `PUT /api/properties/{id}` - Update property (owner only)
- `DELETE /api/properties/{id}` - Delete property (owner only)
- `GET /api/properties/my-properties` - Get user's properties

### Favorites
- `GET /api/favorites` - Get user's favorites
- `POST /api/favorites/{propertyId}` - Add to favorites
- `DELETE /api/favorites/{propertyId}` - Remove from favorites
- `GET /api/favorites/{propertyId}/check` - Check if property is favorited

### Admin
- `GET /api/admin/properties` - Get all properties (all statuses)
- `PUT /api/admin/properties/{id}/approve` - Approve property
- `PUT /api/admin/properties/{id}/reject` - Reject property
- `DELETE /api/admin/properties/{id}` - Delete any property
- `GET /api/admin/users` - Get all users
- `GET /api/admin/users/{id}` - Get user by ID
- `PUT /api/admin/users/{id}/toggle-status` - Activate/Deactivate user

## Property Types
- HOUSE
- APARTMENT
- LAND
- COMMERCIAL
- VILLA
- CONDO

## Listing Types
- SALE
- RENT

## Property Status
- PENDING - Newly created, awaiting approval
- APPROVED - Approved by admin, visible to public
- REJECTED - Rejected by admin
- SOLD - Property sold
- RENTED - Property rented

## Sri Lanka Districts
The system supports all 25 districts of Sri Lanka:
- Western: Colombo, Gampaha, Kalutara
- Central: Kandy, Matale, Nuwara Eliya
- Southern: Galle, Matara, Hambantota
- Northern: Jaffna, Kilinochchi, Mannar, Vavuniya, Mullaitivu
- Eastern: Batticaloa, Ampara, Trincomalee
- North Western: Kurunegala, Puttalam
- North Central: Anuradhapura, Polonnaruwa
- Uva: Badulla, Monaragala
- Sabaragamuwa: Ratnapura, Kegalle

## Security
- JWT tokens are used for authentication
- Token expiration: 24 hours
- Passwords are encrypted using BCrypt
- CORS enabled for frontend (localhost:3000)

## Default Roles
- `ROLE_USER` - Regular users who can list properties
- `ROLE_ADMIN` - Administrators with full access

## Development
The application uses Spring Boot DevTools for hot reload during development.

## Production Deployment
1. Update JWT secret in application.properties
2. Configure production database
3. Build jar: `mvn clean package`
4. Run: `java -jar target/realestate-backend-1.0.0.jar`

