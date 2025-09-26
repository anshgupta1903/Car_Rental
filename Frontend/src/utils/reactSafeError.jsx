import React from 'react';

/**
 * Utility to ensure error messages are safe to render in React components
 * @param {any} error - The error value that might be an object or string
 * @param {string} fallback - Fallback message if error is not a string
 * @returns {string} - A safe string to render in React
 */
export const safeErrorMessage = (error, fallback = 'An error occurred. Please try again.') => {
  console.log('🔍 safeErrorMessage called with:', error, 'Type:', typeof error);
  
  // If error is already a string, return it
  if (typeof error === 'string' && error.trim()) {
    console.log('✅ Returning string error:', error);
    return error;
  }
  
  // If error is an object, try to extract meaningful information
  if (error && typeof error === 'object') {
    console.log('Processing error object:', error);
    
    // Try to get message from error object
    if (error.message && typeof error.message === 'string') {
      console.log('Extracted message from error object:', error.message);
      return error.message;
    }
    
    // Try to get details and join them
    if (error.details && typeof error.details === 'object') {
      const details = Object.values(error.details).filter(d => typeof d === 'string');
      if (details.length > 0) {
        console.log('Extracted details from error object:', details);
        return details.join(', ');
      }
    }
    
    // If error has errorCode, return a generic message with code
    if (error.errorCode && typeof error.errorCode === 'string') {
      const codeMessage = `Error: ${error.errorCode}`;
      console.log('Extracted errorCode from error object:', codeMessage);
      return codeMessage;
    }
    
    console.warn('CRITICAL: Attempted to render object as React child, using fallback:', error);
    console.warn('Object keys:', Object.keys(error));
    return fallback;
  }
  
  // For any other type (null, undefined, boolean, number), return fallback
  return fallback;
};

/**
 * React component wrapper for safe error display
 */
export const SafeErrorDisplay = ({ error, fallback, className = "error-message", ...props }) => {
  console.log('SafeErrorDisplay received error:', error, typeof error);
  const message = safeErrorMessage(error, fallback);
  
  if (!message) {
    console.log('SafeErrorDisplay: No message to display');
    return null;
  }
  
  console.log('SafeErrorDisplay rendering message:', message, typeof message);
  return (
    <div className={className} {...props}>
      {message}
    </div>
  );
};

export default safeErrorMessage;