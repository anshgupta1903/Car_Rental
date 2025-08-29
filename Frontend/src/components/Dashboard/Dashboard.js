import React from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import ThemeToggle from '../ThemeToggle/ThemeToggle';
import './Dashboard.css';

const Dashboard = () => {
  const navigate = useNavigate();
  const user = authService.getCurrentUser();

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  if (!user) {
    navigate('/login');
    return null;
  }

  return (
    <div className="dashboard-container">
      <ThemeToggle />
      
      <header className="dashboard-header">
        <div className="header-content">
          <div className="logo">
            <i className="fas fa-car-alt"></i>
            <span>CarRental</span>
          </div>
          
          <div className="user-menu">
            <div className="user-info">
              <span>Welcome, {user.username || user.email}</span>
            </div>
            <button className="logout-btn" onClick={handleLogout}>
              <i className="fas fa-sign-out-alt"></i>
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="dashboard-main">
        <div className="container">
          <div className="welcome-section">
            <h1>Welcome to CarRental Dashboard</h1>
            <p>Your premium car rental experience starts here</p>
          </div>

          <div className="dashboard-grid">
            <div className="dashboard-card">
              <div className="card-icon">
                <i className="fas fa-car"></i>
              </div>
              <h3>Browse Cars</h3>
              <p>Explore our premium fleet of vehicles</p>
              <button className="card-btn">View Cars</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon">
                <i className="fas fa-calendar-alt"></i>
              </div>
              <h3>My Bookings</h3>
              <p>Manage your current and past reservations</p>
              <button className="card-btn">View Bookings</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon">
                <i className="fas fa-user-cog"></i>
              </div>
              <h3>Profile</h3>
              <p>Update your personal information</p>
              <button className="card-btn">Edit Profile</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon">
                <i className="fas fa-headset"></i>
              </div>
              <h3>Support</h3>
              <p>Get help with your rental experience</p>
              <button className="card-btn">Contact Support</button>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
