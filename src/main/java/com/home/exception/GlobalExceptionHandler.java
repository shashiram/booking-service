package com.home.exception;

import com.home.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null
        );

        return new ResponseEntity<>(errorResponse,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle validation exceptions
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(
            ValidationException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
