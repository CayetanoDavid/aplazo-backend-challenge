package com.aplazo.dmc.AplazoBnpl.exception;

import com.aplazo.dmc.AplazoBnpl.controller.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();

        httpServletRequest = mock(HttpServletRequest.class);
        webRequest = new ServletWebRequest(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleResourceNotFoundException_ReturnsNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Customer not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(ex, webRequest);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("APZ000005", response.getBody().getCode());
        assertEquals("ENTITY_NOT_FOUND", response.getBody().getError());
        assertEquals("Customer not found", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleIllegalArgumentException_ReturnsBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, webRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("APZ000002", response.getBody().getCode());
        assertEquals("INVALID_CUSTOMER_REQUEST", response.getBody().getError());
    }

    @Test
    void handleTooManyRequestsException_Returns429() {
        TooManyRequestsException ex = new TooManyRequestsException("Rate limit exceeded");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTooManyRequestsException(ex, webRequest);

        assertEquals(429, response.getStatusCodeValue());
        assertEquals("APZ000003", response.getBody().getCode());
        assertEquals("RATE_LIMIT_ERROR", response.getBody().getError());
    }

    @Test
    void handleGenericException_Returns500() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneric(ex, webRequest);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("APZ000001", response.getBody().getCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
    }
}
