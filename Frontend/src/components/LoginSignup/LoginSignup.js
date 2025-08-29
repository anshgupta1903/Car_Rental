import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../../contexts/ThemeContext';
import { authService } from '../../services/authService';
import ThemeToggle from '../ThemeToggle/ThemeToggle';
import './LoginSignup.css';

const LoginSignup = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  
  const navigate = useNavigate();
  const { } = useTheme();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!isLogin && !formData.username.trim()) {
      newErrors.username = 'Username is required';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email is invalid';
    }

    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }

    if (!isLogin && formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setLoading(true);
    
    try {
      let result;
      
      if (isLogin) {
        result = await authService.login(formData.email, formData.password);
      } else {
        result = await authService.signup(formData.username, formData.email, formData.password);
      }

      if (result.success) {
        if (isLogin) {
          authService.saveUser(result.data);
          navigate('/dashboard');
        } else {
          // After successful signup, switch to login
          setIsLogin(true);
          setFormData({
            username: '',
            email: '',
            password: '',
            confirmPassword: ''
          });
          alert('Account created successfully! Please login.');
        }
      } else {
        setErrors({ submit: result.message });
      }
    } catch (error) {
      setErrors({ submit: 'An unexpected error occurred. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  const toggleMode = () => {
    setIsLogin(!isLogin);
    setFormData({
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    });
    setErrors({});
  };

  const togglePasswordVisibility = (field) => {
    if (field === 'password') {
      setShowPassword(!showPassword);
    } else {
      setShowConfirmPassword(!showConfirmPassword);
    }
  };

  return (
    <div className="login-signup-container">
      <ThemeToggle />
      
      {/* Background Animation */}
      <div className="bg-animation">
        <div className="floating-car car-1">
          <i className="fas fa-car"></i>
        </div>
        <div className="floating-car car-2">
          <i className="fas fa-car-side"></i>
        </div>
        <div className="floating-car car-3">
          <i className="fas fa-truck"></i>
        </div>
      </div>

      <div className="auth-wrapper">
        <div className="auth-container">
          {/* Left Panel - Branding */}
          <div className="brand-panel">
            <div className="brand-content">
              <div className="logo">
                <i className="fas fa-car-alt"></i>
                <span>CarRental</span>
              </div>
              <h2>Premium Car Rental Service</h2>
              <p>Experience luxury and comfort with our premium fleet of vehicles. Your journey begins here.</p>
              
              <div className="features">
                <div className="feature">
                  <i className="fas fa-shield-alt"></i>
                  <span>Secure & Safe</span>
                </div>
                <div className="feature">
                  <i className="fas fa-clock"></i>
                  <span>24/7 Support</span>
                </div>
                <div className="feature">
                  <i className="fas fa-star"></i>
                  <span>Premium Quality</span>
                </div>
              </div>
            </div>
          </div>

          {/* Right Panel - Form */}
          <div className="form-panel">
            <div className="form-container">
              <div className="form-header">
                <h3>{isLogin ? 'Welcome Back' : 'Create Account'}</h3>
                <p>{isLogin ? 'Sign in to your account' : 'Join us and start your journey'}</p>
              </div>

              <form onSubmit={handleSubmit} className="auth-form">
                {!isLogin && (
                  <div className="form-group">
                    <div className="input-wrapper">
                      <i className="fas fa-user"></i>
                      <input
                        type="text"
                        name="username"
                        placeholder="Full Name"
                        value={formData.username}
                        onChange={handleInputChange}
                        className={errors.username ? 'error' : ''}
                      />
                    </div>
                    {errors.username && <span className="error-message">{errors.username}</span>}
                  </div>
                )}

                <div className="form-group">
                  <div className="input-wrapper">
                    <i className="fas fa-envelope"></i>
                    <input
                      type="email"
                      name="email"
                      placeholder="Email Address"
                      value={formData.email}
                      onChange={handleInputChange}
                      className={errors.email ? 'error' : ''}
                    />
                  </div>
                  {errors.email && <span className="error-message">{errors.email}</span>}
                </div>

                <div className="form-group">
                  <div className="input-wrapper">
                    <i className="fas fa-lock"></i>
                    <input
                      type={showPassword ? 'text' : 'password'}
                      name="password"
                      placeholder="Password"
                      value={formData.password}
                      onChange={handleInputChange}
                      className={errors.password ? 'error' : ''}
                    />
                    <button
                      type="button"
                      className="password-toggle"
                      onClick={() => togglePasswordVisibility('password')}
                    >
                      <i className={`fas ${showPassword ? 'fa-eye-slash' : 'fa-eye'}`}></i>
                    </button>
                  </div>
                  {errors.password && <span className="error-message">{errors.password}</span>}
                </div>

                {!isLogin && (
                  <div className="form-group">
                    <div className="input-wrapper">
                      <i className="fas fa-lock"></i>
                      <input
                        type={showConfirmPassword ? 'text' : 'password'}
                        name="confirmPassword"
                        placeholder="Confirm Password"
                        value={formData.confirmPassword}
                        onChange={handleInputChange}
                        className={errors.confirmPassword ? 'error' : ''}
                      />
                      <button
                        type="button"
                        className="password-toggle"
                        onClick={() => togglePasswordVisibility('confirm')}
                      >
                        <i className={`fas ${showConfirmPassword ? 'fa-eye-slash' : 'fa-eye'}`}></i>
                      </button>
                    </div>
                    {errors.confirmPassword && <span className="error-message">{errors.confirmPassword}</span>}
                  </div>
                )}

                {errors.submit && (
                  <div className="error-message submit-error">{errors.submit}</div>
                )}

                <button type="submit" className="submit-btn" disabled={loading}>
                  {loading ? (
                    <div className="loading-spinner">
                      <i className="fas fa-spinner fa-spin"></i>
                      <span>{isLogin ? 'Signing In...' : 'Creating Account...'}</span>
                    </div>
                  ) : (
                    <>
                      <span>{isLogin ? 'Sign In' : 'Create Account'}</span>
                      <i className={`fas ${isLogin ? 'fa-sign-in-alt' : 'fa-user-plus'}`}></i>
                    </>
                  )}
                </button>
              </form>

              <div className="form-footer">
                <p>
                  {isLogin ? "Don't have an account?" : "Already have an account?"}
                  <button type="button" className="toggle-btn" onClick={toggleMode}>
                    {isLogin ? 'Sign Up' : 'Sign In'}
                  </button>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginSignup;
