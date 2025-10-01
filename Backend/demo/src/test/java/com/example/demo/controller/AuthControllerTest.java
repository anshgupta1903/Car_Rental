package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser
    void signup_ShouldReturnCreatedUser_WhenValidUserProvided() throws Exception {
        // Arrange
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void signup_ShouldReturnBadRequest_WhenInvalidUserProvided() throws Exception {
        // Arrange
        User invalidUser = new User();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void login_ShouldReturnUser_WhenValidCredentialsProvided() throws Exception {
        // Arrange
        when(userService.login(anyString(), anyString())).thenReturn(Optional.of(testUser));

        User loginRequest = new User();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void login_ShouldReturnUnauthorized_WhenInvalidCredentialsProvided() throws Exception {
        // Arrange
        when(userService.login(anyString(), anyString())).thenReturn(Optional.empty());

        User loginRequest = new User();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Credentials"));
    }

    @Test
    @WithMockUser
    void login_ShouldReturnBadRequest_WhenInvalidLoginRequestProvided() throws Exception {
        // Arrange
        User invalidLoginRequest = new User();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());
    }
}