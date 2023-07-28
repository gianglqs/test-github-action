package com.hysteryale.repository;

import com.hysteryale.model.USDPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface USDPriceRepository extends JpaRepository<USDPrice, Integer> {
}
