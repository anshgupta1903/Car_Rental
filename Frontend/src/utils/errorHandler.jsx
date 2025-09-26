/**
 * Utility function to handle and format API error responses
 * @param {Error} error - The error object from axios
 * @param {string} defaultMessage - Default message if no specific error message is found
 * @returns {string} - Formatted error message
 */
export const handleApiError = (error, defaultMessage = 'An error occurred. Please try again.') => {
  console.error('API Error:', error);
  console.log('API Error details:', JSON.stringify(error, null, 2));
  
  if (!error.response) {
    return 'Network error. Please check your connection.';
  }

  const { data, status } = error.response;

  // Handle different error response formats
  if (typeof data === 'string') {
    return data;
  }

  if (data && typeof data === 'object') {
    // Handle field validation errors from FormDetailsController
    if (data.fieldErrors && typeof data.fieldErrors === 'object') {
      const fieldErrors = Object.entries(data.fieldErrors)
        .map(([field, message]) => `${field}: ${message}`)
        .join('; ');
      return fieldErrors;
    }

    // Handle structured error response from GlobalExceptionHandler
    if (data.message) {
      return data.message;
    }

    // Handle validation errors with details
    if (data.details && typeof data.details === 'object') {
      const validationErrors = Object.values(data.details);
      if (validationErrors.length > 0) {
        return validationErrors.join(', ');
      }
    }

    // Handle error response with errorCode
    if (data.errorCode) {
      return data.message || `Error: ${data.errorCode}`;
    }
  }

  // Handle HTTP status codes
  switch (status) {
    case 400:
      return 'Bad request. Please check your input.';
    case 401:
      return 'Unauthorized. Please login again.';
    case 403:
      return 'Access denied. You don\'t have permission to perform this action.';
    case 404:
      return 'Resource not found.';
    case 409:
      return 'Conflict. The resource already exists.';
    case 500:
      return 'Server error. Please try again later.';
    default:
      const result = defaultMessage;
      console.log('Returning default message:', result);
      // Safety check: ensure we never return an object
      if (typeof result === 'object') {
        console.error('WARNING: Attempted to return object as error message:', result);
        return 'An error occurred. Please try again.';
      }
      return result;
  }
};

/**
 * Creates a standardized API response object
 * @param {boolean} success - Whether the operation was successful
 * @param {any} data - The response data (for successful operations)
 * @param {string} message - Success or error message
 * @param {number} status - HTTP status code
 * @returns {Object} - Standardized response object
 */
export const createApiResponse = (success, data = null, message = '', status = null) => {
  // Safety check for message parameter
  if (typeof message === 'object') {
    console.error('WARNING: Attempted to pass object as message in createApiResponse:', message);
    message = 'An error occurred. Please try again.';
  }
  
  const response = {
    success,
    data,
    message,
    status
  };
  
  console.log('Creating API response:', response);
  return response;
};
