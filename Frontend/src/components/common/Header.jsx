import React from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import Icon from './Icon';
import './Header.css';

const Header = ({ showNavigation = true, title = null }) => {
  const navigate = useNavigate();
  const user = authService.getCurrentUser();

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  const hasRole = (user, role) => {
    return user && user.roles && user.roles.some(r => r === role);
  };

  const isAdminOrManager = () => {
    return hasRole(user, 'ROLE_ADMIN') || hasRole(user, 'ROLE_MANAGER');
  };

  return (
    <header className="modern-header">
      <div className="header-container">
        <div 
          className="brand" 
          onClick={() => navigate('/')}
          role="button"
          tabIndex={0}
        >
          <Icon name="car" size="lg" className="brand-icon" />
          <span className="brand-text">CarRental</span>
        </div>

        {title && (
          <div className="page-title">
            <h1>{title}</h1>
          </div>
        )}

        {showNavigation && (
          <nav className="nav-menu">
            {!isAdminOrManager() && (
              <>
                <button 
                  onClick={() => navigate('/cars')} 
                  className="nav-btn"
                >
                  <Icon name="search" size="sm" />
                  Browse Cars
                </button>
                <button 
                  onClick={() => navigate('/bookings')} 
                  className="nav-btn nav-btn-accent"
                >
                  <Icon name="calendar" size="sm" />
                  My Bookings
                </button>
              </>
            )}
            {isAdminOrManager() && (
              <>
                <button 
                  onClick={() => navigate('/cars')} 
                  className="nav-btn"
                >
                  View Inventory
                </button>
                <button 
                  onClick={() => navigate('/admin')} 
                  className="nav-btn nav-btn-primary"
                >
                  <Icon name="settings" size="sm" />
                  Admin Panel
                </button>
              </>
            )}
          </nav>
        )}

        <div className="user-section">
          {user && (
            <div className="user-info">
              <Icon name="user" size="sm" />
              <span className="user-name">
                {user.username || user.email?.split('@')[0]}
              </span>
            </div>
          )}
          
          {user ? (
            <button className="btn btn-secondary" onClick={handleLogout}>
              <Icon name="logout" size="sm" />
              Logout
            </button>
          ) : (
            <button className="btn btn-primary" onClick={() => navigate('/login')}>
              <Icon name="user" size="sm" />
              Login
            </button>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;