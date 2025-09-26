import axios from 'axios';
import { handleApiError, createApiResponse } from '../utils/errorHandler';

const API_BASE_URL = 'http://localhost:8081';

// Create axios instance with default config for admin operations
const adminAPI = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include credentials for authenticated requests
adminAPI.interceptors.request.use(
  (config) => {
    const credentials = localStorage.getItem('credentials');
    if (credentials) {
      config.headers.Authorization = `Basic ${credentials}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const adminService = {
  // Admin Authentication
  signupAdmin: async (username, email, password) => {
    try {
      const response = await adminAPI.post('/auth/signup/admin', {
        username,
        email,
        password,
      });
      return createApiResponse(true, response.data, 'Admin account created successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Admin registration failed. Please try again.');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  signupManager: async (username, email, password) => {
    try {
      const response = await adminAPI.post('/auth/signup/manager', {
        username,
        email,
        password,
      });
      return createApiResponse(true, response.data, 'Manager account created successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Manager registration failed. Please try again.');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  // Order Management
  getPendingOrders: async () => {
    try {
      const response = await adminAPI.get('/api/orders/pending');
      return createApiResponse(true, response.data, 'Pending orders retrieved successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to retrieve pending orders');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  getAllOrders: async () => {
    try {
      const response = await adminAPI.get('/api/orders/all');
      return createApiResponse(true, response.data, 'All orders retrieved successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to retrieve orders');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  getOrdersByStatus: async (status) => {
    try {
      const response = await adminAPI.get(`/api/orders/status/${status}`);
      return createApiResponse(true, response.data, `${status} orders retrieved successfully`);
    } catch (error) {
      const errorMessage = handleApiError(error, `Failed to retrieve ${status} orders`);
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  approveOrder: async (orderId, adminNotes = '') => {
    try {
      const response = await adminAPI.put(`/api/orders/${orderId}/approve`, {
        adminNotes,
      });
      return createApiResponse(true, response.data, 'Order approved successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to approve order');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  rejectOrder: async (orderId, adminNotes = '') => {
    try {
      const response = await adminAPI.put(`/api/orders/${orderId}/reject`, {
        adminNotes,
      });
      return createApiResponse(true, response.data, 'Order rejected successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to reject order');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  completeOrder: async (orderId, adminNotes = '') => {
    try {
      const response = await adminAPI.put(`/api/orders/${orderId}/complete`, {
        adminNotes,
      });
      return createApiResponse(true, response.data, 'Order completed successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to complete order');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  getOrderStatistics: async () => {
    try {
      const response = await adminAPI.get('/api/orders/statistics');
      return createApiResponse(true, response.data, 'Order statistics retrieved successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to retrieve order statistics');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  // Car Management
  getAllCarsForAdmin: async () => {
    try {
      const response = await adminAPI.get('/api/cars/all');
      return createApiResponse(true, response.data, 'All cars retrieved successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to retrieve cars');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  addCar: async (carData) => {
    try {
      const response = await adminAPI.post('/api/cars', carData);
      return createApiResponse(true, response.data, 'Car added successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to add car');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  updateCar: async (carId, carData) => {
    try {
      const response = await adminAPI.put(`/api/cars/${carId}`, carData);
      return createApiResponse(true, response.data, 'Car updated successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to update car');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  deleteCar: async (carId) => {
    try {
      const response = await adminAPI.delete(`/api/cars/${carId}`);
      return createApiResponse(true, response.data, 'Car deleted successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to delete car');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  toggleCarAvailability: async (carId) => {
    try {
      const response = await adminAPI.put(`/api/admin/cars/${carId}/toggle-availability`);
      return createApiResponse(true, response.data, 'Car availability updated successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to update car availability');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  getCarStatistics: async () => {
    try {
      const response = await adminAPI.get('/api/admin/cars/statistics');
      return createApiResponse(true, response.data, 'Car statistics retrieved successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to retrieve car statistics');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  updateCarPricing: async (carId, pricePerDay) => {
    try {
      const response = await adminAPI.put(`/api/admin/cars/${carId}/pricing`, {
        pricePerDay,
      });
      return createApiResponse(true, response.data, 'Car pricing updated successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to update car pricing');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },
};

export default adminService;