package com.hysteryale.repository;

import com.hysteryale.model.CompetitorPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetitorPricingRepository extends JpaRepository<CompetitorPricing, Integer> {

    public List<CompetitorPricing> findCompetitorByFilter(String region)

}
