package com.aplazo.dmc.AplazoBnpl.service.schemaAssignment;

import com.aplazo.dmc.AplazoBnpl.model.Customer;
import com.aplazo.dmc.AplazoBnpl.model.PaymentSchema;

public class DefaultSchemaStrategy implements SchemaAssignmentStrategy {

    private final PaymentSchema schema;

    public DefaultSchemaStrategy(PaymentSchema schema) {
        this.schema = schema;
    }

    @Override
    public boolean matches(Customer customer) {
        return true;
    }

    @Override
    public PaymentSchema getSchema() {
        return schema;
    }
}
