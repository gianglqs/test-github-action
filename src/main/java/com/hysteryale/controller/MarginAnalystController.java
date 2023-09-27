package com.hysteryale.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import com.hysteryale.model.marginAnalyst.MarginAnalystSummary;
import com.hysteryale.service.MarginAnalystService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@RestController

@Slf4j
public class MarginAnalystController {

    @Resource
    MarginAnalystService marginAnalystService;

    @GetMapping(path = "/marginAnalystData", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<MarginAnalystData>> getMarginAnalystData(@RequestParam String modelCode, @RequestParam String currency) {
        Calendar monthYear = Calendar.getInstance();
        return marginAnalystService.getMarginAnalystData(modelCode, currency, monthYear);
    }

    @GetMapping(path = "/marginAnalystSummary", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, MarginAnalystSummary> getMarginAnalystSummary(@RequestParam String modelCode, @RequestParam String currency) {
        return marginAnalystService.getMarginAnalystSummary(modelCode, currency);
    }
}

