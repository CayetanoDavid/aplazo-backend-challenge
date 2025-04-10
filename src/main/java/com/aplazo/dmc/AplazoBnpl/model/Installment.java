package com.aplazo.dmc.AplazoBnpl.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
public class Installment {

    @Id
    private UUID installmentId;

    @ManyToOne
    @JoinColumn(name = "loanId")
    private Loan loan;

    private double amount;

    private LocalDate scheduledPaymentDate;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status;
}
