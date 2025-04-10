package com.aplazo.dmc.AplazoBnpl.controller;

import com.aplazo.dmc.AplazoBnpl.controller.dto.request.CustomerRequest;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.CustomerResponse;
import com.aplazo.dmc.AplazoBnpl.security.AplazoUserDetailsService;
import com.aplazo.dmc.AplazoBnpl.security.JwtUtil;
import com.aplazo.dmc.AplazoBnpl.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    private static final UUID ID = UUID.fromString("e974a8a1-14a0-496d-b5b6-0a7a4be93cba");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AplazoUserDetailsService aplazoUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private CustomerRequest mockRequest;
    private CustomerResponse mockResponse;

    @BeforeEach
    void setUp() {

        mockRequest = new CustomerRequest();
        mockRequest.setFirstName("Juan");
        mockRequest.setLastName("López");
        mockRequest.setSecondLastName("Pérez");
        mockRequest.setDateOfBirth("2000-09-16");

        mockResponse = new CustomerResponse();
        mockResponse.setId(ID);
        mockResponse.setCreatedAt(LocalDateTime.of(2025, 4, 9, 18,0));
        mockResponse.setCreditLineAmount(3000.0);
        mockResponse.setAvailableCreditLineAmount(3000.0);
    }

    @Test
    void testCreateCustomer() throws Exception {
        Mockito.when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetCustomerById() throws Exception {
        UUID customerId = ID;
        Mockito.when(customerService.getCustomerById(eq(customerId)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/v1/customers/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
        @Bean
        public CustomerService customerService() {
            return Mockito.mock(CustomerService.class);
        }

        @Bean
        public AplazoUserDetailsService aplazoUserDetailsService() {
            return Mockito.mock(AplazoUserDetailsService.class);
        }

        @Bean
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
    }
}
