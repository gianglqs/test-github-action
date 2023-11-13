package com.hysteryale.service;

import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.repository.CompetitorPricingRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class IndicatorsService {
    @Resource
    CompetitorPricingRepository competitorPricingRepository;

    public List<CompetitorPricing> getCompetitiveLandscape(String country, String clazz, String category, String series) {
        return competitorPricingRepository.getListOfCompetitorInGroup(country, clazz, category, series);
    }


}
