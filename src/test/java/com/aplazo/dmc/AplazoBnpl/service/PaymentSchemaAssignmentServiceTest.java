package com.aplazo.dmc.AplazoBnpl.service;

import com.aplazo.dmc.AplazoBnpl.model.Customer;
import com.aplazo.dmc.AplazoBnpl.model.PaymentSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentSchemaAssignmentServiceTest {

    private PaymentSchemaAssignmentService service;

    @BeforeEach
    void setUp() {
        service = new PaymentSchemaAssignmentService();
    }

    @Test
    void assignSchema_NameStartsWithStrategy_ShouldReturnFirstSchema() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setFirstName("Carlos");

        PaymentSchema schema = service.assignSchema(customer);

        assertNotNull(schema);
        assertEquals(1, schema.id());
    }

    @Test
    void assignSchema_DefaultStrategy_ShouldReturnSecondSchema() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setFirstName("Zoe");

        PaymentSchema schema = service.assignSchema(customer);

        assertNotNull(schema);
        assertEquals(2, schema.id());
    }

}
