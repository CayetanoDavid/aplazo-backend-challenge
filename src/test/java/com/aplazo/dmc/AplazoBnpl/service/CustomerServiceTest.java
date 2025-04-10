package com.aplazo.dmc.AplazoBnpl.service;

import com.aplazo.dmc.AplazoBnpl.controller.dto.request.CustomerRequest;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.CustomerResponse;
import com.aplazo.dmc.AplazoBnpl.exception.ResourceNotFoundException;
import com.aplazo.dmc.AplazoBnpl.model.CreditLine;
import com.aplazo.dmc.AplazoBnpl.model.Customer;
import com.aplazo.dmc.AplazoBnpl.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

public class CustomerServiceTest {

    private static final UUID ID = UUID.fromString("e974a8a1-14a0-496d-b5b6-0a7a4be93cba");

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CreditLineCalculatorService creditLineCalculatorService;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequest customerRequest;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        customerRequest = new CustomerRequest();
        customerRequest.setFirstName("Juan");
        customerRequest.setLastName("López");
        customerRequest.setSecondLastName("Pérez");
        customerRequest.setDateOfBirth("2000-09-16");
    }

    @Test
    void testCreateCustomer_Success() {

        double creditLineAmount = 5000.0;
        Mockito.when(creditLineCalculatorService.calculateCreditLineAmount(anyInt()))
                .thenReturn(creditLineAmount);


        Customer mockCustomer = new Customer();
        mockCustomer.setCustomerId(UUID.randomUUID());
        mockCustomer.setFirstName("Juan");
        mockCustomer.setLastName("López");
        mockCustomer.setSecondLastName("Pérez");
        mockCustomer.setDateOfBirth("2000-09-16");

        CreditLine mockCreditLine = new CreditLine();
        mockCreditLine.setCreditLineAmount(creditLineAmount);
        mockCustomer.setCreditLine(mockCreditLine);

        Mockito.when(customerRepository.save(any(Customer.class))).thenReturn(mockCustomer);

        CustomerResponse response = customerService.createCustomer(customerRequest);

        assertNotNull(response);
        assertEquals(mockCustomer.getCustomerId(), response.getId());
        assertEquals(creditLineAmount, response.getCreditLineAmount());
    }

    @Test
    void testGetCustomerById_CustomerExists() {

        UUID customerId = ID;
        Customer mockCustomer = new Customer();
        mockCustomer.setCustomerId(ID);
        mockCustomer.setFirstName("Jorge");
        mockCustomer.setLastName("Mena");

        CreditLine mockCreditLine = new CreditLine();
        mockCreditLine.setCreditLineAmount(5000.0);
        mockCustomer.setCreditLine(mockCreditLine);

        Mockito.when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(mockCustomer));

        CustomerResponse response = customerService.getCustomerById(customerId);

        assertNotNull(response);
        assertEquals(customerId, response.getId());
    }

    @Test
    void testGetCustomerById_CustomerNotFound() {

        UUID customerId = UUID.randomUUID();
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(customerId));
    }
}
