package com.hysteryale.repository;

import com.hysteryale.model.SAPCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SAPCustomerRepository extends JpaRepository<SAPCustomer, String> {
}
