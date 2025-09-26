// Simple test to verify error handling works correctly
import { handleApiError } from './errorHandler';

// Test structured error response
const testStructuredError = {
  response: {
    status: 400,
    data: {
      errorCode: 'VALIDATION_ERROR',
      message: 'Validation failed for one or more fields',
      details: {
        email: 'Please provide a valid email address',
        password: 'Password must be at least 6 characters long'
      },
      timestamp: '2024-01-15T10:30:45'
    }
  }
};

// Test simple string error
const testStringError = {
  response: {
    status: 401,
    data: 'Invalid Credentials'
  }
};

// Test with just message
const testMessageError = {
  response: {
    status: 404,
    data: {
      message: 'User not found'
    }
  }
};

console.log('Testing structured error:', handleApiError(testStructuredError));
// Expected: "Validation failed for one or more fields"

console.log('Testing string error:', handleApiError(testStringError));
// Expected: "Invalid Credentials"

console.log('Testing message error:', handleApiError(testMessageError));
// Expected: "User not found"

console.log('Testing validation details:', handleApiError({
  response: {
    status: 400,
    data: {
      details: {
        email: 'Email is required',
        username: 'Username must be at least 3 characters'
      }
    }
  }
}));
// Expected: "Email is required, Username must be at least 3 characters"