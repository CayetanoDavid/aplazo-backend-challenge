package com.aplazo.dmc.AplazoBnpl.controller.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class LoanRequest {

    @NotNull
    private UUID customerId;

    @DecimalMin(value = "0.0", inclusive = false)
    private double amount;

}
