package com.example.demo.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestWithErrorDetails() {
        // Arrange
        FieldError fieldError = new FieldError("user", "email", "Email is required");
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleValidationException(validationException);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals("Validation failed for one or more fields", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("email"));
        assertEquals("Email is required", response.getBody().getDetails().get("email"));
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundWithMessage() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("User not found");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
        assertEquals("User not found", response.getBody().getMessage());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void handleDuplicateResourceException_ShouldReturnConflictWithMessage() {
        // Arrange
        DuplicateResourceException exception = new DuplicateResourceException("User already exists");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleDuplicateResourceException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DUPLICATE_RESOURCE", response.getBody().getErrorCode());
        assertEquals("User already exists", response.getBody().getMessage());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void handleInvalidOperationException_ShouldReturnBadRequestWithMessage() {
        // Arrange
        InvalidOperationException exception = new InvalidOperationException("Invalid operation");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleInvalidOperationException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_OPERATION", response.getBody().getErrorCode());
        assertEquals("Invalid operation", response.getBody().getMessage());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerErrorWithMessage() {
        // Arrange
        Exception exception = new Exception("Something went wrong");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleGlobalException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("Something went wrong"));
        assertNull(response.getBody().getDetails());
    }

    @Test
    void errorResponse_ShouldHaveCorrectProperties() {
        // Arrange
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        
        // Act
        GlobalExceptionHandler.ErrorResponse errorResponse = 
                new GlobalExceptionHandler.ErrorResponse(errorCode, message, null, java.time.LocalDateTime.now());

        // Assert
        assertEquals(errorCode, errorResponse.getErrorCode());
        assertEquals(message, errorResponse.getMessage());
        assertNull(errorResponse.getDetails());
        assertNotNull(errorResponse.getTimestamp());
    }
}