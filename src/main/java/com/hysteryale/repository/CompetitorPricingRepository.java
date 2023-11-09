package com.hysteryale.repository;

import com.hysteryale.model.competitor.CompetitorPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetitorPricingRepository extends JpaRepository<CompetitorPricing, Integer> {

    @Query("SELECT c.country, c.clazz, c.category, c.series FROM CompetitorPricing c GROUP BY c.country, c.clazz, c.category, c.series")
    List<String[]> getCompetitorGroup();

    @Query("SELECT c FROM CompetitorPricing c WHERE c.country = ?1 AND c.clazz = ?2 AND c.category = ?3 AND c.series = ?4")
    List<CompetitorPricing> getListOfCompetitorInGroup(String country, String clazz, String category, String series);

    public List<CompetitorPricing> findCompetitorByFilter(String[] region, String[] dealerName, String[] plant, String[] metaSeries, String[] classes, String[] model, String AOPMarginPercentageGroup, Boolean chineseBrand );


}
