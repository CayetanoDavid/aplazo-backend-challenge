package com.aplazo.dmc.AplazoBnpl.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;


import java.util.UUID;

@Data
@Entity
public class CreditLine {

    @Id
    private UUID creditLineId;

    @OneToOne
    @JoinColumn(name = "customerId", referencedColumnName = "customerId")
    private Customer customer;

    private double creditLineAmount;

    private double availableCreditLineAmount;
}
