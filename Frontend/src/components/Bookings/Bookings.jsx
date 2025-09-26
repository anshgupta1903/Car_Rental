import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useTheme } from '../../contexts/ThemeContext';
import { authService } from '../../services/authService';
import { apiService } from '../../services/apiService';
import { safeErrorMessage } from '../../utils/reactSafeError';
import './Bookings.css';

const Bookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { isDarkMode } = useTheme();
  const user = authService.getCurrentUser();

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    if (!user || !user.email) {
      setError('Please log in to view bookings');
      setLoading(false);
      return;
    }

    console.log('Fetching bookings for user:', user.email);
    
    try {
      const result = await apiService.get(`/forms/user/${user.email}`);
      
      if (result.success) {
        console.log('Bookings fetched successfully:', result.data);
        setBookings(result.data);
        setError('');
      } else {
        console.error('Failed to fetch bookings:', result.message);
        setError(safeErrorMessage(result, 'Failed to fetch bookings'));
      }
    } catch (err) {
      console.error('Error fetching bookings:', err);
      setError(safeErrorMessage(err, 'Failed to fetch bookings'));
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Not specified';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="bookings-container">
        <div className="loading">Loading your bookings...</div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="bookings-container">
        <div className="error">
          <h2>Please Log In</h2>
          <p>You need to be logged in to view your bookings.</p>
          <Link to="/login" className="login-btn">Go to Login</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="bookings-container">
      <div className="bookings-header">
        <h1>My Bookings</h1>
        <p>Here are all your car rental bookings</p>
      </div>

      {error && (
        <div className="error-message">
          <p>{typeof error === 'string' ? error : 'An error occurred while loading bookings'}</p>
        </div>
      )}

      {bookings.length === 0 ? (
        <div className="no-bookings">
          <h2>No Bookings Found</h2>
          <p>You haven't made any bookings yet.</p>
          <Link to="/cars" className="browse-cars-btn">Browse Cars</Link>
        </div>
      ) : (
        <div className="bookings-list">
          {bookings.map((booking) => (
            <div key={booking.id} className="booking-card">
              <div className="booking-header">
                <h3>Booking #{booking.id}</h3>
                <span className={`status ${booking.bookingStatus?.toLowerCase() || 'confirmed'}`}>
                  {booking.bookingStatus || 'Confirmed'}
                </span>
              </div>

              <div className="booking-details">
                {/* Car Information Section */}
                {booking.car ? (
                  <div className="car-info-section">
                    <h4 className="section-title">Vehicle Details</h4>
                    {booking.car.imageUrl && (
                      <div className="car-image">
                        <img src={booking.car.imageUrl} alt={`${booking.car.make} ${booking.car.model}`} />
                      </div>
                    )}
                    <div className="car-details-grid">
                      <div className="detail-row">
                        <span className="label">Vehicle:</span>
                        <span className="value">{booking.car.make} {booking.car.model} ({booking.car.year})</span>
                      </div>
                      <div className="detail-row">
                        <span className="label">Car Type:</span>
                        <span className="value">{booking.car.carType}</span>
                      </div>
                      <div className="detail-row">
                        <span className="label">Color:</span>
                        <span className="value">{booking.car.color}</span>
                      </div>
                      <div className="detail-row">
                        <span className="label">License Plate:</span>
                        <span className="value">{booking.car.licensePlate}</span>
                      </div>
                      <div className="detail-row">
                        <span className="label">Transmission:</span>
                        <span className="value">{booking.car.transmission}</span>
                      </div>
                      <div className="detail-row">
                        <span className="label">Fuel Type:</span>
                        <span className="value">{booking.car.fuelType}</span>
                      </div>
                      <div className="detail-row">
                        <span className="label">Seating Capacity:</span>
                        <span className="value">{booking.car.seatingCapacity} persons</span>
                      </div>
                      <div className="detail-row">
                        <span className="label">Rate:</span>
                        <span className="value">₹{booking.car.pricePerDay}/day</span>
                      </div>
                      <div className="features">
                        <span className="label">Features:</span>
                        <div className="feature-tags">
                          {booking.car.airConditioning && <span className="feature-tag">AC</span>}
                          {booking.car.gpsNavigation && <span className="feature-tag">GPS</span>}
                          {booking.car.bluetoothConnectivity && <span className="feature-tag">Bluetooth</span>}
                          <span className="feature-tag">{booking.car.mileage} km/l</span>
                        </div>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="detail-row">
                    <span className="label">Car Type:</span>
                    <span className="value">{booking.carType}</span>
                  </div>
                )}
                
                {/* Booking Information Section */}
                <div className="booking-info-section">
                  <h4 className="section-title">Booking Information</h4>
                  <div className="booking-details-grid">
                    <div className="detail-row">
                      <span className="label">Customer Name:</span>
                      <span className="value">{booking.fullName}</span>
                    </div>

                <div className="detail-row">
                  <span className="label">Email:</span>
                  <span className="value">{booking.email}</span>
                </div>

                {booking.phoneNumber && (
                  <div className="detail-row">
                    <span className="label">Phone:</span>
                    <span className="value">{booking.phoneNumber}</span>
                  </div>
                )}

                <div className="detail-row">
                  <span className="label">Pickup Location:</span>
                  <span className="value">{booking.pickupLocation}</span>
                </div>

                {booking.dropoffLocation && (
                  <div className="detail-row">
                    <span className="label">Drop-off Location:</span>
                    <span className="value">{booking.dropoffLocation}</span>
                  </div>
                )}

                <div className="detail-row">
                  <span className="label">Pickup Date:</span>
                  <span className="value">{formatDate(booking.pickupDateTime)}</span>
                </div>

                <div className="detail-row">
                  <span className="label">Return Date:</span>
                  <span className="value">{formatDate(booking.returnDateTime)}</span>
                </div>

                {booking.rentalDays && (
                  <div className="detail-row">
                    <span className="label">Duration:</span>
                    <span className="value">{booking.rentalDays} day(s)</span>
                  </div>
                )}

                {booking.totalAmount && (
                  <div className="detail-row">
                    <span className="label">Total Amount:</span>
                    <span className="value amount">₹{booking.totalAmount}</span>
                  </div>
                )}

                {booking.notes && (
                  <div className="detail-row">
                    <span className="label">Notes:</span>
                    <span className="value">{booking.notes}</span>
                  </div>
                )}

                    <div className="detail-row">
                      <span className="label">Booked On:</span>
                      <span className="value">{formatDate(booking.createdAt)}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <div className="bookings-actions">
        <Link to="/cars" className="browse-more-btn">Book Another Car</Link>
        <Link to="/dashboard" className="back-btn">Back to Dashboard</Link>
      </div>
    </div>
  );
};

export default Bookings;