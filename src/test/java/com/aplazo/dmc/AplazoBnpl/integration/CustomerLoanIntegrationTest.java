package com.aplazo.dmc.AplazoBnpl.integration;

import com.aplazo.dmc.AplazoBnpl.controller.dto.request.CustomerRequest;
import com.aplazo.dmc.AplazoBnpl.controller.dto.request.LoanRequest;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.CustomerResponse;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.LoanResponse;
import com.aplazo.dmc.AplazoBnpl.security.JwtUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static com.aplazo.dmc.AplazoBnpl.model.LoanStatus.ACTIVE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerLoanIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private static CustomerResponse createdCustomer;

    private static LoanResponse createdLoan;

    private static String jwtToken;

    @BeforeAll
    static void setupToken(@Autowired JwtUtil jwtUtil) {
        jwtToken = jwtUtil.generateToken("user");
    }

    @Test
    @Order(1)
    void testCreateCustomer() {

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setFirstName("Juan");
        customerRequest.setLastName("López");
        customerRequest.setSecondLastName("Pérez");
        customerRequest.setDateOfBirth("2000-09-16");

        HttpHeaders headers = addHttpHeaders();
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        CustomerResponse response = restTemplate.postForObject(
                "http://localhost:" + port + "/v1/customers",
                request,
                CustomerResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getCreditLineAmount()).isEqualTo(3_000.00);
        assertThat(response.getAvailableCreditLineAmount()).isEqualTo(3_000.00);

        createdCustomer = response;
    }

    @Test
    @Order(2)
    void testGetCustomerById() {

        assertThat(createdCustomer).isNotNull();

        UUID customerId = createdCustomer.getId();

        HttpHeaders headers = addHttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CustomerResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/v1/customers/" + customerId,
                HttpMethod.GET,
                entity,
                CustomerResponse.class);

        CustomerResponse customer = response.getBody();

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(customerId);
        assertThat(customer.getCreditLineAmount()).isEqualTo(3_000.00);
    }

    @Test
    @Order(3)
    void testCreateLoan() {

        assertThat(createdCustomer).isNotNull();

        UUID customerId = createdCustomer.getId();

        LoanRequest request = new LoanRequest();
        request.setCustomerId(customerId);
        request.setAmount(2000.8);

        HttpEntity<LoanRequest> entity = new HttpEntity<>(request, addHttpHeaders());

        ResponseEntity<LoanResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/v1/loans",
                HttpMethod.POST,
                entity,
                LoanResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        LoanResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getStatus()).isEqualTo(ACTIVE);
        assertThat(body.getPaymentPlan().getCommissionAmount()).isEqualTo(320.128);
        assertThat(body.getPaymentPlan().getInstallments().size()).isEqualTo(5);

        createdLoan = body;
    }

    @Test
    @Order(4)
    void testGetLoanById() {

        assertThat(createdLoan).isNotNull();
        UUID loanId = createdLoan.getId();

        HttpEntity<Void> entity = new HttpEntity<>(addHttpHeaders());

        ResponseEntity<LoanResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/v1/loans/" + loanId,
                HttpMethod.GET,
                entity,
                LoanResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoanResponse loan = response.getBody();
        assertThat(loan.getId()).isNotNull();
        assertThat(loan.getStatus()).isEqualTo(ACTIVE);
        assertThat(loan.getPaymentPlan().getCommissionAmount()).isEqualTo(320.128);
        assertThat(loan.getPaymentPlan().getInstallments().size()).isEqualTo(5);
    }

    @NotNull
    private HttpHeaders addHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        return headers;
    }

}
