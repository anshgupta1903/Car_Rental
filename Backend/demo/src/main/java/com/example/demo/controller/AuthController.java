package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.SignupRequest;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000") // allow React frontend
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@Valid @RequestBody SignupRequest signupRequest) {
        System.out.println("Signup request received for user: " + signupRequest.getEmail());
        
        // Create User entity from SignupRequest
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());
        
        // SECURITY: All public signups are restricted to USER role only
        // Ignore any role information sent from frontend for security
        User savedUser = userService.saveUser(user); // Always assigns ROLE_USER
        
        System.out.println("Signup successful for user: " + savedUser.getEmail());
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("Login request received for email: " + loginRequest.getEmail());
        
        return userService.login(loginRequest.getEmail(), loginRequest.getPassword())
                .<ResponseEntity<?>>map(loggedInUser -> {
                    System.out.println("Login successful for user: " + loggedInUser.getEmail());
                    return ResponseEntity.ok(loggedInUser);
                })
                .orElseGet(() -> {
                    System.out.println("Login failed for email: " + loginRequest.getEmail());
                    return ResponseEntity.status(401).body("Invalid Credentials");
                });
    }
    
    @PostMapping("/signup/admin")
    public ResponseEntity<?> signupAdmin(@Valid @RequestBody Map<String, String> adminRequest) {
        // DISABLED: Admin signup has been disabled for security reasons
        // Only the default admin account is allowed, which is created automatically on startup
        System.out.println("Admin signup attempt blocked for security reasons");
        return ResponseEntity.status(403).body("Admin signup is disabled. Contact system administrator.");
    }
    
    @PostMapping("/signup/manager")
    public ResponseEntity<?> signupManager(@Valid @RequestBody Map<String, String> managerRequest) {
        // DISABLED: Manager signup has been disabled for security reasons
        // Only admins can create manager accounts through the admin panel
        System.out.println("Manager signup attempt blocked for security reasons");
        return ResponseEntity.status(403).body("Manager signup is disabled. Contact system administrator.");
    }
}
