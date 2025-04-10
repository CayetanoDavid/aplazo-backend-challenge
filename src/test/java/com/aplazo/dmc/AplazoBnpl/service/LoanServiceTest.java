package com.aplazo.dmc.AplazoBnpl.service;

import com.aplazo.dmc.AplazoBnpl.controller.dto.request.LoanRequest;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.LoanResponse;
import com.aplazo.dmc.AplazoBnpl.exception.ResourceNotFoundException;
import com.aplazo.dmc.AplazoBnpl.model.CreditLine;
import com.aplazo.dmc.AplazoBnpl.model.Customer;
import com.aplazo.dmc.AplazoBnpl.model.Loan;
import com.aplazo.dmc.AplazoBnpl.model.PaymentSchema;
import com.aplazo.dmc.AplazoBnpl.repository.CustomerRepository;
import com.aplazo.dmc.AplazoBnpl.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static com.aplazo.dmc.AplazoBnpl.model.LoanStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentSchemaAssignmentService paymentSchemaAssignmentService;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Customer customer;
    private CreditLine creditLine;
    private LoanRequest loanRequest;
    private PaymentSchema paymentSchema;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        creditLine = new CreditLine();
        creditLine.setCreditLineAmount(5000.0);
        creditLine.setAvailableCreditLineAmount(5000.0);

        customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setCreditLine(creditLine);

        loanRequest = new LoanRequest();
        loanRequest.setCustomerId(customer.getCustomerId());
        loanRequest.setAmount(1000.0);

        paymentSchema = new PaymentSchema(1, 5, 14, 13.0);
    }
    @Test
    void testCreateLoan_Success() {

        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(paymentSchemaAssignmentService.assignSchema(customer)).thenReturn(paymentSchema);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse response = loanService.createLoan(loanRequest);

        assertNotNull(response);
        assertEquals(customer.getCustomerId(), response.getCustomerId());
        assertEquals(ACTIVE, response.getStatus());
        assertEquals(5, response.getPaymentPlan().getInstallments().size());

        double expectedAvailable = 5000.0 - (1000.0 + 130.0);
        assertEquals(expectedAvailable, customer.getCreditLine().getAvailableCreditLineAmount());
    }

    @Test
    void testCreateLoan_InsufficientCredit() {
        loanRequest.setAmount(6000.0);
        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));

        Exception ex = assertThrows(IllegalStateException.class,
                () -> loanService.createLoan(loanRequest));

        assertEquals("Insufficient credit line for requested amount", ex.getMessage());
    }

    @Test
    void testCreateLoan_CustomerNotFound() {
        UUID fakeId = UUID.randomUUID();
        loanRequest.setCustomerId(fakeId);
        when(customerRepository.findById(fakeId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class,
                () -> loanService.createLoan(loanRequest));

        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void testGetLoanById_LoanExists() {
        UUID loanId = UUID.randomUUID();
        Loan loan = new Loan();
        loan.setLoanId(loanId);
        loan.setCustomer(customer);
        loan.setStatus(ACTIVE);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setCommissionAmount(100.0);
        loan.setInstallments(new ArrayList<>());

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        LoanResponse response = loanService.getLoanById(loanId);

        assertNotNull(response);
        assertEquals(loanId, response.getId());
    }

    @Test
    void testGetLoanById_NotFound() {
        UUID loanId = UUID.randomUUID();
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.getLoanById(loanId));
    }
}
