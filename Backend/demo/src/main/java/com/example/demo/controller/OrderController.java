package com.example.demo.controller;

import com.example.demo.model.BookingStatus;
import com.example.demo.model.FormDetails;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.FormDetailsService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class OrderController {

    @Autowired
    private FormDetailsService formDetailsService;

    @Autowired
    private UserService userService;

    // Get all pending orders for admin approval
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<FormDetails>> getPendingOrders() {
        List<FormDetails> pendingOrders = formDetailsService.findByBookingStatus(BookingStatus.PENDING);
        return ResponseEntity.ok(pendingOrders);
    }

    // Get all orders with any status for admin view
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FormDetails>> getAllOrders() {
        List<FormDetails> allOrders = formDetailsService.findAll();
        return ResponseEntity.ok(allOrders);
    }

    // Get orders by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<FormDetails>> getOrdersByStatus(@PathVariable String status) {
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            List<FormDetails> orders = formDetailsService.findByBookingStatus(bookingStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Approve an order
    @PutMapping("/{orderId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<FormDetails> approveOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User admin = userService.findByEmail(userDetails.getUsername());
            
            String adminNotes = requestBody.get("adminNotes");
            
            FormDetails updatedOrder = formDetailsService.updateOrderStatus(
                orderId, 
                BookingStatus.APPROVED, 
                admin.getId(), 
                adminNotes
            );
            
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Reject an order
    @PutMapping("/{orderId}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<FormDetails> rejectOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User admin = userService.findByEmail(userDetails.getUsername());
            
            String adminNotes = requestBody.get("adminNotes");
            
            FormDetails updatedOrder = formDetailsService.updateOrderStatus(
                orderId, 
                BookingStatus.REJECTED, 
                admin.getId(), 
                adminNotes
            );
            
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Mark order as completed
    @PutMapping("/{orderId}/complete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<FormDetails> completeOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User admin = userService.findByEmail(userDetails.getUsername());
            
            String adminNotes = requestBody.get("adminNotes");
            
            FormDetails updatedOrder = formDetailsService.updateOrderStatus(
                orderId, 
                BookingStatus.COMPLETED, 
                admin.getId(), 
                adminNotes
            );
            
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get order statistics for admin dashboard
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        Map<String, Object> statistics = formDetailsService.getOrderStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    // Get bookings for cars owned by the authenticated user
    @GetMapping("/my-cars")
    public ResponseEntity<List<FormDetails>> getBookingsForMyCars(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            
            List<FormDetails> bookings = formDetailsService.findBookingsForUsersCars(user.getId());
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Reject a booking for a car owned by the authenticated user
    @PutMapping("/{orderId}/reject-as-owner")
    public ResponseEntity<FormDetails> rejectOrderAsOwner(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            
            String adminNotes = requestBody.get("adminNotes");
            
            FormDetails updatedOrder = formDetailsService.updateOrderStatusWithOwnershipCheck(
                orderId, 
                BookingStatus.REJECTED, 
                user.getId(), 
                adminNotes
            );
            
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build(); // Forbidden - not owner
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Approve a booking for a car owned by the authenticated user
    @PutMapping("/{orderId}/approve-as-owner")
    public ResponseEntity<FormDetails> approveOrderAsOwner(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            
            String adminNotes = requestBody.get("adminNotes");
            
            FormDetails updatedOrder = formDetailsService.updateOrderStatusWithOwnershipCheck(
                orderId, 
                BookingStatus.APPROVED, 
                user.getId(), 
                adminNotes
            );
            
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build(); // Forbidden - not owner
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
