package com.app.exception.handler;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.app.exception.ResourceNotFoundException;
import com.app.payload.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	 @ExceptionHandler(ResourceNotFoundException.class)
	    public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException ex) {
	        ApiResponse resp = new ApiResponse(false, ex.getMessage(), null);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
	    }

	    @ExceptionHandler(DataIntegrityViolationException.class)
	    public ResponseEntity<ApiResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
	        ApiResponse resp = new ApiResponse(false, "Database error: " + ex.getMostSpecificCause().getMessage(), null);
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
	    }

	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {
	        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
	            .map(err -> (FieldError) err)
	            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a + "; " + b));

	        ApiResponse resp = new ApiResponse(false, "Validation failed", errors);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {
	        ApiResponse resp = new ApiResponse(false, "Server error: " + ex.getMessage(), null);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
	    }
}