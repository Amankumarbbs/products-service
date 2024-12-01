package com.products.service.catalog.api.exceptionHandler;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        Map<String, String> errorResponse = new HashMap<>();
        Throwable cause = ex.getCause();
        if (cause instanceof final UnrecognizedPropertyException unrecognizedPropertyException) {
            String errorMessage = "JSON parse error: Unrecognized field '" + unrecognizedPropertyException.getPropertyName() + "' in request body.";
            logger.error("UnrecognizedPropertyException: {}", errorMessage, ex);
            errorResponse.put("error", errorMessage);
        } else {
            logger.error("HttpMessageNotReadableException: {}", ex.getMessage(), ex);
            errorResponse.put("error", "Malformed JSON request");
        }
        logger.error("HttpMessageNotReadableException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String,String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = "HTTP method not supported: " + ex.getMethod();
        logger.error("HttpRequestMethodNotSupportedException: {}", errorMessage, ex);
        errorResponse.put("error", errorMessage);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                             .body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleBindException(BindException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        ex.getFieldErrors().forEach(error -> errorResponse.put(error.getField(), error.getDefaultMessage()));
        logger.error("BindException: {}", errorResponse, ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", validationErrors);

        logger.error("Validation errors: {}", validationErrors, ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = ex.getMessage();
        logger.error("EntityNotFoundException: {}", errorMessage, ex);
        errorResponse.put("error", errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = ex.getMessage();
        logger.error("IllegalArgumentException: {}", errorMessage, ex);
        errorResponse.put("error", errorMessage);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = "An unexpected error occurred. Please try again later.";
        logger.error("RuntimeException: {}", errorMessage, ex);
        errorResponse.put("error", errorMessage);
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleProductAlreadyExistsException(ProductAlreadyExistsException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error",  ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
