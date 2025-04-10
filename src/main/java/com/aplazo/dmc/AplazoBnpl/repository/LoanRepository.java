package com.aplazo.dmc.AplazoBnpl.repository;

import com.aplazo.dmc.AplazoBnpl.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

}
