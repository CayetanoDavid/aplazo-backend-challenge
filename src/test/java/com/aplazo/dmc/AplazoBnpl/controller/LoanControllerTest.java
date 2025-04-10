package com.aplazo.dmc.AplazoBnpl.controller;

import com.aplazo.dmc.AplazoBnpl.controller.dto.request.LoanRequest;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.InstallmentResponse;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.LoanResponse;
import com.aplazo.dmc.AplazoBnpl.security.AplazoUserDetailsService;
import com.aplazo.dmc.AplazoBnpl.security.JwtUtil;
import com.aplazo.dmc.AplazoBnpl.service.LoanService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.aplazo.dmc.AplazoBnpl.model.InstallmentStatus.NEXT;
import static com.aplazo.dmc.AplazoBnpl.model.LoanStatus.ACTIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
public class LoanControllerTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("c654f64d-b978-49a1-abef-73add51ecfec");
    private static final UUID LOAN_ID = UUID.fromString("ef910cb0-e822-416d-a3eb-b79a6193846f");
    private static final LocalDate TODAY = LocalDate.of(2025, 4, 9);


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoanService loanService;

    @Autowired
    private AplazoUserDetailsService aplazoUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LoanRequest mockRequest;
    private LoanResponse mockResponse;

    @BeforeEach
    void setUp() {

        mockRequest = new LoanRequest();
        mockRequest.setCustomerId(CUSTOMER_ID);
        mockRequest.setAmount(2_000.00);

        mockResponse = new LoanResponse();
        mockResponse.setId(LOAN_ID);
        mockResponse.setCustomerId(CUSTOMER_ID);
        mockResponse.setCreatedAt(LocalDateTime.of(2025, 4, 9, 18,0));
        mockResponse.setStatus(ACTIVE);

        LoanResponse.PaymentPlan paymentPlan = new LoanResponse.PaymentPlan();
        paymentPlan.setCommissionAmount(400.0);

        List<InstallmentResponse> installments = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            InstallmentResponse installmentResponse = new InstallmentResponse();
            installmentResponse.setStatus(NEXT);
            installmentResponse.setAmount(580.0);
            installmentResponse.setScheduledPaymentDate(TODAY.plusDays((long) i * 14));
            installments.add(installmentResponse);
        }

        paymentPlan.setInstallments(installments);
        mockResponse.setPaymentPlan(paymentPlan);

    }

    @Test
    void testCreateLoan() throws Exception {
        Mockito.when(loanService.createLoan(any(LoanRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetLoanById() throws Exception {
        UUID loanId = LOAN_ID;
        Mockito.when(loanService.getLoanById(eq(loanId)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/v1/loans/{loanId}", loanId))
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
        public LoanService loanService() {
            return Mockito.mock(LoanService.class);
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
