package com.aplazo.dmc.AplazoBnpl.service.schemaAssignment;

import com.aplazo.dmc.AplazoBnpl.model.Customer;
import com.aplazo.dmc.AplazoBnpl.model.PaymentSchema;

import java.util.List;

public class NameStartsWithStrategy implements SchemaAssignmentStrategy {

    private final List<Character> initials;
    private final PaymentSchema schema;

    public NameStartsWithStrategy(List<Character> initials, PaymentSchema schema) {
        this.initials = initials;
        this.schema = schema;
    }

    @Override
    public boolean matches(Customer customer) {
        return initials.contains(Character.toUpperCase(customer.getFirstName().charAt(0)));
    }

    @Override
    public PaymentSchema getSchema() {
        return schema;
    }
}
