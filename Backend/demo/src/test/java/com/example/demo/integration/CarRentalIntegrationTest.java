package com.example.demo.integration;

import com.example.demo.model.Car;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class CarRentalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    private User testUser;
    private Car testCar;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        carRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.addRole(Role.ROLE_USER);

        // Create test car
        testCar = new Car();
        testCar.setMake("Toyota");
        testCar.setModel("Camry");
        testCar.setYear(2022);
        testCar.setCarType("Sedan");
        testCar.setTransmission("Automatic");
        testCar.setFuelType("Petrol");
        testCar.setSeatingCapacity(5);
        testCar.setPricePerDay(new BigDecimal("50.00"));
        testCar.setColor("Black");
        testCar.setLicensePlate("ABC123");
        testCar.setMileage(15);
        testCar.setAvailable(true);
    }

    @Test
    void signup_ShouldCreateNewUser_WhenValidDataProvided() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist()); // Password should not be returned
    }

    @Test
    void signup_ShouldReturnValidationError_WhenInvalidDataProvided() throws Exception {
        // Arrange
        User invalidUser = new User();
        invalidUser.setUsername("ab"); // Too short
        invalidUser.setEmail("invalid-email"); // Invalid email format
        invalidUser.setPassword("123"); // Too short

        // Act & Assert
        mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void getAvailableCars_ShouldReturnPublicCarList_WithoutAuthentication() throws Exception {
        // Arrange
        carRepository.save(testCar);

        // Act & Assert
        mockMvc.perform(get("/api/cars/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Camry"));
    }

    @Test
    void searchCars_ShouldReturnFilteredResults_WithoutAuthentication() throws Exception {
        // Arrange
        carRepository.save(testCar);

        // Act & Assert
        mockMvc.perform(get("/api/cars/search")
                .param("keyword", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].make").value("Toyota"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserBookings_ShouldRequireUserRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/forms/user/test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addCar_ShouldRequireAdminOrManagerRole_AndValidateInput() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/cars")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addCar_ShouldReturnForbidden_WhenUserLacksPermission() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/cars")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCar)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addCar_ShouldReturnValidationError_WhenInvalidCarData() throws Exception {
        // Arrange
        Car invalidCar = new Car();
        invalidCar.setMake(""); // Empty make
        invalidCar.setYear(1800); // Invalid year
        invalidCar.setSeatingCapacity(20); // Too many seats

        // Act & Assert
        mockMvc.perform(post("/api/cars")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCar)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateCar_ShouldAllowManagerRole() throws Exception {
        // Arrange
        Car savedCar = carRepository.save(testCar);
        savedCar.setMake("Honda");

        // Act & Assert
        mockMvc.perform(put("/api/cars/" + savedCar.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedCar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Honda"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteCar_ShouldReturnForbidden_WhenManagerTriesToDelete() throws Exception {
        // Arrange
        Car savedCar = carRepository.save(testCar);

        // Act & Assert
        mockMvc.perform(delete("/api/cars/" + savedCar.getId())
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCar_ShouldSucceed_WhenAdminDeletesCar() throws Exception {
        // Arrange
        Car savedCar = carRepository.save(testCar);

        // Act & Assert
        mockMvc.perform(delete("/api/cars/" + savedCar.getId())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void bookCar_ShouldBeAccessible_WithoutAuthentication() throws Exception {
        // Arrange
        Car savedCar = carRepository.save(testCar);

        // Act & Assert
        mockMvc.perform(post("/api/cars/" + savedCar.getId() + "/book")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCarStatistics_ShouldRequireAdminOrManagerRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/cars/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCars").exists())
                .andExpect(jsonPath("$.availableCars").exists());
    }

    @Test
    void unauthorizedAccess_ShouldReturnUnauthorized_WhenAccessingProtectedEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/cars/all"))
                .andExpect(status().isUnauthorized());
    }
}