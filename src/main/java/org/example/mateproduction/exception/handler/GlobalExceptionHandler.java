package org.example.mateproduction.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.example.mateproduction.dto.response.ErrorResponse;
import org.example.mateproduction.exception.ReportAlreadyExistsException;
import org.example.mateproduction.exception.ResourceNotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handler for "You already reported this"
    @ExceptionHandler(ReportAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleReportAlreadyExists(
            ReportAlreadyExistsException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value()) // 409
                .error(HttpStatus.CONFLICT.getReasonPhrase()) // "Conflict"
                .message(ex.getMessage()) // "You have already reported this content."
                .path(request.getRequestURI())
                .build();
        System.out.println(errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value()) // 404
                .error(HttpStatus.NOT_FOUND.getReasonPhrase()) // "Not Found"
                .message(ex.getMessage()) // The message from the exception
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    // A fallback handler for any other unexpected exceptions
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .status(400)
                        .error("Bad Request")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .build()
        );
    }

}