import './utils/ReactPatch'; // Nuclear patch - must be first!
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from './contexts/ThemeContext';
import ErrorBoundary from './components/ErrorBoundary/ErrorBoundary';
import LoginSignup from './components/LoginSignup/LoginSignup';
import Dashboard from './components/Dashboard/Dashboard';
import CarListing from './components/CarListing/CarListing';
import CarDetails from './components/CarDetails/CarDetails';
import BookingConfirmation from './components/BookingConfirmation/BookingConfirmation';
import Bookings from './components/Bookings/Bookings';
import AdminDashboard from './components/admin/AdminDashboard';
import './App.css';

function App() {
  return (
    <ErrorBoundary>
      <ThemeProvider>
        <Router>
          <div className="App">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/login" element={<LoginSignup />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/cars" element={<CarListing />} />
              <Route path="/car/:id" element={<CarDetails />} />
              <Route path="/bookings" element={<Bookings />} />
              <Route path="/booking-confirmation" element={<BookingConfirmation />} />
              <Route path="/admin" element={<AdminDashboard />} />
              {/* Redirect any unknown paths to the dashboard */}
              <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
          </div>
        </Router>
      </ThemeProvider>
    </ErrorBoundary>
  );
}

export default App;
