import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import './LoginSignup.css';

const LoginSignup = () => {
  const location = useLocation();
  const [isSignUp, setIsSignUp] = useState(() => {
    // Initialize from navigation state if available
    return location.state?.mode === 'signup';
  });
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Update mode if navigation state changes (e.g., deep link to sign-up)
    if (location.state?.mode === 'signup') {
      setIsSignUp(true);
    } else if (location.state?.mode === 'signin') {
      setIsSignUp(false);
    }
  }, [location.state]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (isSignUp && !formData.username.trim()) newErrors.username = 'Username is required';
    if (!formData.email.trim()) newErrors.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(formData.email)) newErrors.email = 'Email is invalid';
    if (!formData.password) newErrors.password = 'Password is required';
    else if (formData.password.length < 6) newErrors.password = 'Password must be at least 6 characters';
    if (isSignUp && formData.password !== formData.confirmPassword) newErrors.confirmPassword = 'Passwords do not match';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    setLoading(true);
    try {
      const result = isSignUp
        ? await authService.signup(formData.username, formData.email, formData.password)
        : await authService.login(formData.email, formData.password);

      if (result.success) {
        if (isSignUp) {
          setIsSignUp(false);
          alert('Account created successfully! Please login.');
        } else {
          authService.saveUser(result.data);
          navigate('/dashboard');
        }
      } else {
        setErrors({ submit: result.message });
      }
    } catch (error) {
      setErrors({ submit: 'An unexpected error occurred.' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`login-signup-container fade-in ${isSignUp ? 'sign-up-mode' : ''}`}>
      <div className="forms-container">
        <div className="signin-signup">
          <form onSubmit={handleSubmit} className="sign-in-form">
            <h2 className="title">Sign in</h2>
            <div className="input-field">
              <i className="fas fa-user"></i>
              <input type="email" name="email" placeholder="Email" onChange={handleInputChange} required />
            </div>
            <div className="input-field">
              <i className="fas fa-lock"></i>
              <input type="password" name="password" placeholder="Password" onChange={handleInputChange} required />
            </div>
            {errors.submit && <span className="error-message">{errors.submit}</span>}
            <input type="submit" value={loading ? 'Signing in...' : 'Login'} className="btn solid" disabled={loading} />
          </form>

          <form onSubmit={handleSubmit} className="sign-up-form">
            <h2 className="title">Sign up</h2>
            <div className="input-field">
              <i className="fas fa-user"></i>
              <input type="text" name="username" placeholder="Username" onChange={handleInputChange} required />
            </div>
            <div className="input-field">
              <i className="fas fa-envelope"></i>
              <input type="email" name="email" placeholder="Email" onChange={handleInputChange} required />
            </div>
            <div className="input-field">
              <i className="fas fa-lock"></i>
              <input type="password" name="password" placeholder="Password" onChange={handleInputChange} required />
            </div>
            <div className="input-field">
              <i className="fas fa-lock"></i>
              <input type="password" name="confirmPassword" placeholder="Confirm Password" onChange={handleInputChange} required />
            </div>
            {errors.submit && <span className="error-message">{errors.submit}</span>}
            <input type="submit" value={loading ? 'Signing up...' : 'Sign up'} className="btn solid" disabled={loading} />
          </form>
        </div>
      </div>

      <div className="panels-container">
        <div className="panel left-panel">
          <div className="content">
            <h3>New here ?</h3>
            <p>
              Ready to start your journey? Join us today and discover a world of premium car rentals.
            </p>
            <button className="btn transparent" id="sign-up-btn" onClick={() => setIsSignUp(true)}>
              Sign up
            </button>
          </div>
          <img src="img/log.svg" className="image" alt="" />
        </div>
        <div className="panel right-panel">
          <div className="content">
            <h3>One of us ?</h3>
            <p>
              Welcome back! Sign in to access your account and continue your adventure.
            </p>
            <button className="btn transparent" id="sign-in-btn" onClick={() => setIsSignUp(false)}>
              Sign in
            </button>
          </div>
          <img src="img/register.svg" className="image" alt="" />
        </div>
      </div>
    </div>
  );
};

export default LoginSignup;
