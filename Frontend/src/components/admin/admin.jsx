import React from 'react';
import './admin.css';

const Admin = () => {
  return (
    <div className="admin-container">
      <div className="admin-header">
        <h1>Admin Dashboard</h1>
      </div>
      
      <div className="admin-content">
        <div className="admin-sidebar">
          <nav className="admin-nav">
            <ul>
              <li><a href="#users">Manage Users</a></li>
              <li><a href="#bookings">Manage Bookings</a></li>
              <li><a href="#cars">Manage Cars</a></li>
              <li><a href="#reports">Reports</a></li>
            </ul>
          </nav>
        </div>
        
        <div className="admin-main">
          <div className="admin-stats">
            <div className="stat-card">
              <h3>Total Users</h3>
              <p className="stat-number">0</p>
            </div>
            <div className="stat-card">
              <h3>Active Bookings</h3>
              <p className="stat-number">0</p>
            </div>
            <div className="stat-card">
              <h3>Available Cars</h3>
              <p className="stat-number">0</p>
            </div>
          </div>
          
          <div className="admin-actions">
            <h2>Quick Actions</h2>
            <button className="admin-btn">Add New Car</button>
            <button className="admin-btn">View All Bookings</button>
            <button className="admin-btn">Generate Report</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Admin;
