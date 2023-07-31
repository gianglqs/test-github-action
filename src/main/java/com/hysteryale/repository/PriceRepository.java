package com.hysteryale.repository;

import com.hysteryale.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Integer> {
    @Query("SELECT p FROM Price p WHERE p.series= ?1")
    public List<Price> getPricesListBySeries(String seriesNum);
}
