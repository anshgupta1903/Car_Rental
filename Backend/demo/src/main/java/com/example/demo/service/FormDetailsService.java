package com.example.demo.service;

import com.example.demo.model.BookingStatus;
import com.example.demo.model.Car;
import com.example.demo.model.FormDetails;
import com.example.demo.repository.FormDetailsRepository;
import com.example.demo.service.CarService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormDetailsService {

    private final FormDetailsRepository repository;
    
    @Autowired
    private CarService carService;

    public FormDetailsService(FormDetailsRepository repository) {
        this.repository = repository;
    }

    public FormDetails save(FormDetails details) {
        // If car is provided with just an ID, fetch the full car entity
        if (details.getCar() != null && details.getCar().getId() != null) {
            try {
                Car fullCar = carService.findById(details.getCar().getId());
                
                // Check if the car is available for booking (not booked and not disabled)
                if (!fullCar.getAvailable()) {
                    throw new IllegalStateException("Car with ID " + details.getCar().getId() + " is not available for booking");
                }
                if (fullCar.getDisabled() != null && fullCar.getDisabled()) {
                    throw new IllegalStateException("Car with ID " + details.getCar().getId() + " has been disabled and is not available for booking");
                }
                
                details.setCar(fullCar);
                System.out.println("Successfully found car: " + fullCar.getMake() + " " + fullCar.getModel() + " (ID: " + fullCar.getId() + ")");
            } catch (ResourceNotFoundException e) {
                System.err.println("Car not found with ID: " + details.getCar().getId());
                throw new ResourceNotFoundException("Car not found with ID: " + details.getCar().getId());
            } catch (Exception e) {
                System.err.println("Error fetching car with ID " + details.getCar().getId() + ": " + e.getMessage());
                throw e;
            }
        } else if (details.getCar() == null || details.getCar().getId() == null) {
            throw new IllegalArgumentException("Car ID is required for booking");
        }
        
        return repository.save(details);
    }

    public List<FormDetails> findRecent(int limit) {
        var pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findAll(pageable).getContent();
    }

    public List<FormDetails> findByEmail(String email) {
        return repository.findByEmailOrderByCreatedAtDesc(email);
    }
    
    // Admin management methods
    public List<FormDetails> findAll() {
        return repository.findAllWithCar();
    }
    
    public List<FormDetails> findByBookingStatus(BookingStatus status) {
        return repository.findByBookingStatusOrderByCreatedAtDesc(status);
    }
    
    public FormDetails findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
    
    public FormDetails updateOrderStatus(Long orderId, BookingStatus status, Long adminId, String adminNotes) {
        FormDetails order = findById(orderId);
        order.setBookingStatus(status);
        order.setApprovedByAdminId(adminId);
        order.setAdminNotes(adminNotes);
        order.setApprovalDate(LocalDateTime.now());
        return repository.save(order);
    }
    
    // Update order status with ownership validation
    public FormDetails updateOrderStatusWithOwnershipCheck(Long orderId, BookingStatus status, Long userId, String adminNotes) {
        FormDetails order = findById(orderId);
        
        // Check if the user owns the car associated with this booking
        if (order.getCar() != null && order.getCar().getOwner() != null) {
            if (!order.getCar().getOwner().getId().equals(userId)) {
                throw new IllegalArgumentException("You can only manage bookings for cars you own");
            }
        }
        
        order.setBookingStatus(status);
        order.setApprovedByAdminId(userId);
        order.setAdminNotes(adminNotes);
        order.setApprovalDate(LocalDateTime.now());
        return repository.save(order);
    }
    
    // Get bookings for cars owned by a specific user
    public List<FormDetails> findBookingsForUsersCars(Long userId) {
        return repository.findByCarOwnerIdOrderByCreatedAtDesc(userId);
    }
    
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalOrders = repository.count();
        long pendingOrders = repository.countByBookingStatus(BookingStatus.PENDING);
        long approvedOrders = repository.countByBookingStatus(BookingStatus.APPROVED);
        long rejectedOrders = repository.countByBookingStatus(BookingStatus.REJECTED);
        long completedOrders = repository.countByBookingStatus(BookingStatus.COMPLETED);
        
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("approvedOrders", approvedOrders);
        stats.put("rejectedOrders", rejectedOrders);
        stats.put("completedOrders", completedOrders);
        
        return stats;
    }
}
