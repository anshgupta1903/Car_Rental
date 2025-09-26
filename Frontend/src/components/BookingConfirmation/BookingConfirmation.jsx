import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useTheme } from '../../contexts/ThemeContext';
import { safeErrorMessage } from '../../utils/reactSafeError';
import './BookingConfirmation.css';

const BookingConfirmation = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { isDarkMode } = useTheme();
  
  const [bookingId, setBookingId] = useState(null);
  const [error, setError] = useState('');
  
  const { bookingData, car } = location.state || {};

  useEffect(() => {
    // If no booking data, redirect to cars page
    if (!bookingData || !car) {
      navigate('/cars');
      return;
    }
    
    // If booking data has an ID, it was already submitted successfully
    if (bookingData.id) {
      setBookingId(bookingData.id);
      setError('');
    } else {
      // If no ID, something went wrong with the submission
      setError('Booking submission failed. Please try again.');
    }
  }, [bookingData, car, navigate]);


  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const handlePrintBooking = () => {
    window.print();
  };

  const handleNewBooking = () => {
    navigate('/cars');
  };

  if (!bookingData || !car) {
    return (
      <div className="booking-confirmation-container">
        <div className="error-section">
          <h2>No Booking Data Found</h2>
          <p>Please start a new booking from the cars page.</p>
          <Link to="/cars" className="back-to-cars-btn">
            Browse Cars
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="booking-confirmation-container">
      <div className="confirmation-content">
        <div className="success-header">
          <div className="success-icon">✓</div>
          <h1>Booking Confirmed!</h1>
          <p>Your car rental has been successfully booked.</p>
          {bookingId && (
            <div className="booking-reference">
              <strong>Booking ID: #{bookingId}</strong>
            </div>
          )}
        </div>

        {error && (
          <div className="error-message">
            <p>{typeof error === 'string' ? error : 'An error occurred'}</p>
          </div>
        )}

        <div className="booking-details-section">
          <div className="car-summary">
            <div className="car-image">
              <img 
                src={car.imageUrl || 'https://via.placeholder.com/300x200?text=Car+Image'} 
                alt={`${car.make} ${car.model}`}
                onError={(e) => {
                  e.target.src = 'https://via.placeholder.com/300x200?text=Car+Image';
                }}
              />
            </div>
            <div className="car-info">
              <h2>{car.make} {car.model}</h2>
              <p className="car-details">{car.year} • {car.carType} • {car.transmission}</p>
              <div className="price-info">
                <span className="rate">₹{car.pricePerDay}/day</span>
              </div>
            </div>
          </div>

          <div className="booking-info">
            <h3>Booking Details</h3>
            <div className="info-grid">
              <div className="info-item">
                <span className="label">Customer Name:</span>
                <span className="value">{bookingData.fullName}</span>
              </div>
              <div className="info-item">
                <span className="label">Email:</span>
                <span className="value">{bookingData.email}</span>
              </div>
              <div className="info-item">
                <span className="label">Phone:</span>
                <span className="value">{bookingData.phoneNumber}</span>
              </div>
              <div className="info-item">
                <span className="label">Pickup Location:</span>
                <span className="value">{bookingData.pickupLocation}</span>
              </div>
              {bookingData.dropoffLocation && (
                <div className="info-item">
                  <span className="label">Drop-off Location:</span>
                  <span className="value">{bookingData.dropoffLocation}</span>
                </div>
              )}
              <div className="info-item">
                <span className="label">Pickup Date & Time:</span>
                <span className="value">{formatDate(bookingData.pickupDateTime)}</span>
              </div>
              <div className="info-item">
                <span className="label">Return Date & Time:</span>
                <span className="value">{formatDate(bookingData.returnDateTime)}</span>
              </div>
              <div className="info-item">
                <span className="label">Rental Duration:</span>
                <span className="value">{bookingData.rentalDays} day{bookingData.rentalDays > 1 ? 's' : ''}</span>
              </div>
              {bookingData.notes && (
                <div className="info-item full-width">
                  <span className="label">Special Notes:</span>
                  <span className="value">{bookingData.notes}</span>
                </div>
              )}
            </div>
          </div>

          <div className="payment-summary">
            <h3>Payment Summary</h3>
            <div className="payment-breakdown">
              <div className="payment-item">
                <span>Base Rate (₹{car.pricePerDay} × {bookingData.rentalDays} day{bookingData.rentalDays > 1 ? 's' : ''})</span>
                <span>₹{(car.pricePerDay * bookingData.rentalDays).toFixed(2)}</span>
              </div>
              <div className="payment-item">
                <span>Taxes & Fees</span>
                <span>Included</span>
              </div>
              <div className="payment-item total">
                <span>Total Amount</span>
                <span>₹{bookingData.totalAmount}</span>
              </div>
            </div>
            <div className="payment-status">
              <span className="status-badge">Payment Pending</span>
              <p className="payment-note">
                Please pay at the time of pickup. Accepted payment methods: Cash, Card, UPI.
              </p>
            </div>
          </div>

          <div className="important-notes">
            <h3>Important Information</h3>
            <ul>
              <li>Please arrive 15 minutes before your pickup time</li>
              <li>Bring a valid driving license and government ID</li>
              <li>Vehicle will be provided with full fuel tank</li>
              <li>Return the vehicle with the same fuel level</li>
              <li>Late return charges: ₹{Math.round(car.pricePerDay * 0.1)}/hour</li>
              <li>Security deposit may be required at pickup</li>
            </ul>
          </div>

          <div className="contact-info">
            <h3>Contact Information</h3>
            <div className="contact-details">
              <div className="contact-item">
                <strong>Customer Support:</strong>
                <span>+91 9876543210</span>
              </div>
              <div className="contact-item">
                <strong>Email:</strong>
                <span>support@carrental.com</span>
              </div>
              <div className="contact-item">
                <strong>Emergency:</strong>
                <span>+91 9876543211 (24/7)</span>
              </div>
            </div>
          </div>
        </div>

        <div className="action-buttons">
          <button onClick={handlePrintBooking} className="print-btn">
            🖨️ Print Booking
          </button>
          <button onClick={handleNewBooking} className="new-booking-btn">
            Book Another Car
          </button>
          <Link to="/dashboard" className="dashboard-btn">
            Go to Dashboard
          </Link>
        </div>

        <div className="confirmation-footer">
          <p>
            A confirmation email has been sent to <strong>{bookingData.email}</strong>
          </p>
          <p className="thank-you">
            Thank you for choosing our car rental service! 🚗
          </p>
        </div>
      </div>
    </div>
  );
};

export default BookingConfirmation;