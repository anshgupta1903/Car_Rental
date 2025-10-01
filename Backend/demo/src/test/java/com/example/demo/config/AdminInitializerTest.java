package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInitializer adminInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDefaultAdminIfNotExists_WhenNoAdminExists() throws Exception {
        // Arrange
        when(userRepository.findByEmail("admin@carrental.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        
        User savedAdmin = new User();
        savedAdmin.setEmail("admin@carrental.com");
        when(userRepository.save(any(User.class))).thenReturn(savedAdmin);

        // Act
        adminInitializer.run();

        // Assert
        verify(userRepository, times(1)).findByEmail("admin@carrental.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("Admin@123!");
    }

    @Test
    void testCreateDefaultAdminIfNotExists_WhenAdminAlreadyExists() throws Exception {
        // Arrange
        User existingAdmin = new User();
        existingAdmin.setEmail("admin@carrental.com");
        existingAdmin.setRoles(Set.of(Role.ROLE_ADMIN));
        
        when(userRepository.findByEmail("admin@carrental.com")).thenReturn(Optional.of(existingAdmin));
        when(userRepository.findAll()).thenReturn(List.of(existingAdmin));

        // Act
        adminInitializer.run();

        // Assert
        verify(userRepository, times(1)).findByEmail("admin@carrental.com");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testCleanupExistingAdmins_RemovesNonDefaultAdmins() throws Exception {
        // Arrange
        User defaultAdmin = new User();
        defaultAdmin.setId(1L);
        defaultAdmin.setEmail("admin@carrental.com");
        defaultAdmin.setRoles(Set.of(Role.ROLE_ADMIN));

        User otherAdmin = new User();
        otherAdmin.setId(2L);
        otherAdmin.setEmail("other@admin.com");
        otherAdmin.setRoles(Set.of(Role.ROLE_ADMIN));

        User regularUser = new User();
        regularUser.setId(3L);
        regularUser.setEmail("user@example.com");
        regularUser.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.findAll()).thenReturn(List.of(defaultAdmin, otherAdmin, regularUser));
        when(userRepository.findByEmail("admin@carrental.com")).thenReturn(Optional.of(defaultAdmin));

        // Act
        adminInitializer.run();

        // Assert
        verify(userRepository, times(1)).delete(otherAdmin);
        verify(userRepository, never()).delete(defaultAdmin);
        verify(userRepository, never()).delete(regularUser);
    }
}