import React from 'react';
import { safeErrorMessage } from '../../utils/reactSafeError';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, errorMessage: '' };
  }

  static getDerivedStateFromError(error) {
    // Update state so the next render will show the fallback UI
    console.error('ErrorBoundary caught error:', error);
    const errorMessage = safeErrorMessage(error, 'Something went wrong. Please refresh the page.');
    return { hasError: true, errorMessage };
  }

  componentDidCatch(error, errorInfo) {
    // Log the error to an error reporting service
    console.error('ErrorBoundary caught error:', error, errorInfo);
    
    // Log specific details if it's an object rendering error
    if (error.message && error.message.includes('Objects are not valid as a React child')) {
      console.error('CRITICAL: React child object error detected!');
      console.error('Error details:', error);
      console.error('Component stack:', errorInfo.componentStack);
    }
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{
          padding: '20px',
          margin: '20px',
          border: '1px solid #ff6b6b',
          borderRadius: '8px',
          backgroundColor: '#fff5f5',
          textAlign: 'center'
        }}>
          <h2 style={{ color: '#d63031' }}>Oops! Something went wrong</h2>
          <p style={{ color: '#2d3436' }}>{this.state.errorMessage}</p>
          <button 
            onClick={() => window.location.reload()}
            style={{
              padding: '10px 20px',
              backgroundColor: '#0984e3',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Refresh Page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;