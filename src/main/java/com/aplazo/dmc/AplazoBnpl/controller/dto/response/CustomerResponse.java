package com.aplazo.dmc.AplazoBnpl.controller.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CustomerResponse {

    private UUID id;
    private LocalDateTime createdAt;
    private double creditLineAmount;
    private double availableCreditLineAmount;

}
