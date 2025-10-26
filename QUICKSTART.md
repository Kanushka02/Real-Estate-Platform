# 🚀 Quick Start Guide

Get your Real Estate Platform up and running in 5 minutes!

## Prerequisites Checklist
- [ ] Java 17 installed (`java -version`)
- [ ] Maven installed (`mvn -version`)
- [ ] Node.js 16+ installed (`node -version`)
- [ ] PostgreSQL installed and running
- [ ] Git (optional, for cloning)

## Step-by-Step Setup

### Step 1: Database Setup (2 minutes)

```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE realestate_db;

# Verify
\l

# Exit
\q
```

### Step 2: Backend Setup (2 minutes)

```bash
# Navigate to backend folder
cd backend

# Edit application.properties (optional - only if your PostgreSQL password is different)
# File: src/main/resources/application.properties
# Update: spring.datasource.password=YOUR_PASSWORD

# Run backend
mvn spring-boot:run
```

✅ Backend running at: http://localhost:8080

### Step 3: Frontend Setup (1 minute)

Open a **NEW terminal** window:

```bash
# Navigate to frontend folder
cd frontend

# Install dependencies
npm install

# Start frontend
npm start
```

✅ Frontend running at: http://localhost:3000

## 🎉 You're Done!

The application will automatically open in your browser!

## First Steps

### 1. Create an Admin Account (Optional)

Register a new account, then manually add admin role in database:

```sql
-- After creating a user through the UI
psql -U postgres -d realestate_db

-- Find your user ID
SELECT id, username FROM users;

-- Add admin role (replace USER_ID with actual ID)
INSERT INTO user_roles (user_id, role_id)
VALUES (USER_ID, (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));
```

### 2. Create a Regular User

1. Go to http://localhost:3000/signup
2. Fill in the registration form
3. Login with your credentials

### 3. Post Your First Property

1. Click "Post Property" in the navbar
2. Fill in property details
3. Submit (status will be PENDING)
4. Admin can approve it from Admin Dashboard

## Default Test Data

You can manually insert test data using:

```sql
-- Example: Insert a test property (after creating a user)
INSERT INTO properties (title, description, type, listing_type, price, address, city, district, 
    bedrooms, bathrooms, land_size, status, owner_id, created_at, updated_at)
VALUES (
    'Beautiful 3BR House in Colombo',
    'Spacious modern house with garden',
    'HOUSE',
    'SALE',
    25000000,
    '123 Main Street',
    'Colombo',
    'Colombo',
    3,
    2,
    15,
    'APPROVED',
    1,  -- Replace with actual user ID
    NOW(),
    NOW()
);
```

## Common Issues & Solutions

### Issue: Port 8080 already in use
**Solution:** Stop other applications using port 8080, or change port in `application.properties`:
```properties
server.port=8081
```

### Issue: Port 3000 already in use
**Solution:** Run on different port:
```bash
PORT=3001 npm start
```

### Issue: Database connection error
**Solution:** 
1. Verify PostgreSQL is running: `pg_isready`
2. Check credentials in `application.properties`
3. Ensure database `realestate_db` exists

### Issue: "Role not found" error on signup
**Solution:** The backend auto-creates roles on first startup. Restart the backend:
```bash
# Stop backend (Ctrl+C)
# Start again
mvn spring-boot:run
```

### Issue: CORS errors
**Solution:** Ensure backend is configured for `http://localhost:3000` in `application.properties`

## Testing the Application

### Test User Flow:
1. ✅ Register account
2. ✅ Login
3. ✅ Browse properties
4. ✅ Search & filter properties
5. ✅ Add property to favorites
6. ✅ Post new property
7. ✅ Edit your property
8. ✅ Delete your property

### Test Admin Flow:
1. ✅ Login as admin
2. ✅ Go to Admin Dashboard
3. ✅ Approve/reject properties
4. ✅ Manage users
5. ✅ Delete any property

## Sample Property Data for Testing

Here's JSON data you can use when posting properties:

**Features (JSON Array):**
```json
["Swimming Pool", "Air Conditioning", "Security System", "Parking", "Garden"]
```

**Images (JSON Array):**
```json
["https://images.unsplash.com/photo-1568605114967-8130f3a36994", "https://images.unsplash.com/photo-1570129477492-45c003edd2be"]
```

## API Testing with cURL

Test the backend directly:

```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Get properties (no auth required)
curl http://localhost:8080/api/properties
```

## Next Steps

1. **Customize the styling** - Edit Tailwind configuration in `frontend/tailwind.config.js`
2. **Add more districts/cities** - Update `frontend/src/utils/constants.js`
3. **Configure production settings** - Update JWT secret, database credentials
4. **Deploy** - Follow deployment guides in individual README files

## Resources

- Backend README: `backend/README.md`
- Frontend README: `frontend/README.md`
- Main README: `README.md`

## Need Help?

- Check console logs in browser (F12)
- Check backend logs in terminal
- Review API endpoints in `backend/README.md`
- Check network tab in browser DevTools

---

**Happy Coding! 🎉**

If you encounter any issues, feel free to create an issue in the repository.

