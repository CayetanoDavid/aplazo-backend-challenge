package com.aplazo.dmc.AplazoBnpl.service;

import com.aplazo.dmc.AplazoBnpl.controller.dto.request.LoanRequest;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.InstallmentResponse;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.LoanResponse;
import com.aplazo.dmc.AplazoBnpl.exception.ResourceNotFoundException;
import com.aplazo.dmc.AplazoBnpl.model.*;
import com.aplazo.dmc.AplazoBnpl.repository.CustomerRepository;
import com.aplazo.dmc.AplazoBnpl.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.aplazo.dmc.AplazoBnpl.model.InstallmentStatus.NEXT;
import static com.aplazo.dmc.AplazoBnpl.model.LoanStatus.ACTIVE;

@Service
public class LoanService {

    private static final int HUNDRED_PERCENTAGE = 100;

    private final CustomerRepository customerRepository;
    private final PaymentSchemaAssignmentService paymentSchemaAssignmentService;
    private final LoanRepository loanRepository;


    public LoanService(CustomerRepository customerRepository,
                       PaymentSchemaAssignmentService paymentSchemaAssignmentService,
                       LoanRepository loanRepository) {
        this.customerRepository = customerRepository;
        this.paymentSchemaAssignmentService = paymentSchemaAssignmentService;
        this.loanRepository = loanRepository;
    }

    public LoanResponse createLoan(LoanRequest request) {

        Customer customer = fetchCustomer(request.getCustomerId());
        validateCreditLine(customer, request.getAmount());
        PaymentSchema schema = paymentSchemaAssignmentService.assignSchema(customer);

        double commissionAmount = calculateCommission(request.getAmount(), schema.interestRate());
        double totalPurchasedAmount = request.getAmount() + commissionAmount;
        double installmentAmount = totalPurchasedAmount / schema.payments();

        Loan loan = createLoanEntity(customer, schema, commissionAmount);
        List<Installment> installments = generateInstallments(loan, schema, installmentAmount);

        loan.setInstallments(installments);
        deductCreditLine(customer, totalPurchasedAmount);

        Loan savedLoan = loanRepository.save(loan);

        return mapToLoanResponse(savedLoan);

    }
    public LoanResponse getLoanById(UUID id) {

        return loanRepository.findById(id)
                .map(this::mapToLoanResponse)
                .orElseThrow(() -> new ResourceNotFoundException("loan not found"));
    }

    private LoanResponse mapToLoanResponse(Loan loan) {

        LoanResponse loanResponse = new LoanResponse();
        loanResponse.setId(loan.getLoanId());
        loanResponse.setCustomerId(loan.getCustomer().getCustomerId());
        loanResponse.setStatus(loan.getStatus());
        loanResponse.setCreatedAt(loan.getCreatedAt());

        LoanResponse.PaymentPlan paymentPlan = new LoanResponse.PaymentPlan();
        paymentPlan.setCommissionAmount(loan.getCommissionAmount());

        List<InstallmentResponse> installmentResponseList = new ArrayList<>();
        for (Installment installment: loan.getInstallments()) {
            InstallmentResponse installmentResponse = new InstallmentResponse();
            installmentResponse.setAmount(installment.getAmount());
            installmentResponse.setScheduledPaymentDate(installment.getScheduledPaymentDate());
            installmentResponse.setStatus(installment.getStatus());
            installmentResponseList.add(installmentResponse);
        }

        paymentPlan.setInstallments(installmentResponseList);
        loanResponse.setPaymentPlan(paymentPlan);

        return loanResponse;
    }

    private Customer fetchCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private void validateCreditLine(Customer customer, double requestedAmount) {
        double available = customer.getCreditLine().getAvailableCreditLineAmount();
        if (requestedAmount > available) {
            throw new IllegalStateException("Insufficient credit line for requested amount");
        }
    }

    private double calculateCommission(double amount, double interestRate) {
        return amount * (interestRate / HUNDRED_PERCENTAGE);
    }

    private Loan createLoanEntity(Customer customer, PaymentSchema schema, double commissionAmount) {
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setCustomer(customer);
        loan.setStatus(ACTIVE);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setPaymentPlan(schema.id());
        loan.setCommissionAmount(commissionAmount);
        return loan;
    }

    private List<Installment> generateInstallments(Loan loan, PaymentSchema schema, double amountPerInstallment) {
        List<Installment> installments = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < schema.payments(); i++) {
            Installment installment = new Installment();
            installment.setInstallmentId(UUID.randomUUID());
            installment.setLoan(loan);
            installment.setAmount(amountPerInstallment);
            installment.setScheduledPaymentDate(today.plusDays((long) i * schema.frequency()));
            installment.setStatus(NEXT);
            installments.add(installment);
        }

        return installments;
    }

    private void deductCreditLine(Customer customer, double amount) {
        CreditLine creditLine = customer.getCreditLine();
        creditLine.setAvailableCreditLineAmount(creditLine.getAvailableCreditLineAmount() - amount);
    }

}
