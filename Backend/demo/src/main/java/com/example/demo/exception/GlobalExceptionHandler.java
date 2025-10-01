package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed for one or more fields",
            errors,
            LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            "RESOURCE_NOT_FOUND",
            ex.getMessage(),
            null,
            LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            "DUPLICATE_RESOURCE",
            ex.getMessage(),
            null,
            LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(InvalidOperationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_OPERATION",
            ex.getMessage(),
            null,
            LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred: " + ex.getMessage(),
            null,
            LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Inner class for error response structure
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private Map<String, String> details;
        private LocalDateTime timestamp;

        public ErrorResponse(String errorCode, String message, Map<String, String> details, LocalDateTime timestamp) {
            this.errorCode = errorCode;
            this.message = message;
            this.details = details;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Map<String, String> getDetails() { return details; }
        public void setDetails(Map<String, String> details) { this.details = details; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}