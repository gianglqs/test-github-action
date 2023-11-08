package com.hysteryale.repository;

import com.hysteryale.model.competitor.CompetitorPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetitorPricingRepository extends JpaRepository<CompetitorPricing, Integer> {

    @Query("SELECT c.country, c.clazz, c.category, c.series FROM CompetitorPricing c GROUP BY c.country, c.clazz, c.category, c.series")
    List<CompetitorPricing> getCompetitorGroup();

}
