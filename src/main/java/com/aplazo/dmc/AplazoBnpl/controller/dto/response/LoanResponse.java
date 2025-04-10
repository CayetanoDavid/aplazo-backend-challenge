package com.aplazo.dmc.AplazoBnpl.controller.dto.response;

import com.aplazo.dmc.AplazoBnpl.model.LoanStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class LoanResponse {

    private UUID id;
    private UUID customerId;
    private LoanStatus status;
    private LocalDateTime createdAt;
    private PaymentPlan paymentPlan;

    @Data
    public static class PaymentPlan {
        private double commissionAmount;
        private List<InstallmentResponse> installments;
    }

}