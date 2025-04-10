package com.aplazo.dmc.AplazoBnpl.repository;

import com.aplazo.dmc.AplazoBnpl.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

}
