import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/auth';

// Create axios instance with default config
const authAPI = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Auth service functions
export const authService = {
  // Login user
  login: async (email, password) => {
    try {
      const response = await authAPI.post('/login', {
        email,
        password,
      });
      return {
        success: true,
        data: response.data,
        message: 'Login successful'
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Login failed. Please try again.',
        status: error.response?.status
      };
    }
  },

  // Register user
  signup: async (username, email, password) => {
    try {
      const response = await authAPI.post('/signup', {
        username,
        email,
        password,
      });
      return {
        success: true,
        data: response.data,
        message: 'Account created successfully'
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Registration failed. Please try again.',
        status: error.response?.status
      };
    }
  },

  // Logout user (client-side)
  logout: () => {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    return { success: true, message: 'Logged out successfully' };
  },

  // Get current user from localStorage
  getCurrentUser: () => {
    try {
      const user = localStorage.getItem('user');
      return user ? JSON.parse(user) : null;
    } catch (error) {
      return null;
    }
  },

  // Save user to localStorage
  saveUser: (user) => {
    localStorage.setItem('user', JSON.stringify(user));
  },

  // Check if user is authenticated
  isAuthenticated: () => {
    const user = authService.getCurrentUser();
    return user !== null;
  }
};

export default authService;
