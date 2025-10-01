package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Default admin credentials
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@carrental.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@123!"; // Secure default password

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Admin Initialization Started ===");
        
        // Remove all existing admin users except the default one
        cleanupExistingAdmins();
        
        // Create default admin if it doesn't exist
        createDefaultAdminIfNotExists();
        
        System.out.println("=== Admin Initialization Completed ===");
    }

    private void cleanupExistingAdmins() {
        System.out.println("Cleaning up existing admin users...");
        
        // Find all users with ADMIN role
        List<User> allUsers = userRepository.findAll();
        
        for (User user : allUsers) {
            if (user.getRoles().contains(Role.ROLE_ADMIN) && 
                !user.getEmail().equals(DEFAULT_ADMIN_EMAIL)) {
                
                System.out.println("Removing admin user: " + user.getEmail());
                userRepository.delete(user);
            }
        }
    }

    private void createDefaultAdminIfNotExists() {
        System.out.println("Checking for default admin user...");
        
        // Check if default admin already exists
        if (userRepository.findByEmail(DEFAULT_ADMIN_EMAIL).isPresent()) {
            System.out.println("Default admin user already exists: " + DEFAULT_ADMIN_EMAIL);
            return;
        }

        // Create default admin user
        User adminUser = new User();
        adminUser.setUsername(DEFAULT_ADMIN_USERNAME);
        adminUser.setEmail(DEFAULT_ADMIN_EMAIL);
        adminUser.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
        adminUser.setRoles(Set.of(Role.ROLE_ADMIN));

        User savedAdmin = userRepository.save(adminUser);
        System.out.println("Created default admin user: " + savedAdmin.getEmail());
        System.out.println("Default admin credentials:");
        System.out.println("  Email: " + DEFAULT_ADMIN_EMAIL);
        System.out.println("  Password: " + DEFAULT_ADMIN_PASSWORD);
        System.out.println("  PLEASE CHANGE THE DEFAULT PASSWORD AFTER FIRST LOGIN!");
    }
}