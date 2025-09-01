import axios from 'axios';
import Cookies from 'js-cookie';

// Create axios instance with base configuration
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8082/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Token management
const TOKEN_KEY = 'propertylk_token';
const USER_KEY = 'propertylk_user';

export const tokenService = {
  getToken: () => {
    return Cookies.get(TOKEN_KEY);
  },
  setToken: (token) => {
    try {
      console.log('Attempting to store token:', token.substring(0, 20) + '...');
      Cookies.set(TOKEN_KEY, token, { expires: 1, secure: true, sameSite: 'strict' });
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      console.log('Token stored successfully');
    } catch (error) {
      console.error('Error storing token:', error);
      throw error; // Propagate error to catch in register
    }
  },
  
  removeToken: () => {
    Cookies.remove(TOKEN_KEY);
    delete api.defaults.headers.common['Authorization'];
  },
  
  setUser: (user) => {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },
  
  getUser: () => {
    const user = localStorage.getItem(USER_KEY);
    return user ? JSON.parse(user) : null;
  },
  
  removeUser: () => {
    localStorage.removeItem(USER_KEY);
  },
  
  isAuthenticated: () => {
    return !!Cookies.get(TOKEN_KEY);
  }
};

// Set token if exists on app load
const token = tokenService.getToken();
if (token) {
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = tokenService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for handling errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      tokenService.removeToken();
      tokenService.removeUser();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  register: async (userData) => {
    try {
      const response = await api.post('/auth/register', userData);
      console.log('authAPI.register raw response:', response);
      console.log('authAPI.register response.data:', response.data);
      return response.data;
    } catch (error) {
      console.error('authAPI.register error:', error, 'Response:', error.response);
      throw error;
    }
  },
  
  login: async (credentials) => {
    const response = await api.post('/auth/login', credentials);
    return response.data;
  },
  
  validateToken: async () => {
    const response = await api.get('/auth/validate');
    return response.data;
  },

  logout: () => {
    tokenService.removeToken();
    tokenService.removeUser();
  },
  isValidToken: (token) => {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000; // Convert to milliseconds
      return Date.now() < expiry;
    } catch (error) {
      console.error('Invalid token:', error);
      return false;
    }
  }
};

// Properties API
export const propertiesAPI = {
  // Public endpoints
  getAll: async (filters = {}) => {
    const params = new URLSearchParams();
    Object.keys(filters).forEach(key => {
      if (filters[key]) params.append(key, filters[key]);
    });
    const response = await api.get(`/properties/all?${params}`);
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`/properties/${id}`);
    return response.data;
  },
  
  search: async (keyword) => {
    const response = await api.get(`/properties/search`, { params: { keyword } });
    return response.data;
  },
  
  getByDistrict: async (district) => {
    const response = await api.get(`/properties/district/${district}`);
    return response.data;
  },
  
  getForSale: async () => {
    const response = await api.get('/properties/for-sale');
    return response.data;
  },
  
  getForRent: async () => {
    const response = await api.get('/properties/for-rent');
    return response.data;
  },
  
  getByBedrooms: async (bedrooms) => {
    const response = await api.get(`/properties/bedrooms/${bedrooms}`);
    return response.data;
  },

  // Advanced search with multiple filters
  searchWithFilters: async (filters) => {
    const params = new URLSearchParams();
    Object.keys(filters).forEach(key => {
      if (filters[key] && filters[key] !== '') {
        params.append(key, filters[key]);
      }
    });
    const response = await api.get(`/properties/search/advanced?${params}`);
    return response.data;
  },
  
  // Authenticated endpoints
  create: async (propertyData) => {
    const response = await api.post('/properties/secure/create', propertyData);
    return response.data;
  },
  
  update: async (id, propertyData) => {
    const response = await api.put(`/properties/secure/update/${id}`, propertyData);
    return response.data;
  },
  
  delete: async (id) => {
    const response = await api.delete(`/properties/secure/delete/${id}`);
    return response.data;
  },
  
  getMyProperties: async () => {
    const response = await api.get('/properties/secure/my-properties');
    return response.data;
  },
  
  toggleStatus: async (id) => {
    const response = await api.patch(`/properties/secure/toggle-status/${id}`);
    return response.data;
  },
  
  // Helper endpoints for forms
  getLocations: async () => {
    const response = await api.get('/properties/secure/locations');
    return response.data;
  },
  
  getCategories: async () => {
    const response = await api.get('/properties/secure/categories');
    return response.data;
  }
};

// Admin API
export const adminAPI = {
  getDashboardStats: async () => {
    const response = await api.get('/admin/dashboard/stats');
    return response.data;
  },
  
  getAllUsers: async () => {
    const response = await api.get('/admin/users');
    return response.data;
  },
  
  getUsersByRole: async (role) => {
    const response = await api.get(`/admin/users/role/${role}`);
    return response.data;
  },
  
  toggleUserStatus: async (userId) => {
    const response = await api.patch(`/admin/users/${userId}/toggle-status`);
    return response.data;
  },
  
  getAllPropertiesForAdmin: async () => {
    const response = await api.get('/admin/properties');
    return response.data;
  },
  
  changePropertyStatus: async (propertyId, status) => {
    const response = await api.patch(`/admin/properties/${propertyId}/status`, null, {
      params: { status }
    });
    return response.data;
  },
  
  deletePropertyAsAdmin: async (propertyId) => {
    const response = await api.delete(`/admin/properties/${propertyId}`);
    return response.data;
  },
  
  getRecentActivities: async () => {
    const response = await api.get('/admin/recent-activities');
    return response.data;
  },
  
  searchUsers: async (query) => {
    const response = await api.get('/admin/users/search', { params: { query } });
    return response.data;
  },
  
  getUserDetails: async (userId) => {
    const response = await api.get(`/admin/users/${userId}/details`);
    return response.data;
  }
};

// Generic API error handler
export const handleAPIError = (error) => {
  if (error.response) {
    // Server responded with error status
    const { status, data } = error.response;
    if (status === 401) {
      return 'Authentication required. Please login again.';
    } else if (status === 403) {
      return 'You do not have permission to perform this action.';
    } else if (status === 404) {
      return 'The requested resource was not found.';
    } else if (status === 500) {
      return 'Server error. Please try again later.';
    } else if (data && data.message) {
      return data.message;
    }
  } else if (error.request) {
    // Request was made but no response received
    return 'Network error. Please check your connection.';
  }
  
  // Something else happened
  return error.message || 'An unexpected error occurred.';
};

export default api;