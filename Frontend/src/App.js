import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from './contexts/ThemeContext';
import LoginSignup from './components/LoginSignup/LoginSignup';
import Dashboard from './components/Dashboard/Dashboard';
import './App.css';

function App() {
  return (
    <ThemeProvider>
      <Router>
        <div className="App">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/login" element={<LoginSignup />} />
            <Route path="/dashboard" element={<Dashboard />} />
            {/* Redirect any unknown paths to the dashboard */}
            <Route path="*" element={<Navigate to="/dashboard" />} />
          </Routes>
        </div>
      </Router>
    </ThemeProvider>
  );
}

export default App;
