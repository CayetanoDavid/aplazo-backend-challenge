package com.aplazo.dmc.AplazoBnpl.service;

import com.aplazo.dmc.AplazoBnpl.controller.dto.request.CustomerRequest;
import com.aplazo.dmc.AplazoBnpl.controller.dto.response.CustomerResponse;
import com.aplazo.dmc.AplazoBnpl.exception.ResourceNotFoundException;
import com.aplazo.dmc.AplazoBnpl.model.CreditLine;
import com.aplazo.dmc.AplazoBnpl.model.Customer;
import com.aplazo.dmc.AplazoBnpl.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CreditLineCalculatorService creditLineCalculatorService;

    public CustomerService(CustomerRepository customerRepository,
                           CreditLineCalculatorService creditLineCalculatorService) {

        this.customerRepository = customerRepository;
        this.creditLineCalculatorService = creditLineCalculatorService;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {

        LocalDate birthDate = parseDateOfBirth(request.getDateOfBirth());
        int customerAge = calculateAge(birthDate);

        double creditLineAmount = creditLineCalculatorService.calculateCreditLineAmount(customerAge);
        validateCreditLineAmount(creditLineAmount);

        Customer customer = buildCustomer(request);
        CreditLine creditLine = buildCreditLine(customer, creditLineAmount);
        customer.setCreditLine(creditLine);

        Customer savedCustomer = customerRepository.save(customer);

        return buildCustomerResponse(savedCustomer);

    }
    public CustomerResponse getCustomerById(UUID id) {

        return customerRepository.findById(id)
                .map(this::buildCustomerResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private LocalDate parseDateOfBirth(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dob, formatter);
    }

    private int calculateAge(LocalDate birthDate) {

        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private void validateCreditLineAmount(double amount) {
        if (Double.compare(amount, 0.0) <= 0) {
            throw new IllegalArgumentException("Customer age is not valid for a credit-line.");
        }
    }

    private Customer buildCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setSecondLastName(request.getSecondLastName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setCreatedAt(LocalDateTime.now());

        return customer;
    }

    private CreditLine buildCreditLine(Customer customer, double amount) {

        CreditLine creditLine = new CreditLine();
        creditLine.setCreditLineId(UUID.randomUUID());
        creditLine.setCustomer(customer);
        creditLine.setCreditLineAmount(amount);
        creditLine.setAvailableCreditLineAmount(amount);

        return creditLine;
    }

    private CustomerResponse buildCustomerResponse(Customer customer) {

        CreditLine creditLine = customer.getCreditLine();
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getCustomerId());
        response.setCreatedAt(customer.getCreatedAt());
        response.setCreditLineAmount(creditLine.getCreditLineAmount());
        response.setAvailableCreditLineAmount(creditLine.getAvailableCreditLineAmount());

        return response;
    }
}
