package com.hysteryale.controller;

import com.hysteryale.service.FilterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("filters")
public class FilterController {
    @Resource
    FilterService filterService;

    @GetMapping("/competitorPricing")
    public Map<String, Object> getCompetitorPricingFilters() {
        return filterService.getCompetitorPricingFilter();
    }

}

