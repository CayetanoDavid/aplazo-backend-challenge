package com.aplazo.dmc.AplazoBnpl.exception;

import com.aplazo.dmc.AplazoBnpl.controller.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse();
        error.setCode("APZ000005");
        error.setError("ENTITY_NOT_FOUND");
        error.setMessage(ex.getMessage());
        error.setTimestamp(Instant.now().getEpochSecond());
        error.setPath(((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse();
        error.setCode("APZ000002");
        error.setError("INVALID_CUSTOMER_REQUEST");
        error.setMessage(ex.getMessage());
        error.setTimestamp(Instant.now().getEpochSecond());
        error.setPath(((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(
            TooManyRequestsException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse();
        error.setCode("APZ000003");
        error.setError("RATE_LIMIT_ERROR");
        error.setMessage(ex.getMessage());
        error.setTimestamp(Instant.now().getEpochSecond());
        error.setPath(((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse();
        error.setCode("APZ000001");
        error.setError("INTERNAL_SERVER_ERROR");
        error.setMessage(ex.getMessage());
        error.setTimestamp(Instant.now().getEpochSecond());
        error.setPath(((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
