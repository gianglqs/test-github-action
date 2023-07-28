package com.hysteryale.repository;

import com.hysteryale.model.AUDPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AUDPriceRepository extends JpaRepository<AUDPrice, Integer> {
}
