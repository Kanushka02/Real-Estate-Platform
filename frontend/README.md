# Real Estate Frontend - React Application

## Overview
This is the frontend application for the Real Estate Listing Platform built with React and Tailwind CSS.

## Features
- 🏠 Browse and search properties
- 🔍 Advanced filtering by type, location, price, etc.
- ⭐ Save favorite properties
- 📝 Post and manage your own properties
- 🧑‍💼 Admin dashboard for property and user management
- 🔐 JWT-based authentication
- 📱 Responsive design with Tailwind CSS

## Technologies
- **Framework:** React 18
- **Styling:** Tailwind CSS
- **Routing:** React Router v6
- **HTTP Client:** Axios
- **Build Tool:** Create React App

## Prerequisites
- Node.js 16 or higher
- npm or yarn

## Installation

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The application will open in your browser at `http://localhost:3000`

## Available Scripts

- `npm start` - Runs the app in development mode
- `npm build` - Builds the app for production
- `npm test` - Runs the test suite
- `npm eject` - Ejects from Create React App (one-way operation)

## Project Structure

```
src/
├── components/          # Reusable components
│   ├── Navbar.js
│   ├── Footer.js
│   ├── PropertyCard.js
│   └── PropertyFilter.js
├── pages/              # Page components
│   ├── Home.js
│   ├── Login.js
│   ├── Signup.js
│   ├── Properties.js
│   ├── PropertyDetails.js
│   ├── PropertyForm.js
│   ├── MyProperties.js
│   ├── Favorites.js
│   └── AdminDashboard.js
├── context/            # React Context
│   └── AuthContext.js
├── services/           # API services
│   └── api.js
├── utils/              # Utilities
│   └── constants.js
├── App.js              # Main app component
├── index.js            # Entry point
└── index.css           # Global styles
```

## Features Guide

### User Features
1. **Browse Properties**: View all available properties with pagination
2. **Search**: Search properties by keywords
3. **Filter**: Filter by type, location, price range, bedrooms, etc.
4. **Property Details**: View detailed information about each property
5. **Favorites**: Save properties to favorites for later viewing
6. **Post Property**: Create new property listings
7. **Manage Properties**: Edit or delete your own properties

### Admin Features
1. **Property Management**: View, approve, reject, or delete all properties
2. **User Management**: View and manage user accounts
3. **Status Control**: Activate or deactivate user accounts

## Configuration

The API base URL is set in `src/services/api.js`:
```javascript
const API_URL = 'http://localhost:8080/api';
```

Update this URL if your backend is running on a different address.

## Authentication

The app uses JWT tokens for authentication:
- Tokens are stored in localStorage
- Automatically added to API requests via Axios interceptors
- User state managed through React Context

## Styling

Tailwind CSS is configured with custom colors and components:
- Primary color palette based on blue shades
- Custom utility classes for buttons, inputs, and cards
- Responsive breakpoints for mobile, tablet, and desktop

## Sri Lanka-Specific Features

- All 25 districts of Sri Lanka
- Major cities by district
- Price formatting in Sri Lankan Rupees (Rs.)
- Perches for land measurement
- Square feet for floor size

## Build for Production

1. Create production build:
```bash
npm run build
```

2. The build folder will contain optimized production files

3. Deploy the contents of the `build` folder to your hosting service

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

This project is part of the Real Estate Listing Platform.

