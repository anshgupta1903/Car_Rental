package com.example.demo.controller;

import com.example.demo.model.FormDetails;
import com.example.demo.service.FormDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class FormDetailsController {

    private final FormDetailsService service;
    private static final Logger log = LoggerFactory.getLogger(FormDetailsController.class);

    public FormDetailsController(FormDetailsService service) {
        this.service = service;
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookRide(@Valid @RequestBody FormDetails details, BindingResult bindingResult) {
        try {
            log.info("Received booking request");
            log.info("Request details: pickupLocation={}, dropoffLocation={}, pickupDateTime={}, returnDateTime={}", 
                    details.getPickupLocation(), details.getDropoffLocation(), details.getPickupDateTime(), details.getReturnDateTime());
            log.info("Customer info: fullName={}, email={}, phone={}", 
                    details.getFullName(), details.getEmail(), details.getPhoneNumber());
            log.info("Car info: carType={}, carId={}, totalAmount={}, rentalDays={}", 
                    details.getCarType(), details.getCar() != null ? details.getCar().getId() : "null", 
                    details.getTotalAmount(), details.getRentalDays());
            
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Validation failed for one or more fields");
                
                Map<String, String> fieldErrors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                    ));
                
                errorResponse.put("fieldErrors", fieldErrors);
                log.error("Validation errors: {}", fieldErrors);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            FormDetails saved = service.save(details);
            log.info("Successfully saved booking with id={}", saved.getId());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error saving booking: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<FormDetails>> recent() {
        return ResponseEntity.ok(service.findRecent(10));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<FormDetails>> getUserBookings(@PathVariable String email) {
        log.info("Fetching bookings for user: {}", email);
        List<FormDetails> bookings = service.findByEmail(email);
        return ResponseEntity.ok(bookings);
    }
}
