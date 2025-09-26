import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { carService } from '../../services/carService';
import { bookRide } from '../../services/formService';
import { authService } from '../../services/authService';
import { useTheme } from '../../contexts/ThemeContext';
import { safeErrorMessage } from '../../utils/reactSafeError';
import Header from '../common/Header';
import Icon, { CarTypeIcon } from '../common/Icon';
import './CarDetails.css';

const CarDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isDarkMode } = useTheme();
  const user = authService.getCurrentUser();

  const hasRole = (user, role) => {
    return user && user.roles && user.roles.some(r => r === role);
  };

  const isAdminOrManager = () => {
    return hasRole(user, 'ROLE_ADMIN') || hasRole(user, 'ROLE_MANAGER');
  };

  const [car, setCar] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [bookingLoading, setBookingLoading] = useState(false);
  const [showBookingForm, setShowBookingForm] = useState(false);

  const [bookingData, setBookingData] = useState({
    fullName: '',
    email: '',
    phoneNumber: '',
    pickupLocation: '',
    dropoffLocation: '',
    pickupDateTime: '',
    returnDateTime: '',
    notes: ''
  });

  useEffect(() => {
    fetchCarDetails();
  }, [id]);

  const fetchCarDetails = async () => {
    setLoading(true);
    setError('');
    try {
      console.log('Fetching car details for ID:', id);
      const result = await carService.getAvailableCarById(id);
      console.log('Car fetch result:', result);
      
      if (result.success && result.data) {
        console.log('Car loaded successfully:', result.data);
        setCar(result.data);
        setError('');
      } else {
        console.error('Failed to load car:', result);
        const errorMsg = result.status === 404 
          ? 'This car is not available or does not exist.' 
          : safeErrorMessage(result, 'Car not available');
        setError(errorMsg);
      }
    } catch (err) {
      console.error('Error fetching car details:', err);
      setError(safeErrorMessage(err, 'Failed to load car details'));
    } finally {
      setLoading(false);
    }
  };

  const handleBookingInputChange = (e) => {
    const { name, value } = e.target;
    setBookingData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const calculateRentalDays = () => {
    if (bookingData.pickupDateTime && bookingData.returnDateTime) {
      const pickup = new Date(bookingData.pickupDateTime);
      const returnDate = new Date(bookingData.returnDateTime);
      const diffTime = Math.abs(returnDate - pickup);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return diffDays > 0 ? diffDays : 1;
    }
    return 1;
  };

  const calculateTotalAmount = () => {
    if (car) {
      const days = calculateRentalDays();
      return (car.pricePerDay * days).toFixed(2);
    }
    return 0;
  };

  const handleBooking = async (e) => {
    e.preventDefault();
    
    // Prevent double submission
    if (bookingLoading) {
      return;
    }
    
    setBookingLoading(true);
    setError(''); // Clear previous errors

    try {
      // Validate car availability first
      if (!car || !car.id) {
        setError('Car information not available. Please refresh the page.');
        return;
      }
      
      if (!car.available) {
        setError('This car is no longer available for booking.');
        return;
      }
      
      // Helper function to format datetime to backend format
      const formatDateTime = (dateTimeString) => {
        if (!dateTimeString) return null;
        // Convert from HTML datetime-local format to backend format
        // HTML: "2024-01-15T14:30" -> Backend: "2024-01-15T14:30:00"
        return dateTimeString.includes('T') && dateTimeString.length === 16 
          ? dateTimeString + ':00' 
          : dateTimeString;
      };

      // Validate required fields
      if (!bookingData.fullName?.trim()) {
        setError('Full name is required');
        return;
      }
      if (!bookingData.pickupLocation?.trim()) {
        setError('Pickup location is required');
        return;
      }
      if (!bookingData.pickupDateTime) {
        setError('Pickup date and time is required');
        return;
      }
      if (!bookingData.returnDateTime) {
        setError('Return date and time is required');
        return;
      }

      // Create booking record using formService
      const totalAmount = calculateTotalAmount();
      const rentalDays = calculateRentalDays();
      
      const bookingPayload = {
        fullName: bookingData.fullName?.trim(),
        email: bookingData.email?.trim(),
        phoneNumber: bookingData.phoneNumber?.trim(),
        pickupLocation: bookingData.pickupLocation?.trim(),
        dropoffLocation: bookingData.dropoffLocation?.trim() || bookingData.pickupLocation?.trim(),
        pickupDateTime: formatDateTime(bookingData.pickupDateTime),
        returnDateTime: formatDateTime(bookingData.returnDateTime),
        carType: car.carType,
        notes: bookingData.notes?.trim() || '',
        totalAmount: parseFloat(totalAmount),
        rentalDays: Math.max(1, rentalDays), // Ensure at least 1 day
        car: { id: car.id }
      };
      
      console.log('=== BOOKING SUBMISSION ===' );
      console.log('Car ID:', car.id);
      console.log('Car available:', car.available);
      console.log('Total amount:', totalAmount);
      console.log('Rental days:', rentalDays);
      console.log('Full payload:', bookingPayload);

      console.log('Submitting booking:', bookingPayload);
      console.log('Booking loading state before submission:', bookingLoading);
      
      const result = await bookRide(bookingPayload);
      
      console.log('Booking result:', result);
      console.log('Result success:', result.success);
      console.log('Result data:', result.data);

      if (result.success && result.data) {
        console.log('Booking successful, navigating to confirmation');
        console.log('Booking ID:', result.data.id);
        console.log('Navigation state:', {
          bookingData: {
            ...bookingPayload,
            id: result.data?.id,
            totalAmount: totalAmount,
            rentalDays: rentalDays
          },
          car: car
        });
        
        // Navigate to booking confirmation page with booking details
        navigate('/booking-confirmation', {
          state: {
            bookingData: {
              ...bookingPayload,
              id: result.data?.id,
              totalAmount: totalAmount,
              rentalDays: rentalDays
            },
            car: car
          }
        });
        return; // Early return to prevent further execution
      } else {
        console.error('Booking failed:', result);
        let errorMessage = 'Failed to create booking';
        
        if (result.message) {
          if (result.message.includes('Car not found')) {
            errorMessage = 'The selected car is not available. Please choose another car.';
          } else if (result.message.includes('not available')) {
            errorMessage = 'This car is no longer available for booking.';
          } else {
            errorMessage = result.message;
          }
        }
        
        setError(errorMessage);
      }
    } catch (err) {
      console.error('Booking error:', err);
      setError(safeErrorMessage(err, 'Booking failed. Please try again.'));
    } finally {
      setBookingLoading(false);
    }
  };

  const isBookingFormValid = () => {
    return bookingData.fullName.trim() && 
           bookingData.email.trim() && 
           bookingData.phoneNumber.trim() &&
           bookingData.pickupLocation.trim() && 
           bookingData.pickupDateTime && 
           bookingData.returnDateTime;
  };

  if (loading) {
    return (
      <div className="car-details-container">
        <Header title="Car Details" />
        <div className="loading-state">
          <div className="loading-spinner"></div>
          <p className="text-lg text-secondary">Loading car details...</p>
        </div>
      </div>
    );
  }

  if (error || !car) {
    return (
      <div className="car-details-container">
        <Header title="Car Details" />
        <div className="error-state">
          <Icon name="warning" size="xl" className="text-error" />
          <h2 className="text-2xl font-semibold text-error mt-lg">Car Not Found</h2>
          <p className="text-lg text-secondary mt-md mb-xl">
            {(typeof error === 'string' ? error : null) || 'The requested car is not available or does not exist.'}
          </p>
          <button onClick={() => navigate('/cars')} className="btn btn-primary">
            <Icon name="chevronLeft" size="sm" />
            Back to Cars
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="car-details-container">
      <Header />
      
      <main className="container">
        <div className="breadcrumb mb-lg">
          <button onClick={() => navigate('/cars')} className="btn btn-secondary btn-sm">
            <Icon name="chevronLeft" size="sm" />
            Back to Cars
          </button>
        </div>

        <div className="car-details-content">
          <div className="car-image-section">
            <div className="image-container">
              <img 
                src={car.imageUrl || 'https://via.placeholder.com/600x400?text=Car+Image'} 
                alt={`${car.make} ${car.model}`}
                className="car-main-image"
                onError={(e) => {
                  e.target.src = 'https://via.placeholder.com/600x400?text=Car+Image';
                }}
              />
              <div className={`car-status-badge ${
                car.available ? 'status-available' : 'status-unavailable'
              }`}>
                <Icon name={car.available ? 'check' : 'close'} size="sm" />
                {car.available ? 'Available' : 'Booked'}
              </div>
            </div>
          </div>

          <div className="car-info-section">
            <div className="card car-info-card">
              <div className="car-header">
                <div className="car-title-section">
                  <h1 className="text-3xl font-bold text-primary mb-xs">{car.make} {car.model}</h1>
                  <div className="flex items-center gap-md mb-md">
                    <span className="text-lg text-secondary">{car.year}</span>
                    <CarTypeIcon type={car.carType} size="sm" />
                  </div>
                </div>
                <div className="car-price-display">
                  <div className="price-main">
                    <span className="price text-4xl font-bold text-primary">₹{car.pricePerDay}</span>
                    <span className="per-day text-sm text-muted">per day</span>
                  </div>
                </div>
              </div>

              <div className="car-description">
                <p className="text-base text-secondary leading-relaxed">{car.description || 'A reliable and comfortable vehicle perfect for your travel needs.'}</p>
              </div>

              <div className="car-specifications">
                <h3 className="text-xl font-semibold text-primary mb-lg">Specifications</h3>
                <div className="specs-grid">
                  <div className="spec-item">
                    <div className="spec-icon">
                      <CarTypeIcon type={car.carType} size="sm" showLabel={false} />
                    </div>
                    <div className="spec-content">
                      <span className="spec-label">Type</span>
                      <span className="spec-value">{car.carType}</span>
                    </div>
                  </div>
                  <div className="spec-item">
                    <div className="spec-icon">
                      <Icon name="settings" size="sm" />
                    </div>
                    <div className="spec-content">
                      <span className="spec-label">Transmission</span>
                      <span className="spec-value">{car.transmission}</span>
                    </div>
                  </div>
                  <div className="spec-item">
                    <div className="spec-icon">
                      <Icon name="location" size="sm" />
                    </div>
                    <div className="spec-content">
                      <span className="spec-label">Fuel Type</span>
                      <span className="spec-value">{car.fuelType}</span>
                    </div>
                  </div>
                  <div className="spec-item">
                    <div className="spec-icon">
                      <Icon name="user" size="sm" />
                    </div>
                    <div className="spec-content">
                      <span className="spec-label">Seating</span>
                      <span className="spec-value">{car.seatingCapacity} seats</span>
                    </div>
                  </div>
                  <div className="spec-item">
                    <div className="spec-icon">
                      <Icon name="dashboard" size="sm" />
                    </div>
                    <div className="spec-content">
                      <span className="spec-label">Mileage</span>
                      <span className="spec-value">{car.mileage} km/l</span>
                    </div>
                  </div>
                  <div className="spec-item">
                    <div className="spec-icon">
                      <Icon name="star" size="sm" />
                    </div>
                    <div className="spec-content">
                      <span className="spec-label">Color</span>
                      <span className="spec-value">{car.color}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div className="car-features">
                <h3 className="text-xl font-semibold text-primary mb-lg">Features & Amenities</h3>
                <div className="features-grid">
                  {car.airConditioning && (
                    <div className="feature-item">
                      <Icon name="star" size="sm" className="text-accent" />
                      <span>Air Conditioning</span>
                    </div>
                  )}
                  {car.gpsNavigation && (
                    <div className="feature-item">
                      <Icon name="location" size="sm" className="text-accent" />
                      <span>GPS Navigation</span>
                    </div>
                  )}
                  {car.bluetoothConnectivity && (
                    <div className="feature-item">
                      <Icon name="star" size="sm" className="text-accent" />
                      <span>Bluetooth</span>
                    </div>
                  )}
                  <div className="feature-item">
                    <Icon name="check" size="sm" className="text-success" />
                    <span>Insurance Included</span>
                  </div>
                  <div className="feature-item">
                    <Icon name="user" size="sm" className="text-success" />
                    <span>24/7 Support</span>
                  </div>
                </div>
              </div>

              {isAdminOrManager() ? (
                <div className="admin-notice-section">
                  <div className="admin-notice card">
                    <div className="flex items-center gap-md mb-lg">
                      <Icon name="info" size="lg" className="text-primary" />
                      <div>
                        <h4 className="text-lg font-semibold text-primary">Administrator Account</h4>
                        <p className="text-sm text-secondary mb-0">Car booking is restricted for administrative accounts.</p>
                      </div>
                    </div>
                    <div className="admin-actions flex gap-md">
                      <button 
                        onClick={() => navigate('/admin')}
                        className="btn btn-primary flex-1"
                      >
                        <Icon name="settings" size="sm" />
                        Admin Panel
                      </button>
                      <button 
                        onClick={() => navigate('/cars')}
                        className="btn btn-secondary flex-1"
                      >
                        <Icon name="chevronLeft" size="sm" />
                        Back to Cars
                      </button>
                    </div>
                  </div>
                </div>
              ) : !showBookingForm ? (
                <div className="booking-section">
                  <button 
                    onClick={() => {
                      console.log('Book Now clicked for car:', car);
                      if (car && car.available) {
                        setShowBookingForm(true);
                      }
                    }}
                    className={`btn btn-lg w-full ${
                      car?.available ? 'btn-accent' : 'btn-secondary'
                    }`}
                    disabled={!car || !car.available}
                    title={!car ? 'Car not loaded' : !car.available ? 'Car not available' : 'Click to book this car'}
                  >
                    <Icon name={car?.available ? 'calendar' : 'close'} size="sm" />
                    {!car ? 'Loading...' : car.available ? 'Book This Car' : 'Not Available'}
                  </button>
                </div>
              ) : (
                <div className="booking-form-section card">
                  <div className="flex items-center justify-between mb-lg">
                    <h3 className="text-2xl font-semibold text-primary">Booking Details</h3>
                    <button 
                      type="button" 
                      onClick={() => setShowBookingForm(false)}
                      className="btn btn-secondary btn-sm"
                    >
                      <Icon name="close" size="sm" />
                      Cancel
                    </button>
                  </div>
                  
                  {error && (
                    <div className="error-message mb-lg flex items-center gap-sm p-md rounded-lg bg-error text-white">
                      <Icon name="warning" size="sm" />
                      <span>{error}</span>
                    </div>
                  )}
                  
                  <form onSubmit={handleBooking} className="booking-form">
                    <div className="grid grid-cols-2 gap-md mb-md">
                      <div className="form-group">
                        <label htmlFor="fullName" className="form-label">
                          <Icon name="user" size="sm" />
                          Full Name *
                        </label>
                        <input
                          type="text"
                          id="fullName"
                          name="fullName"
                          value={bookingData.fullName}
                          onChange={handleBookingInputChange}
                          className="form-input"
                          required
                        />
                      </div>
                      <div className="form-group">
                        <label htmlFor="email" className="form-label">
                          <Icon name="user" size="sm" />
                          Email *
                        </label>
                        <input
                          type="email"
                          id="email"
                          name="email"
                          value={bookingData.email}
                          onChange={handleBookingInputChange}
                          className="form-input"
                          required
                        />
                      </div>
                    </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="phoneNumber">Phone Number *</label>
                    <input
                      type="tel"
                      id="phoneNumber"
                      name="phoneNumber"
                      value={bookingData.phoneNumber}
                      onChange={handleBookingInputChange}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="pickupLocation">Pickup Location *</label>
                    <input
                      type="text"
                      id="pickupLocation"
                      name="pickupLocation"
                      value={bookingData.pickupLocation}
                      onChange={handleBookingInputChange}
                      required
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="dropoffLocation">Drop-off Location</label>
                    <input
                      type="text"
                      id="dropoffLocation"
                      name="dropoffLocation"
                      value={bookingData.dropoffLocation}
                      onChange={handleBookingInputChange}
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="pickupDateTime">Pickup Date & Time *</label>
                    <input
                      type="datetime-local"
                      id="pickupDateTime"
                      name="pickupDateTime"
                      value={bookingData.pickupDateTime}
                      onChange={handleBookingInputChange}
                      min={new Date().toISOString().slice(0, 16)}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="returnDateTime">Return Date & Time *</label>
                    <input
                      type="datetime-local"
                      id="returnDateTime"
                      name="returnDateTime"
                      value={bookingData.returnDateTime}
                      onChange={handleBookingInputChange}
                      min={bookingData.pickupDateTime}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="notes">Additional Notes</label>
                  <textarea
                    id="notes"
                    name="notes"
                    value={bookingData.notes}
                    onChange={handleBookingInputChange}
                    rows={3}
                    placeholder="Any special requirements or notes..."
                  />
                </div>

                <div className="booking-summary">
                  <div className="summary-item">
                    <span>Rental Days:</span>
                    <span>{calculateRentalDays()} day(s)</span>
                  </div>
                  <div className="summary-item">
                    <span>Rate per day:</span>
                    <span>₹{car.pricePerDay}</span>
                  </div>
                  <div className="summary-item total">
                    <span>Total Amount:</span>
                    <span>₹{calculateTotalAmount()}</span>
                  </div>
                </div>

                <div className="form-actions">
                  <button 
                    type="button" 
                    onClick={() => setShowBookingForm(false)}
                    className="cancel-btn"
                  >
                    Cancel
                  </button>
                      <button 
                        type="submit" 
                        className="btn btn-accent btn-lg flex-1"
                        disabled={!isBookingFormValid() || bookingLoading}
                      >
                        {bookingLoading ? (
                          <>
                            <Icon name="refresh" size="sm" className="animate-spin" />
                            Booking...
                          </>
                        ) : (
                          <>
                            <Icon name="check" size="sm" />
                            Confirm Booking
                          </>
                        )}
                      </button>
                    </div>
                  </form>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default CarDetails;