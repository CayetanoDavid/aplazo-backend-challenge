package com.aplazo.dmc.AplazoBnpl.service;

import com.aplazo.dmc.AplazoBnpl.model.Customer;
import com.aplazo.dmc.AplazoBnpl.model.PaymentSchema;
import com.aplazo.dmc.AplazoBnpl.service.schemaAssignment.DefaultSchemaStrategy;
import com.aplazo.dmc.AplazoBnpl.service.schemaAssignment.NameStartsWithStrategy;
import com.aplazo.dmc.AplazoBnpl.service.schemaAssignment.SchemaAssignmentStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentSchemaAssignmentService {

    private final List<PaymentSchema> schemas;
    private final List<SchemaAssignmentStrategy> strategies;

    public PaymentSchemaAssignmentService() {
        this.schemas = initializeSchemas();
        this.strategies = initializeStrategies();
    }

    public PaymentSchema assignSchema(Customer customer) {
        for (SchemaAssignmentStrategy strategy : strategies) {
            if (strategy.matches(customer)) {
                return strategy.getSchema();
            }
        }

        throw new IllegalStateException("No id matched the given customer.");
    }

    private List<PaymentSchema> initializeSchemas() {
        return List.of(
                new PaymentSchema(1, 5, 14, 13),
                new PaymentSchema(2, 5, 14, 16));
    }

    private List<SchemaAssignmentStrategy> initializeStrategies() {
        return List.of(
                new NameStartsWithStrategy(List.of('C', 'L', 'H'), schemas.get(0)),
                new DefaultSchemaStrategy(schemas.get(1))
        );
    }

}
