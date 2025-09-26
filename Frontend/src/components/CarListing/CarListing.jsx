import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { carService } from '../../services/carService';
import { useTheme } from '../../contexts/ThemeContext';
import { safeErrorMessage } from '../../utils/reactSafeError';
import Header from '../common/Header';
import Icon, { CarTypeIcon } from '../common/Icon';
import './CarListing.css';

const CarListing = () => {
  const [cars, setCars] = useState([]);
  const [filteredCars, setFilteredCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [filters, setFilters] = useState({
    carType: '',
    transmission: '',
    fuelType: '',
    minPrice: '',
    maxPrice: '',
    seatingCapacity: ''
  });
  const [filterOptions, setFilterOptions] = useState({
    carTypes: [],
    makes: [],
    fuelTypes: []
  });
  const [showFilters, setShowFilters] = useState(false);

  const { isDarkMode } = useTheme();
  const navigate = useNavigate();

  useEffect(() => {
    fetchCars();
    fetchFilterOptions();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [cars, searchKeyword, filters]);

  const fetchCars = async () => {
    setLoading(true);
    try {
      const result = await carService.getAllAvailableCars();
      if (result.success) {
        setCars(result.data);
        setFilteredCars(result.data);
        setError('');
      } else {
        setError(safeErrorMessage(result, 'Failed to load cars'));
      }
    } catch (err) {
      setError(safeErrorMessage(err, 'Failed to fetch cars'));
    } finally {
      setLoading(false);
    }
  };

  const fetchFilterOptions = async () => {
    try {
      const result = await carService.getFilterOptions();
      if (result.success) {
        setFilterOptions(result.data);
      }
    } catch (err) {
      console.error('Failed to fetch filter options:', err);
    }
  };

  const applyFilters = () => {
    let filtered = [...cars];

    // Apply search keyword filter
    if (searchKeyword.trim()) {
      filtered = filtered.filter(car =>
        car.make.toLowerCase().includes(searchKeyword.toLowerCase()) ||
        car.model.toLowerCase().includes(searchKeyword.toLowerCase()) ||
        car.carType.toLowerCase().includes(searchKeyword.toLowerCase())
      );
    }

    // Apply other filters
    if (filters.carType) {
      filtered = filtered.filter(car => car.carType === filters.carType);
    }
    if (filters.transmission) {
      filtered = filtered.filter(car => car.transmission === filters.transmission);
    }
    if (filters.fuelType) {
      filtered = filtered.filter(car => car.fuelType === filters.fuelType);
    }
    if (filters.minPrice) {
      filtered = filtered.filter(car => car.pricePerDay >= parseFloat(filters.minPrice));
    }
    if (filters.maxPrice) {
      filtered = filtered.filter(car => car.pricePerDay <= parseFloat(filters.maxPrice));
    }
    if (filters.seatingCapacity) {
      filtered = filtered.filter(car => car.seatingCapacity >= parseInt(filters.seatingCapacity));
    }

    setFilteredCars(filtered);
  };

  const handleSearch = (e) => {
    setSearchKeyword(e.target.value);
  };

  const handleFilterChange = (filterName, value) => {
    setFilters(prev => ({
      ...prev,
      [filterName]: value
    }));
  };

  const clearFilters = () => {
    setFilters({
      carType: '',
      transmission: '',
      fuelType: '',
      minPrice: '',
      maxPrice: '',
      seatingCapacity: ''
    });
    setSearchKeyword('');
  };

  const handleCarClick = (carId) => {
    navigate(`/car/${carId}`);
  };

  if (loading) {
    return (
      <div className="car-listing-container">
        <Header title="Available Cars" />
        <div className="loading-state">
          <div className="loading-spinner"></div>
          <p className="text-lg text-secondary">Loading cars...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="car-listing-container">
        <Header title="Available Cars" />
        <div className="error-state">
          <Icon name="warning" size="xl" className="text-error" />
          <p className="text-lg text-error">{typeof error === 'string' ? error : 'An error occurred while loading cars'}</p>
          <button onClick={fetchCars} className="btn btn-primary">
            <Icon name="refresh" size="sm" />
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="car-listing-container">
      <Header title="Available Cars" />
      
      <main className="container">
        <div className="page-header">
          <h1 className="text-3xl font-bold text-primary mb-sm">Available Cars</h1>
          <p className="text-lg text-secondary">Find the perfect car for your journey</p>
        </div>

        {/* Search and Filter Section */}
        <div className="search-filter-section card">
          <div className="search-bar">
            <div className="search-input-wrapper">
              <Icon name="search" size="sm" className="search-icon" />
              <input
                type="text"
                placeholder="Search by make, model, or type..."
                value={searchKeyword}
                onChange={handleSearch}
                className="form-input search-input"
              />
            </div>
            <button 
              className="btn btn-secondary filter-toggle"
              onClick={() => setShowFilters(!showFilters)}
            >
              <Icon name="filter" size="sm" />
              Filters
              <Icon name={showFilters ? 'chevronUp' : 'chevronDown'} size="sm" />
            </button>
          </div>

          {showFilters && (
            <div className="filters-panel">
              <div className="filters-row">
                <div className="form-group">
                  <label className="form-label">Car Type</label>
                  <select
                    value={filters.carType}
                    onChange={(e) => handleFilterChange('carType', e.target.value)}
                    className="form-select"
                  >
                    <option value="">All Types</option>
                    {filterOptions.carTypes.map(type => (
                      <option key={type} value={type}>{type}</option>
                    ))}
                  </select>
                </div>

              <select
                value={filters.transmission}
                onChange={(e) => handleFilterChange('transmission', e.target.value)}
                className="filter-select"
              >
                <option value="">All Transmissions</option>
                <option value="Automatic">Automatic</option>
                <option value="Manual">Manual</option>
              </select>

              <select
                value={filters.fuelType}
                onChange={(e) => handleFilterChange('fuelType', e.target.value)}
                className="filter-select"
              >
                <option value="">All Fuel Types</option>
                {filterOptions.fuelTypes.map(fuel => (
                  <option key={fuel} value={fuel}>{fuel}</option>
                ))}
              </select>
            </div>

            <div className="filters-row">
              <input
                type="number"
                placeholder="Min Price"
                value={filters.minPrice}
                onChange={(e) => handleFilterChange('minPrice', e.target.value)}
                className="filter-input"
              />
              <input
                type="number"
                placeholder="Max Price"
                value={filters.maxPrice}
                onChange={(e) => handleFilterChange('maxPrice', e.target.value)}
                className="filter-input"
              />
              <select
                value={filters.seatingCapacity}
                onChange={(e) => handleFilterChange('seatingCapacity', e.target.value)}
                className="filter-select"
              >
                <option value="">Any Capacity</option>
                <option value="5">5+ Seats</option>
                <option value="7">7+ Seats</option>
              </select>
              <button onClick={clearFilters} className="clear-filters-btn">
                Clear Filters
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Results Count */}
      <div className="results-info">
        <p>{filteredCars.length} car{filteredCars.length !== 1 ? 's' : ''} found</p>
      </div>

      {/* Cars Grid */}
      <div className="cars-grid">
        {filteredCars.length === 0 ? (
          <div className="no-results">
            <Icon name="search" size="xl" className="text-muted" />
            <p className="text-lg text-secondary mt-md">No cars found matching your criteria</p>
            <button onClick={clearFilters} className="btn btn-primary mt-lg">
              <Icon name="refresh" size="sm" />
              Clear Filters
            </button>
          </div>
        ) : (
          filteredCars.map(car => (
            <div 
              key={car.id} 
              className="car-card card" 
              onClick={() => handleCarClick(car.id)}
            >
              <div className="car-image">
                <img 
                  src={car.imageUrl || '/placeholder-car.png'} 
                  alt={`${car.make} ${car.model}`}
                  onError={(e) => {
                    e.target.src = '/placeholder-car.png';
                  }}
                />
                <div className="car-status">
                  {car.available ? 'Available' : 'Booked'}
                </div>
              </div>
              
              <div className="car-details">
                <h3 className="car-title">{car.make} {car.model}</h3>
                <p className="car-year">{car.year}</p>
                
                <div className="car-specs">
                  <div className="spec-item">
                    <CarTypeIcon type={car.carType} size="sm" showLabel={false} />
                    <span>{car.carType}</span>
                  </div>
                  <div className="spec-item">
                    <Icon name="settings" size="sm" />
                    <span>{car.transmission}</span>
                  </div>
                  <div className="spec-item">
                    <Icon name="location" size="sm" />
                    <span>{car.fuelType}</span>
                  </div>
                  <div className="spec-item">
                    <Icon name="user" size="sm" />
                    <span>{car.seatingCapacity} seats</span>
                  </div>
                </div>

                <div className="car-features">
                  {car.airConditioning && <span className="feature">AC</span>}
                  {car.gpsNavigation && <span className="feature">GPS</span>}
                  {car.bluetoothConnectivity && <span className="feature">Bluetooth</span>}
                </div>

                <div className="car-price">
                  <span className="price">₹{car.pricePerDay}</span>
                  <span className="per-day">per day</span>
                </div>

                <div className="car-rating">
                  <span className="mileage">{car.mileage} km/l</span>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

        {/* Quick Navigation */}
        <div className="quick-nav mt-2xl">
          <Link to="/dashboard" className="btn btn-secondary">
            <Icon name="chevronLeft" size="sm" />
            Back to Dashboard
          </Link>
        </div>
      </main>
    </div>
  );
};

export default CarListing;