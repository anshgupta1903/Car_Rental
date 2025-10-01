package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    // Get all available cars (excluding disabled cars)
    public List<Car> getAllAvailableCars() {
        return carRepository.findByAvailableTrueAndDisabledFalse();
    }
    
    // Get all enabled cars (both available and unavailable but not disabled)
    public List<Car> getAllEnabledCars() {
        return carRepository.findByDisabledFalse();
    }

    // Get all cars (including unavailable)
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // Get car by ID
    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    // Get available car by ID
    public Optional<Car> getAvailableCarById(Long id) {
        Optional<Car> car = carRepository.findById(id);
        if (car.isPresent() && car.get().getAvailable()) {
            return car;
        }
        return Optional.empty();
    }

    // Search cars by keyword (make or model)
    public List<Car> searchCars(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAvailableCars();
        }
        return carRepository.findByMakeOrModelContainingIgnoreCase(keyword.trim());
    }

    // Filter cars with multiple criteria
    public List<Car> filterCars(String carType, String transmission, String fuelType,
                               BigDecimal minPrice, BigDecimal maxPrice, Integer seatingCapacity) {
        return carRepository.findCarsWithFilters(carType, transmission, fuelType,
                                                minPrice, maxPrice, seatingCapacity);
    }

    // Get cars by type
    public List<Car> getCarsByType(String carType) {
        return carRepository.findByCarTypeAndAvailableTrueAndDisabledFalse(carType);
    }

    // Get cars by price range
    public List<Car> getCarsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return carRepository.findByPriceRange(minPrice, maxPrice);
    }

    // Get cars by seating capacity
    public List<Car> getCarsBySeatingCapacity(Integer seatingCapacity) {
        return carRepository.findBySeatingCapacityAndAvailableTrue(seatingCapacity);
    }

    // Book a car (mark as unavailable)
    public boolean bookCar(Long carId) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isPresent() && carOpt.get().getAvailable()) {
            Car car = carOpt.get();
            car.setAvailable(false);
            car.setUpdatedAt(LocalDateTime.now());
            carRepository.save(car);
            return true;
        }
        return false;
    }

    // Release a car (mark as available)
    public boolean releaseCar(Long carId) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isPresent()) {
            Car car = carOpt.get();
            car.setAvailable(true);
            car.setUpdatedAt(LocalDateTime.now());
            carRepository.save(car);
            return true;
        }
        return false;
    }

    // Add a new car
    public Car addCar(Car car) {
        car.setAvailable(true);
        car.setCreatedAt(LocalDateTime.now());
        car.setUpdatedAt(LocalDateTime.now());
        return carRepository.save(car);
    }


    // Get filter options
    public List<String> getDistinctCarTypes() {
        return carRepository.findDistinctCarTypes();
    }

    public List<String> getDistinctMakes() {
        return carRepository.findDistinctMakes();
    }

    public List<String> getDistinctFuelTypes() {
        return carRepository.findDistinctFuelTypes();
    }

    // Get car by license plate
    public Optional<Car> getCarByLicensePlate(String licensePlate) {
        return carRepository.findByLicensePlate(licensePlate);
    }

    // Check if car is available (not booked and not disabled)
    public boolean isCarAvailable(Long carId) {
        Optional<Car> car = carRepository.findById(carId);
        return car.isPresent() && car.get().getAvailable() && !car.get().getDisabled();
    }

    // Get total number of available cars (excluding disabled)
    public long getAvailableCarCount() {
        return carRepository.findByAvailableTrueAndDisabledFalse().size();
    }

    // Get total number of cars
    public long getTotalCarCount() {
        return carRepository.count();
    }
    
    // Admin-specific methods
    public List<Car> findAll() {
        return carRepository.findAll();
    }
    
    public Car findById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));
    }
    
    public Car saveCar(Car car) {
        if (car.getId() == null) {
            car.setCreatedAt(LocalDateTime.now());
            car.setAvailable(true);
        }
        car.setUpdatedAt(LocalDateTime.now());
        return carRepository.save(car);
    }
    
    public Car updateCar(Long carId, Car carDetails) {
        Car existingCar = findById(carId);
        
        existingCar.setMake(carDetails.getMake());
        existingCar.setModel(carDetails.getModel());
        existingCar.setYear(carDetails.getYear());
        existingCar.setCarType(carDetails.getCarType());
        existingCar.setTransmission(carDetails.getTransmission());
        existingCar.setFuelType(carDetails.getFuelType());
        existingCar.setSeatingCapacity(carDetails.getSeatingCapacity());
        existingCar.setPricePerDay(carDetails.getPricePerDay());
        existingCar.setColor(carDetails.getColor());
        existingCar.setMileage(carDetails.getMileage());
        existingCar.setImageUrl(carDetails.getImageUrl());
        existingCar.setDescription(carDetails.getDescription());
        existingCar.setAirConditioning(carDetails.getAirConditioning());
        existingCar.setGpsNavigation(carDetails.getGpsNavigation());
        existingCar.setBluetoothConnectivity(carDetails.getBluetoothConnectivity());
        existingCar.setUpdatedAt(LocalDateTime.now());
        
        return carRepository.save(existingCar);
    }
    
    public void deleteCar(Long carId) {
        Car car = findById(carId);
        carRepository.delete(car);
    }
    
    public Map<String, Object> getCarStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalCars = carRepository.count();
        long availableCars = carRepository.findByAvailableTrue().size();
        long unavailableCars = totalCars - availableCars;
        
        stats.put("totalCars", totalCars);
        stats.put("availableCars", availableCars);
        stats.put("unavailableCars", unavailableCars);
        stats.put("carTypes", getDistinctCarTypes());
        stats.put("makes", getDistinctMakes());
        stats.put("fuelTypes", getDistinctFuelTypes());
        
        return stats;
    }
    
    public Car updateCarPricing(Long carId, Map<String, Object> pricingData) {
        Car car = findById(carId);
        
        if (pricingData.containsKey("pricePerDay")) {
            BigDecimal newPrice = new BigDecimal(pricingData.get("pricePerDay").toString());
            car.setPricePerDay(newPrice);
        }
        
        car.setUpdatedAt(LocalDateTime.now());
        return carRepository.save(car);
    }
    
    // Disable a car (admin/owner only)
    public boolean disableCar(Long carId) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isPresent()) {
            Car car = carOpt.get();
            car.setDisabled(true);
            car.setUpdatedAt(LocalDateTime.now());
            carRepository.save(car);
            return true;
        }
        return false;
    }
    
    // Enable a car (admin/owner only)
    public boolean enableCar(Long carId) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isPresent()) {
            Car car = carOpt.get();
            car.setDisabled(false);
            car.setUpdatedAt(LocalDateTime.now());
            carRepository.save(car);
            return true;
        }
        return false;
    }
    
    // Check if a car can be disabled by a specific user (owner or admin)
    public boolean canUserDisableCar(Long carId, Long userId) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isPresent()) {
            Car car = carOpt.get();
            return car.getOwner() != null && car.getOwner().getId().equals(userId);
        }
        return false;
    }
}