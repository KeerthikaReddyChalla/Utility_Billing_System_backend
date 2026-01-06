package com.chubb.meter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateReadingException.class)
	public ResponseEntity<Map<String, Object>> duplicate(DuplicateReadingException ex) {
	    return ResponseEntity
	            .badRequest()
	            .body(error(ex.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) {
	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(error(ex.getMessage()));
	}


    private Map<String, Object> error(String message) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "error", message
        );
    }
    @ExceptionHandler(InvalidConnectionStateException.class)
    public ResponseEntity<String> handleInvalidConnection(
            InvalidConnectionStateException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
