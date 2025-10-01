package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.SignupRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class UserOnlySignupTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void signup_ShouldCreateUserRole_RegardlessOfInputRole() throws Exception {
        // Arrange
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("testuser");
        expectedUser.setEmail("test@example.com");
        expectedUser.setPassword("password123");
        expectedUser.addRole(Role.ROLE_USER);

        when(userService.saveUser(any(User.class))).thenReturn(expectedUser);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void adminSignup_ShouldReturn403Forbidden() throws Exception {
        // Arrange
        String adminRequest = "{"
                + "\"username\":\"admin\","
                + "\"email\":\"admin@test.com\","
                + "\"password\":\"password123\""
                + "}";

        // Act & Assert
        mockMvc.perform(post("/auth/signup/admin")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(adminRequest))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Admin signup is disabled. Contact system administrator."));
    }

    @Test
    @WithMockUser
    void managerSignup_ShouldReturn403Forbidden() throws Exception {
        // Arrange
        String managerRequest = "{"
                + "\"username\":\"manager\","
                + "\"email\":\"manager@test.com\","
                + "\"password\":\"password123\""
                + "}";

        // Act & Assert
        mockMvc.perform(post("/auth/signup/manager")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(managerRequest))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Manager signup is disabled. Contact system administrator."));
    }
}