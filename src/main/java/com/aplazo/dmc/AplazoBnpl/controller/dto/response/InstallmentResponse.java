package com.aplazo.dmc.AplazoBnpl.controller.dto.response;

import com.aplazo.dmc.AplazoBnpl.model.InstallmentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InstallmentResponse {

    private double amount;
    private LocalDate scheduledPaymentDate;
    private InstallmentStatus status;

}
