import React from 'react';

// Store original createElement
const originalCreateElement = React.createElement;

// Patch createElement to catch object children
React.createElement = function(type, props, ...children) {
  // Check all children for objects
  const safeChildren = children.map((child, index) => {
    if (child && typeof child === 'object' && !React.isValidElement(child)) {
      console.error(`🚨 NUCLEAR PATCH: Prevented object child at index ${index}:`, child);
      
      // Try to extract meaningful text
      if (child.message && typeof child.message === 'string') {
        console.log('🔧 Extracted message:', child.message);
        return child.message;
      } else if (child.details && typeof child.details === 'object') {
        const details = Object.values(child.details).filter(d => typeof d === 'string');
        if (details.length > 0) {
          const extracted = details.join(', ');
          console.log('🔧 Extracted details:', extracted);
          return extracted;
        }
      } else if (child.errorCode) {
        const extracted = `Error: ${child.errorCode}`;
        console.log('🔧 Extracted error code:', extracted);
        return extracted;
      }
      
      // Last resort
      console.error('🔧 Using fallback for object:', Object.keys(child));
      return 'An error occurred';
    }
    return child;
  });
  
  // Also check props.children if it exists
  if (props && props.children) {
    if (typeof props.children === 'object' && !React.isValidElement(props.children) && !Array.isArray(props.children)) {
      console.error('🚨 NUCLEAR PATCH: Prevented object in props.children:', props.children);
      props = { 
        ...props, 
        children: props.children.message || 'An error occurred' 
      };
    }
  }
  
  return originalCreateElement.call(this, type, props, ...safeChildren);
};

console.log('🔥 NUCLEAR REACT PATCH ACTIVATED');

export default React;