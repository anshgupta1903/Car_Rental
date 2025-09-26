import axios from 'axios';
import { authService } from './authService';
import { safeErrorMessage } from '../utils/reactSafeError';

const API_BASE_URL = 'http://localhost:8081/api';

// Create axios instance for authenticated requests
const createAuthenticatedAPI = () => {
  const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Add request interceptor to include authentication headers
  api.interceptors.request.use(
    (config) => {
      const credentials = authService.getStoredCredentials();
      if (credentials) {
        config.headers.Authorization = `Basic ${credentials}`;
        console.log('Adding Basic Auth header to request:', config.url);
      } else {
        console.warn('No stored credentials found for authenticated request:', config.url);
      }
      return config;
    },
    (error) => {
      console.error('Request interceptor error:', error);
      return Promise.reject(error);
    }
  );

  // Add response interceptor to handle authentication errors
  api.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response && error.response.status === 401) {
        console.error('Authentication failed - redirecting to login');
        // Clear stored credentials and user data
        authService.logout();
        // Redirect to login page
        window.location.href = '/login';
        return Promise.reject(new Error('Authentication failed'));
      }
      return Promise.reject(error);
    }
  );

  return api;
};

// API service for authenticated requests
export const apiService = {
  // Generic authenticated GET request
  get: async (endpoint) => {
    try {
      const api = createAuthenticatedAPI();
      const response = await api.get(endpoint);
      return {
        success: true,
        data: response.data,
        message: 'Request successful'
      };
    } catch (error) {
      console.error('API GET error:', error);
      return {
        success: false,
        data: null,
        message: safeErrorMessage(error, 'Request failed'),
        status: error.response?.status
      };
    }
  },

  // Generic authenticated POST request
  post: async (endpoint, data) => {
    try {
      const api = createAuthenticatedAPI();
      const response = await api.post(endpoint, data);
      return {
        success: true,
        data: response.data,
        message: 'Request successful'
      };
    } catch (error) {
      console.error('API POST error:', error);
      return {
        success: false,
        data: null,
        message: safeErrorMessage(error, 'Request failed'),
        status: error.response?.status
      };
    }
  },

  // Generic authenticated PUT request
  put: async (endpoint, data) => {
    try {
      const api = createAuthenticatedAPI();
      const response = await api.put(endpoint, data);
      return {
        success: true,
        data: response.data,
        message: 'Request successful'
      };
    } catch (error) {
      console.error('API PUT error:', error);
      return {
        success: false,
        data: null,
        message: safeErrorMessage(error, 'Request failed'),
        status: error.response?.status
      };
    }
  },

  // Generic authenticated DELETE request
  delete: async (endpoint) => {
    try {
      const api = createAuthenticatedAPI();
      const response = await api.delete(endpoint);
      return {
        success: true,
        data: response.data,
        message: 'Request successful'
      };
    } catch (error) {
      console.error('API DELETE error:', error);
      return {
        success: false,
        data: null,
        message: safeErrorMessage(error, 'Request failed'),
        status: error.response?.status
      };
    }
  }
};

export default apiService;