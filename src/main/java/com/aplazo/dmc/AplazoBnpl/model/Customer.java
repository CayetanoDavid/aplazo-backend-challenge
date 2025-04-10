package com.aplazo.dmc.AplazoBnpl.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Customer {

    @Id
    private UUID customerId;

    private String firstName;

    private String lastName;

    private String secondLastName;

    private String dateOfBirth;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private CreditLine creditLine;
}
