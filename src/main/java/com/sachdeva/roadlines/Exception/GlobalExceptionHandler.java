package com.sachdeva.roadlines.Exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// Handle IllegalArgumentException (like duplicate checks)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException iae) {
		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put("timestamp", LocalDateTime.now());
		errorBody.put("status", HttpStatus.CONFLICT.value());
		errorBody.put("error", "Conflict");
		errorBody.put("message", iae.getMessage());

		return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
	}

	// Handle validation errors (@Valid)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException manve) {
		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put("timestamp", LocalDateTime.now());
		errorBody.put("status", HttpStatus.BAD_REQUEST.value());
		errorBody.put("error", "Bad Request");

		Map<String, String> fieldErrors = new HashMap<>();
		manve.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			fieldErrors.put(fieldName, message);
		});
		errorBody.put("errors", fieldErrors);

		return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put("timestamp", LocalDateTime.now());
		errorBody.put("status", ex.getStatusCode().value());
		errorBody.put("error", ex.getStatusCode());
		errorBody.put("message", ex.getReason());
		return new ResponseEntity<>(errorBody, ex.getStatusCode());
	}

	// Handle all other exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handlegenericEexception(Exception ex) {
		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put("timestamp", LocalDateTime.now());
		errorBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorBody.put("error", "Internal server Error");
		errorBody.put("message", ex.getMessage());
		return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
