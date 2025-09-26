import axios from 'axios';
import { handleApiError, createApiResponse } from '../utils/errorHandler';

const API_BASE_URL = 'http://localhost:8081/api/cars';

// Create axios instance with default config
const carAPI = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Car service functions
export const carService = {
  // Get all available cars
  getAllAvailableCars: async () => {
    try {
      const response = await carAPI.get('/available');
      return createApiResponse(true, response.data, 'Cars fetched successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to fetch cars');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  // Get car by ID
  getCarById: async (id) => {
    try {
      const response = await carAPI.get(`/${id}`);
      return createApiResponse(true, response.data, 'Car fetched successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to fetch car details');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  // Get available car by ID
  getAvailableCarById: async (id) => {
    try {
      const response = await carAPI.get(`/available/${id}`);
      return createApiResponse(true, response.data, 'Car fetched successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Car not available or not found');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  // Search cars by keyword
  searchCars: async (keyword) => {
    try {
      const response = await carAPI.get('/search', {
        params: { keyword }
      });
      return {
        success: true,
        data: response.data,
        message: 'Search completed successfully'
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Search failed',
        status: error.response?.status
      };
    }
  },

  // Filter cars
  filterCars: async (filters) => {
    try {
      const response = await carAPI.get('/filter', {
        params: filters
      });
      return {
        success: true,
        data: response.data,
        message: 'Cars filtered successfully'
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Filter failed',
        status: error.response?.status
      };
    }
  },

  // Get cars by type
  getCarsByType: async (carType) => {
    try {
      const response = await carAPI.get(`/type/${carType}`);
      return {
        success: true,
        data: response.data,
        message: 'Cars fetched successfully'
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to fetch cars by type',
        status: error.response?.status
      };
    }
  },

  // Book a car
  bookCar: async (carId) => {
    try {
      const response = await carAPI.post(`/${carId}/book`);
      return {
        success: true,
        data: response.data,
        message: response.data.message || 'Car booked successfully'
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Booking failed',
        status: error.response?.status
      };
    }
  },

  // Get filter options
  getFilterOptions: async () => {
    try {
      const response = await carAPI.get('/filter-options');
      return createApiResponse(true, response.data, 'Filter options fetched successfully');
    } catch (error) {
      const errorMessage = handleApiError(error, 'Failed to fetch filter options');
      return createApiResponse(false, null, errorMessage, error.response?.status);
    }
  },

  // Check car availability
  checkCarAvailability: async (carId) => {
    try {
      const response = await carAPI.get(`/${carId}/availability`);
      return {
        success: true,
        data: response.data,
        message: 'Availability checked successfully'
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to check availability',
        status: error.response?.status
      };
    }
  },

  // Get car statistics (admin)
  getCarStatistics: async () => {
    try {
      const response = await carAPI.get('/statistics');
      return {
        success: true,
        data: response.data,
        message: 'Statistics fetched successfully'
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to fetch statistics',
        status: error.response?.status
      };
    }
  }
};

export default carService;