package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.addRole(Role.ROLE_USER);
    }

    @Test
    void saveUser_ShouldSaveUserWithDefaultRole_WhenNoRolesProvided() {
        // Arrange
        User userWithoutRoles = new User();
        userWithoutRoles.setUsername("newuser");
        userWithoutRoles.setEmail("new@example.com");
        userWithoutRoles.setPassword("password123");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userWithoutRoles);

        // Act
        User savedUser = userService.saveUser(userWithoutRoles);

        // Assert
        assertNotNull(savedUser);
        assertTrue(savedUser.getRoles().contains(Role.ROLE_USER));
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void saveUser_ShouldThrowDuplicateResourceException_WhenUserAlreadyExists() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> userService.saveUser(testUser));
    }

    @Test
    void saveUserWithRole_ShouldSaveUserWithSpecificRole() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User savedUser = userService.saveUserWithRole(testUser, Role.ROLE_ADMIN);

        // Assert
        assertTrue(savedUser.getRoles().contains(Role.ROLE_ADMIN));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        // Act
        Optional<User> result = userService.login("test@example.com", "password123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void login_ShouldReturnEmpty_WhenCredentialsAreInvalid() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        // Act
        Optional<User> result = userService.login("test@example.com", "wrongpassword");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findByEmail("test@example.com");

        // Assert
        assertEquals(testUser, result);
    }

    @Test
    void findByEmail_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> userService.findByEmail("nonexistent@example.com"));
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findById(1L);

        // Assert
        assertEquals(testUser, result);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> userService.findById(999L));
    }

    @Test
    void findAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.findAllUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
    }

    @Test
    void hasRole_ShouldReturnTrue_WhenUserHasRole() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.hasRole(1L, Role.ROLE_USER);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasRole_ShouldReturnFalse_WhenUserDoesNotHaveRole() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.hasRole(1L, Role.ROLE_ADMIN);

        // Assert
        assertFalse(result);
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).delete(testUser);
    }
}