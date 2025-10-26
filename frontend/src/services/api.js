import axios from 'axios';

const API_URL = 'http://localhost:8083/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (username, password) => 
    api.post('/auth/login', { username, password }),
  
  signup: (userData) => 
    api.post('/auth/signup', userData),
};

// Property API
export const propertyAPI = {
  getAll: (page = 0, size = 12) => 
    api.get(`/properties?page=${page}&size=${size}`),
  
  getById: (id) => 
    api.get(`/properties/${id}`),
  
  getMyProperties: (page = 0, size = 10) => 
    api.get(`/properties/my-properties?page=${page}&size=${size}`),
  
  search: (keyword, page = 0, size = 12) => 
    api.get(`/properties/search?keyword=${keyword}&page=${page}&size=${size}`),
  
  filter: (filters, page = 0, size = 12) => {
    const params = new URLSearchParams();
    params.append('page', page);
    params.append('size', size);
    
    Object.keys(filters).forEach(key => {
      if (filters[key] !== null && filters[key] !== undefined && filters[key] !== '') {
        params.append(key, filters[key]);
      }
    });
    
    return api.get(`/properties/filter?${params.toString()}`);
  },
  
  getLatest: () => 
    api.get('/properties/latest'),
  
  getFeatured: () => 
    api.get('/properties/featured'),
  
  create: (propertyData) => 
    api.post('/properties', propertyData),
  
  update: (id, propertyData) => 
    api.put(`/properties/${id}`, propertyData),
  
  delete: (id) => 
    api.delete(`/properties/${id}`),
};

// Favorite API
export const favoriteAPI = {
  getAll: () => 
    api.get('/favorites'),
  
  add: (propertyId) => 
    api.post(`/favorites/${propertyId}`),
  
  remove: (propertyId) => 
    api.delete(`/favorites/${propertyId}`),
  
  check: (propertyId) => 
    api.get(`/favorites/${propertyId}/check`),
};

// Admin API
export const adminAPI = {
  getAllProperties: (page = 0, size = 10) => 
    api.get(`/admin/properties?page=${page}&size=${size}`),
  
  approveProperty: (id) => 
    api.put(`/admin/properties/${id}/approve`),
  
  rejectProperty: (id) => 
    api.put(`/admin/properties/${id}/reject`),
  
  deleteProperty: (id) => 
    api.delete(`/admin/properties/${id}`),
  
  getAllUsers: () => 
    api.get('/admin/users'),
  
  getUser: (id) => 
    api.get(`/admin/users/${id}`),
  
  toggleUserStatus: (id) => 
    api.put(`/admin/users/${id}/toggle-status`),
};

export default api;

