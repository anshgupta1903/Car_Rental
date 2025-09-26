import React, { useState, useEffect } from 'react';
import { adminService } from '../../services/adminService';
import { authService } from '../../services/authService';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const [user, setUser] = useState(null);
  const [cars, setCars] = useState([]);
  const [newCar, setNewCar] = useState({
    make: '',
    model: '',
    year: '',
    carType: '',
    transmission: 'Automatic',
    fuelType: 'Petrol',
    seatingCapacity: 5,
    pricePerDay: '',
    color: '',
    licensePlate: '',
    mileage: '',
    imageUrl: '',
    description: '',
    available: true,
    airConditioning: true,
    gpsNavigation: false,
    bluetoothConnectivity: false
  });
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('cars');
  const [isAddingCar, setIsAddingCar] = useState(false);

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
    
    if (currentUser && (hasRole(currentUser, 'ROLE_ADMIN') || hasRole(currentUser, 'ROLE_MANAGER'))) {
      loadCars();
    } else {
      setLoading(false);
    }
  }, []);

  const hasRole = (user, role) => {
    return user && user.roles && user.roles.some(r => r === role);
  };

  const loadCars = async () => {
    setLoading(true);
    try {
      const response = await adminService.getAllCarsForAdmin();
      if (response.success) {
        setCars(response.data);
      }
    } catch (error) {
      console.error('Error loading cars:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setNewCar(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleAddCar = async (e) => {
    e.preventDefault();
    try {
      const response = await adminService.addCar(newCar);
      if (response.success) {
        setCars(prev => [...prev, response.data]);
        setNewCar({
          make: '',
          model: '',
          year: '',
          carType: '',
          transmission: 'Automatic',
          fuelType: 'Petrol',
          seatingCapacity: 5,
          pricePerDay: '',
          color: '',
          licensePlate: '',
          mileage: '',
          imageUrl: '',
          description: '',
          available: true,
          airConditioning: true,
          gpsNavigation: false,
          bluetoothConnectivity: false
        });
        setIsAddingCar(false);
        alert('Car added successfully!');
      }
    } catch (error) {
      console.error('Error adding car:', error);
      alert('Error adding car. Please try again.');
    }
  };

  const handleDeleteCar = async (carId) => {
    if (window.confirm('Are you sure you want to delete this car?')) {
      try {
        const response = await adminService.deleteCar(carId);
        if (response.success) {
          setCars(prev => prev.filter(car => car.id !== carId));
          alert('Car deleted successfully!');
        }
      } catch (error) {
        console.error('Error deleting car:', error);
        alert('Error deleting car. Please try again.');
      }
    }
  };

  if (loading) {
    return (
      <div className="admin-loading">
        <div className="loading-spinner"></div>
        <p>Loading admin dashboard...</p>
      </div>
    );
  }

  if (!user || (!hasRole(user, 'ROLE_ADMIN') && !hasRole(user, 'ROLE_MANAGER'))) {
    return (
      <div className="admin-error">
        <h2>Access Denied</h2>
        <p>You don't have permission to access the admin dashboard.</p>
      </div>
    );
  }

  return (
    <div className="admin-dashboard">
      <header className="admin-header">
        <h1>Car Management System</h1>
        <div className="admin-user-info">
          <span>Welcome, {user.username}</span>
          <span className="user-role">
            {hasRole(user, 'ROLE_ADMIN') ? 'Admin' : 'Manager'}
          </span>
        </div>
      </header>

      <nav className="admin-nav">
        <button 
          className={activeTab === 'cars' ? 'nav-btn active' : 'nav-btn'}
          onClick={() => setActiveTab('cars')}
        >
          <i className="fas fa-car"></i> Manage Cars
        </button>
        <button 
          className="nav-btn"
          onClick={() => window.location.href = '/dashboard'}
        >
          <i className="fas fa-home"></i> Back to Dashboard
        </button>
      </nav>

      <main className="admin-content">
        <div className="cars-section">
          <div className="section-header">
            <h2>Car Management</h2>
            <button 
              className="btn primary"
              onClick={() => setIsAddingCar(!isAddingCar)}
            >
              <i className="fas fa-plus"></i> 
              {isAddingCar ? 'Cancel' : 'Add New Car'}
            </button>
          </div>

          {isAddingCar && (
            <div className="add-car-form">
              <h3>Add New Car</h3>
              <form onSubmit={handleAddCar}>
                <div className="form-grid">
                  <div className="form-group">
                    <label htmlFor="make">Make/Brand</label>
                    <input
                      type="text"
                      id="make"
                      name="make"
                      value={newCar.make}
                      onChange={handleInputChange}
                      placeholder="e.g., Toyota"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="model">Model</label>
                    <input
                      type="text"
                      id="model"
                      name="model"
                      value={newCar.model}
                      onChange={handleInputChange}
                      placeholder="e.g., Camry"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="year">Year</label>
                    <input
                      type="number"
                      id="year"
                      name="year"
                      value={newCar.year}
                      onChange={handleInputChange}
                      placeholder="e.g., 2023"
                      min="1900"
                      max="2050"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="carType">Car Type</label>
                    <select
                      id="carType"
                      name="carType"
                      value={newCar.carType}
                      onChange={handleInputChange}
                      required
                    >
                      <option value="">Select Type</option>
                      <option value="SUV">SUV</option>
                      <option value="Sedan">Sedan</option>
                      <option value="Hatchback">Hatchback</option>
                      <option value="Coupe">Coupe</option>
                      <option value="Convertible">Convertible</option>
                      <option value="Wagon">Wagon</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label htmlFor="transmission">Transmission</label>
                    <select
                      id="transmission"
                      name="transmission"
                      value={newCar.transmission}
                      onChange={handleInputChange}
                      required
                    >
                      <option value="Automatic">Automatic</option>
                      <option value="Manual">Manual</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label htmlFor="fuelType">Fuel Type</label>
                    <select
                      id="fuelType"
                      name="fuelType"
                      value={newCar.fuelType}
                      onChange={handleInputChange}
                      required
                    >
                      <option value="Petrol">Petrol</option>
                      <option value="Diesel">Diesel</option>
                      <option value="Electric">Electric</option>
                      <option value="Hybrid">Hybrid</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label htmlFor="seatingCapacity">Seating Capacity</label>
                    <input
                      type="number"
                      id="seatingCapacity"
                      name="seatingCapacity"
                      value={newCar.seatingCapacity}
                      onChange={handleInputChange}
                      placeholder="e.g., 5"
                      min="1"
                      max="15"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="pricePerDay">Price Per Day ($)</label>
                    <input
                      type="number"
                      id="pricePerDay"
                      name="pricePerDay"
                      value={newCar.pricePerDay}
                      onChange={handleInputChange}
                      placeholder="e.g., 50.00"
                      min="0.01"
                      step="0.01"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="color">Color</label>
                    <input
                      type="text"
                      id="color"
                      name="color"
                      value={newCar.color}
                      onChange={handleInputChange}
                      placeholder="e.g., White"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="licensePlate">License Plate</label>
                    <input
                      type="text"
                      id="licensePlate"
                      name="licensePlate"
                      value={newCar.licensePlate}
                      onChange={handleInputChange}
                      placeholder="e.g., ABC-1234"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="mileage">Mileage (km/l)</label>
                    <input
                      type="number"
                      id="mileage"
                      name="mileage"
                      value={newCar.mileage}
                      onChange={handleInputChange}
                      placeholder="e.g., 15"
                      min="1"
                      required
                    />
                  </div>

                  <div className="form-group full-width">
                    <label htmlFor="imageUrl">Image URL</label>
                    <input
                      type="url"
                      id="imageUrl"
                      name="imageUrl"
                      value={newCar.imageUrl}
                      onChange={handleInputChange}
                      placeholder="https://example.com/car-image.jpg"
                    />
                  </div>

                  <div className="form-group full-width">
                    <label htmlFor="description">Description</label>
                    <textarea
                      id="description"
                      name="description"
                      value={newCar.description}
                      onChange={handleInputChange}
                      placeholder="Car description and features..."
                      rows="3"
                    />
                  </div>

                  <div className="form-group">
                    <label>
                      <input
                        type="checkbox"
                        name="available"
                        checked={newCar.available}
                        onChange={handleInputChange}
                      />
                      Available for Rental
                    </label>
                  </div>

                  <div className="form-group">
                    <label>
                      <input
                        type="checkbox"
                        name="airConditioning"
                        checked={newCar.airConditioning}
                        onChange={handleInputChange}
                      />
                      Air Conditioning
                    </label>
                  </div>

                  <div className="form-group">
                    <label>
                      <input
                        type="checkbox"
                        name="gpsNavigation"
                        checked={newCar.gpsNavigation}
                        onChange={handleInputChange}
                      />
                      GPS Navigation
                    </label>
                  </div>

                  <div className="form-group">
                    <label>
                      <input
                        type="checkbox"
                        name="bluetoothConnectivity"
                        checked={newCar.bluetoothConnectivity}
                        onChange={handleInputChange}
                      />
                      Bluetooth Connectivity
                    </label>
                  </div>
                </div>

                <div className="form-actions">
                  <button type="submit" className="btn primary">
                    <i className="fas fa-save"></i> Add Car
                  </button>
                  <button 
                    type="button" 
                    className="btn secondary"
                    onClick={() => setIsAddingCar(false)}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          )}

          <div className="cars-list">
            <h3>Current Cars ({cars.length})</h3>
            {cars.length === 0 ? (
              <div className="empty-state">
                <i className="fas fa-car"></i>
                <p>No cars found. Add your first car to get started!</p>
              </div>
            ) : (
              <div className="cars-grid">
                {cars.map(car => (
                  <div key={car.id} className="car-card">
                    {car.imageUrl && (
                      <img src={car.imageUrl} alt={car.name} className="car-image" />
                    )}
                    <div className="car-details">
                      <h4>{car.make} {car.model} ({car.year})</h4>
                      <p className="car-info">{car.color} • {car.seatingCapacity} seats • {car.transmission}</p>
                      <p className="car-type">{car.carType}</p>
                      <p className="car-price">${car.pricePerDay}/day</p>
                      <p className="car-mileage">{car.mileage} km/l • {car.fuelType}</p>
                      <p className={`car-status ${car.available ? 'available' : 'unavailable'}`}>
                        {car.available ? '✅ Available' : '❌ Unavailable'}
                      </p>
                      <div className="car-actions">
                        <button 
                          className="btn danger small"
                          onClick={() => handleDeleteCar(car.id)}
                        >
                          <i className="fas fa-trash"></i> Delete
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default AdminDashboard;