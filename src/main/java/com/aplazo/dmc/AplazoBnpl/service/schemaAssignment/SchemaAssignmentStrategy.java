package com.aplazo.dmc.AplazoBnpl.service.schemaAssignment;

import com.aplazo.dmc.AplazoBnpl.model.Customer;
import com.aplazo.dmc.AplazoBnpl.model.PaymentSchema;

public interface SchemaAssignmentStrategy {

    boolean matches(Customer customer);
    PaymentSchema getSchema();
}
