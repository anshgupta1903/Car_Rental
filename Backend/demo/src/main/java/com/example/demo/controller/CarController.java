package com.example.demo.controller;

import com.example.demo.model.Car;
import com.example.demo.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CarController {

    @Autowired
    private CarService carService;

    private static final Logger log = LoggerFactory.getLogger(CarController.class);

    // Get all available cars
    @GetMapping("/available")
    public ResponseEntity<List<Car>> getAvailableCars() {
        log.info("Fetching all available cars");
        List<Car> cars = carService.getAllAvailableCars();
        return ResponseEntity.ok(cars);
    }

    // Get all cars (admin endpoint)
    @GetMapping("/all")
    public ResponseEntity<List<Car>> getAllCars() {
        log.info("Fetching all cars");
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    // Get car by ID
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        log.info("Fetching car with id: {}", id);
        Optional<Car> car = carService.getCarById(id);
        
        if (car.isPresent()) {
            return ResponseEntity.ok(car.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get available car by ID
    @GetMapping("/available/{id}")
    public ResponseEntity<Car> getAvailableCarById(@PathVariable Long id) {
        log.info("Fetching available car with id: {}", id);
        Optional<Car> car = carService.getAvailableCarById(id);
        
        if (car.isPresent()) {
            return ResponseEntity.ok(car.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Search cars
    @GetMapping("/search")
    public ResponseEntity<List<Car>> searchCars(@RequestParam(required = false) String keyword) {
        log.info("Searching cars with keyword: {}", keyword);
        List<Car> cars = carService.searchCars(keyword);
        return ResponseEntity.ok(cars);
    }

    // Filter cars
    @GetMapping("/filter")
    public ResponseEntity<List<Car>> filterCars(
            @RequestParam(required = false) String carType,
            @RequestParam(required = false) String transmission,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer seatingCapacity) {
        
        log.info("Filtering cars with - carType: {}, transmission: {}, fuelType: {}, minPrice: {}, maxPrice: {}, seatingCapacity: {}", 
                 carType, transmission, fuelType, minPrice, maxPrice, seatingCapacity);
        
        List<Car> cars = carService.filterCars(carType, transmission, fuelType, minPrice, maxPrice, seatingCapacity);
        return ResponseEntity.ok(cars);
    }

    // Get cars by type
    @GetMapping("/type/{carType}")
    public ResponseEntity<List<Car>> getCarsByType(@PathVariable String carType) {
        log.info("Fetching cars of type: {}", carType);
        List<Car> cars = carService.getCarsByType(carType);
        return ResponseEntity.ok(cars);
    }

    // Book a car
    @PostMapping("/{id}/book")
    public ResponseEntity<Map<String, Object>> bookCar(@PathVariable Long id) {
        log.info("Booking car with id: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        boolean success = carService.bookCar(id);
        if (success) {
            response.put("success", true);
            response.put("message", "Car booked successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Car not available or not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Release a car (admin endpoint)
    @PostMapping("/{id}/release")
    public ResponseEntity<Map<String, Object>> releaseCar(@PathVariable Long id) {
        log.info("Releasing car with id: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        boolean success = carService.releaseCar(id);
        if (success) {
            response.put("success", true);
            response.put("message", "Car released successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Car not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Add a new car (admin endpoint)
    @PostMapping
    public ResponseEntity<Car> addCar(@Valid @RequestBody Car car) {
        log.info("Adding new car: {} {}", car.getMake(), car.getModel());
        Car savedCar = carService.addCar(car);
        return ResponseEntity.ok(savedCar);
    }

    // Update car (admin endpoint)
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @Valid @RequestBody Car car) {
        log.info("Updating car with id: {}", id);
        try {
            Car updatedCar = carService.updateCar(id, car);
            return ResponseEntity.ok(updatedCar);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete car (admin endpoint)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCar(@PathVariable Long id) {
        log.info("Deleting car with id: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            carService.deleteCar(id);
            response.put("success", true);
            response.put("message", "Car deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Car not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get filter options
    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, List<String>>> getFilterOptions() {
        log.info("Fetching filter options");
        Map<String, List<String>> filterOptions = new HashMap<>();
        
        filterOptions.put("carTypes", carService.getDistinctCarTypes());
        filterOptions.put("makes", carService.getDistinctMakes());
        filterOptions.put("fuelTypes", carService.getDistinctFuelTypes());
        
        return ResponseEntity.ok(filterOptions);
    }

    // Check car availability
    @GetMapping("/{id}/availability")
    public ResponseEntity<Map<String, Boolean>> checkCarAvailability(@PathVariable Long id) {
        log.info("Checking availability for car with id: {}", id);
        Map<String, Boolean> response = new HashMap<>();
        
        boolean available = carService.isCarAvailable(id);
        response.put("available", available);
        
        return ResponseEntity.ok(response);
    }

    // Get car statistics (admin endpoint)
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getCarStatistics() {
        log.info("Fetching car statistics");
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("totalCars", carService.getTotalCarCount());
        stats.put("availableCars", carService.getAvailableCarCount());
        stats.put("bookedCars", carService.getTotalCarCount() - carService.getAvailableCarCount());
        
        return ResponseEntity.ok(stats);
    }
    
    // Debug endpoint to list all cars with IDs
    @GetMapping("/debug/list")
    public ResponseEntity<List<Map<String, Object>>> debugListCars() {
        log.info("Debug: Listing all cars with IDs");
        List<Car> allCars = carService.getAllCars();
        
        List<Map<String, Object>> carSummaries = allCars.stream().map(car -> {
            Map<String, Object> summary = new HashMap<>();
            summary.put("id", car.getId());
            summary.put("make", car.getMake());
            summary.put("model", car.getModel());
            summary.put("available", car.getAvailable());
            summary.put("pricePerDay", car.getPricePerDay());
            return summary;
        }).toList();
        
        log.info("Found {} cars in database", allCars.size());
        return ResponseEntity.ok(carSummaries);
    }
    
    // Disable a car (admin/owner endpoint)
    @PostMapping("/{id}/disable")
    public ResponseEntity<Map<String, Object>> disableCar(@PathVariable Long id) {
        log.info("Disabling car with id: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        boolean success = carService.disableCar(id);
        if (success) {
            response.put("success", true);
            response.put("message", "Car disabled successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Car not found");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Enable a car (admin/owner endpoint)
    @PostMapping("/{id}/enable")
    public ResponseEntity<Map<String, Object>> enableCar(@PathVariable Long id) {
        log.info("Enabling car with id: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        boolean success = carService.enableCar(id);
        if (success) {
            response.put("success", true);
            response.put("message", "Car enabled successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Car not found");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
