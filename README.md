# 🏠 Real Estate Listing Platform

A full-stack real estate listing platform for Sri Lanka with property browsing, posting, filtering, favorites, and admin dashboard features.

## 🌟 Features

### User Features
- ✅ User registration and authentication (JWT)
- 🏠 Browse and search properties
- 🔍 Advanced filtering (type, location, price, bedrooms, etc.)
- ⭐ Save favorite properties
- 📝 Post new property listings
- ✏️ Edit and delete own properties
- 🇱🇰 Sri Lanka-specific data (25 districts, cities)

### Admin Features
- 🧑‍💼 Admin dashboard
- ✅ Approve/reject property listings
- 👥 User management
- 🗑️ Delete any property
- 🔒 Role-based access control

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** PostgreSQL
- **Security:** Spring Security + JWT
- **ORM:** JPA/Hibernate
- **Build Tool:** Maven

### Frontend
- **Framework:** React 18
- **Styling:** Tailwind CSS
- **Routing:** React Router v6
- **HTTP Client:** Axios
- **Build Tool:** Create React App (without Vite)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 16 or higher
- PostgreSQL 12 or higher
- npm or yarn

## 🚀 Getting Started

### 1. Database Setup

```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE realestate_db;

# Exit psql
\q
```

### 2. Backend Setup

```bash
# Navigate to backend directory
cd backend

# Update database credentials in src/main/resources/application.properties
# spring.datasource.username=your_postgres_username
# spring.datasource.password=your_postgres_password

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend server will start at `http://localhost:8080`

### 3. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

The frontend application will open at `http://localhost:3000`

## 📁 Project Structure

```
realestate/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/realestate/
│   │   │   │   ├── config/           # Configuration classes
│   │   │   │   ├── controller/       # REST controllers
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── model/            # Entity models
│   │   │   │   ├── payload/          # Request/Response payloads
│   │   │   │   ├── repository/       # JPA repositories
│   │   │   │   ├── security/         # Security & JWT
│   │   │   │   └── service/          # Business logic
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── database/
│   │   └── init.sql             # Database initialization
│   ├── pom.xml
│   └── README.md
│
├── frontend/                   # React frontend
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/         # Reusable components
│   │   ├── pages/              # Page components
│   │   ├── context/            # React Context
│   │   ├── services/           # API services
│   │   ├── utils/              # Utility functions
│   │   ├── App.js
│   │   ├── index.js
│   │   └── index.css
│   ├── package.json
│   ├── tailwind.config.js
│   └── README.md
│
└── README.md                   # This file
```

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Login user

### Properties
- `GET /api/properties` - Get all properties (paginated)
- `GET /api/properties/{id}` - Get property by ID
- `GET /api/properties/search?keyword={keyword}` - Search properties
- `GET /api/properties/filter` - Filter properties
- `GET /api/properties/latest` - Get latest properties
- `GET /api/properties/featured` - Get featured properties
- `POST /api/properties` - Create property (authenticated)
- `PUT /api/properties/{id}` - Update property (owner only)
- `DELETE /api/properties/{id}` - Delete property (owner only)
- `GET /api/properties/my-properties` - Get user's properties

### Favorites
- `GET /api/favorites` - Get user's favorites
- `POST /api/favorites/{propertyId}` - Add to favorites
- `DELETE /api/favorites/{propertyId}` - Remove from favorites

### Admin (Role: ADMIN)
- `GET /api/admin/properties` - Get all properties
- `PUT /api/admin/properties/{id}/approve` - Approve property
- `PUT /api/admin/properties/{id}/reject` - Reject property
- `DELETE /api/admin/properties/{id}` - Delete property
- `GET /api/admin/users` - Get all users
- `PUT /api/admin/users/{id}/toggle-status` - Toggle user status

## 🏗️ Data Models

### User
- ID, Username, Email, Password (hashed)
- Full Name, Phone
- Roles (USER, ADMIN)
- Active status

### Property
- Title, Description
- Type (HOUSE, APARTMENT, LAND, COMMERCIAL, VILLA, CONDO)
- Listing Type (SALE, RENT)
- Price, Address, City, District
- Bedrooms, Bathrooms, Land Size, Floor Size
- Features, Images
- Status (PENDING, APPROVED, REJECTED, SOLD, RENTED)
- Owner reference

### Role
- ROLE_USER - Regular users
- ROLE_ADMIN - Administrators

### Favorite
- User-Property relationship

## 🇱🇰 Sri Lanka Districts

The system supports all 25 districts:
- **Western:** Colombo, Gampaha, Kalutara
- **Central:** Kandy, Matale, Nuwara Eliya
- **Southern:** Galle, Matara, Hambantota
- **Northern:** Jaffna, Kilinochchi, Mannar, Vavuniya, Mullaitivu
- **Eastern:** Batticaloa, Ampara, Trincomalee
- **North Western:** Kurunegala, Puttalam
- **North Central:** Anuradhapura, Polonnaruwa
- **Uva:** Badulla, Monaragala
- **Sabaragamuwa:** Ratnapura, Kegalle

## 🔒 Security

- JWT tokens for authentication
- BCrypt password encryption
- Role-based access control
- CORS enabled for frontend
- Token expiration: 24 hours




## 🚀 Future Enhancements

- [ ] File upload for property images
- [ ] Email notifications
- [ ] Property comparison feature
- [ ] Advanced map integration
- [ ] Property analytics
- [ ] Mobile app (React Native)
- [ ] Payment gateway integration
- [ ] Property verification system
- [ ] Review and rating system


