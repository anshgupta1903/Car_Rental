import React from 'react';

/**
 * Emergency component that ensures NOTHING renders as an object
 * This component will force ANY input to be a safe string
 */
export const EmergencySafeText = ({ children, fallback = "Error occurred", ...props }) => {
  let safeContent;
  
  try {
    if (children === null || children === undefined) {
      return null;
    }
    
    if (typeof children === 'string' || typeof children === 'number') {
      safeContent = String(children);
    } else if (typeof children === 'boolean') {
      safeContent = children ? 'true' : 'false';
    } else if (typeof children === 'object') {
      console.error('EMERGENCY: Prevented object from rendering as React child:', children);
      
      // Try to extract meaningful text from object
      if (children.message && typeof children.message === 'string') {
        safeContent = String(children.message);
      } else if (children.details && typeof children.details === 'object') {
        const details = Object.values(children.details).filter(d => typeof d === 'string');
        safeContent = details.length > 0 ? details.join(', ') : String(fallback);
      } else if (children.toString && typeof children.toString === 'function') {
        try {
          safeContent = String(children.toString());
        } catch (e) {
          console.error('Failed to convert object to string:', e);
          safeContent = String(fallback);
        }
      } else {
        console.error('Object keys:', Object.keys(children));
        safeContent = String(fallback);
      }
    } else {
      // For functions, symbols, etc.
      safeContent = String(fallback);
    }
    
    // Final safety check
    if (typeof safeContent !== 'string') {
      console.error('CRITICAL: safeContent is not a string:', safeContent, typeof safeContent);
      safeContent = String(fallback);
    }
    
  } catch (error) {
    console.error('Emergency conversion error:', error);
    safeContent = String(fallback);
  }
  
  return <span {...props}>{safeContent}</span>;
};

export default EmergencySafeText;