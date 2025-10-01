package com.example.demo.controller;

import com.example.demo.model.Car;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.CarService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private CarService carService;
    
    @Autowired
    private UserService userService;
    
    private static final String DEFAULT_ADMIN_EMAIL = "admin@carrental.com";

    // Get all cars (including unavailable ones) for admin management
    @GetMapping("/cars")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Car>> getAllCarsForAdmin() {
        List<Car> allCars = carService.findAll();
        return ResponseEntity.ok(allCars);
    }

    // Add a new car
    @PostMapping("/cars")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Car> addCar(@Valid @RequestBody Car car) {
        try {
            Car savedCar = carService.saveCar(car);
            return ResponseEntity.ok(savedCar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update an existing car
    @PutMapping("/cars/{carId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Car> updateCar(@PathVariable Long carId, @Valid @RequestBody Car carDetails) {
        try {
            Car updatedCar = carService.updateCar(carId, carDetails);
            return ResponseEntity.ok(updatedCar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete a car
    @DeleteMapping("/cars/{carId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCar(@PathVariable Long carId) {
        try {
            carService.deleteCar(carId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Toggle car availability
    @PutMapping("/cars/{carId}/toggle-availability")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Car> toggleCarAvailability(@PathVariable Long carId) {
        try {
            Car car = carService.findById(carId);
            car.setAvailable(!car.getAvailable());
            Car updatedCar = carService.saveCar(car);
            return ResponseEntity.ok(updatedCar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get car statistics
    @GetMapping("/cars/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCarStatistics() {
        Map<String, Object> statistics = carService.getCarStatistics();
        return ResponseEntity.ok(statistics);
    }

    // Update car pricing
    @PutMapping("/cars/{carId}/pricing")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Car> updateCarPricing(@PathVariable Long carId, @RequestBody Map<String, Object> pricingData) {
        try {
            Car updatedCar = carService.updateCarPricing(carId, pricingData);
            return ResponseEntity.ok(updatedCar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // === USER MANAGEMENT ENDPOINTS ===
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = userService.findAllUsers().stream()
                .filter(user -> user.getRoles().contains(Role.ROLE_ADMIN))
                .collect(Collectors.toList());
        return ResponseEntity.ok(admins);
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            User userToDelete = userService.findById(userId);
            
            // Prevent deletion of the default admin
            if (userToDelete.getEmail().equals(DEFAULT_ADMIN_EMAIL)) {
                return ResponseEntity.badRequest()
                        .body("Cannot delete the default admin user");
            }
            
            // Prevent admin from deleting themselves
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = auth.getName();
            if (userToDelete.getEmail().equals(currentUserEmail)) {
                return ResponseEntity.badRequest()
                        .body("Cannot delete your own account");
            }
            
            // If deleting an admin, ensure at least one admin remains
            if (userToDelete.getRoles().contains(Role.ROLE_ADMIN)) {
                long adminCount = userService.findAllUsers().stream()
                        .filter(user -> user.getRoles().contains(Role.ROLE_ADMIN))
                        .count();
                
                if (adminCount <= 1) {
                    return ResponseEntity.badRequest()
                            .body("Cannot delete the last admin user");
                }
            }
            
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    @PutMapping("/users/{userId}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserPassword(@PathVariable Long userId, 
                                              @RequestBody Map<String, String> passwordData) {
        try {
            String newPassword = passwordData.get("password");
            if (newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                        .body("Password must be at least 6 characters long");
            }

            User user = userService.findById(userId);
            user.setPassword(newPassword);
            userService.saveUser(user);
            
            return ResponseEntity.ok("Password updated successfully");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error updating password: " + e.getMessage());
        }
    }

    @PutMapping("/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long userId, 
                                           @RequestBody Map<String, List<String>> roleData) {
        try {
            User user = userService.findById(userId);
            
            // Prevent modification of default admin roles
            if (user.getEmail().equals(DEFAULT_ADMIN_EMAIL)) {
                return ResponseEntity.badRequest()
                        .body("Cannot modify roles of the default admin user");
            }
            
            List<String> roleStrings = roleData.get("roles");
            List<Role> roles = roleStrings.stream()
                    .map(roleString -> {
                        try {
                            return Role.valueOf(roleString);
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Invalid role: " + roleString);
                        }
                    })
                    .collect(Collectors.toList());
            
            userService.updateUserRoles(userId, roles);
            return ResponseEntity.ok("User roles updated successfully");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error updating user roles: " + e.getMessage());
        }
    }

    @PostMapping("/cleanup-admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cleanupAdminUsers() {
        try {
            int removedCount = 0;
            List<User> allUsers = userService.findAllUsers();
            
            for (User user : allUsers) {
                if (user.getRoles().contains(Role.ROLE_ADMIN) && 
                    !user.getEmail().equals(DEFAULT_ADMIN_EMAIL)) {
                    
                    userService.deleteUser(user.getId());
                    removedCount++;
                }
            }
            
            return ResponseEntity.ok("Removed " + removedCount + " admin users. Default admin preserved.");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error cleaning up admin users: " + e.getMessage());
        }
    }
}
