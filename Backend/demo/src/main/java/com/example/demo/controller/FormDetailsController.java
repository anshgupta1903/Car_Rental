package com.example.demo.controller;

import com.example.demo.model.FormDetails;
import com.example.demo.service.FormDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

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
    public ResponseEntity<FormDetails> bookRide(@RequestBody FormDetails details) {
        log.info("Received booking: pickupLocation={}, dropoffLocation={}, pickupDateTime={}, returnDateTime={}, carType={}, fullName={}, email={}",
                details.getPickupLocation(), details.getDropoffLocation(), details.getPickupDateTime(), details.getReturnDateTime(), details.getCarType(), details.getFullName(), details.getEmail());
        FormDetails saved = service.save(details);
        log.info("Saved booking with id={}", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<FormDetails>> recent() {
        return ResponseEntity.ok(service.findRecent(10));
    }
}
