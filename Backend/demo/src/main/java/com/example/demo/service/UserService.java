package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        try {
            // Set default role if no roles are assigned
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.addRole(Role.ROLE_USER);
                System.out.println("Assigned default ROLE_USER to user: " + user.getEmail());
            } else {
                System.out.println("User " + user.getEmail() + " signup with roles: " + user.getRoles());
            }
            
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            System.out.println("Saved user " + savedUser.getEmail() + " with roles: " + savedUser.getRoles());
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            System.err.println("Failed to save user: " + e.getMessage());
            throw new DuplicateResourceException("User with this email or username already exists");
        }
    }

    public User saveUserWithRole(User user, Role role) {
        user.addRole(role);
        return saveUser(user);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        System.out.println("Login attempt for email: " + email);
        
        if (user.isPresent()) {
            System.out.println("User found: " + user.get().getEmail() + " with roles: " + user.get().getRoles());
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                System.out.println("Password match successful for user: " + email);
                return user;
            } else {
                System.out.println("Password match failed for user: " + email);
            }
        } else {
            System.out.println("User not found with email: " + email);
        }
        return Optional.empty();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserRoles(Long userId, List<Role> roles) {
        User user = findById(userId);
        user.getRoles().clear();
        roles.forEach(user::addRole);
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }

    public boolean hasRole(Long userId, Role role) {
        User user = findById(userId);
        return user.getRoles().contains(role);
    }
}