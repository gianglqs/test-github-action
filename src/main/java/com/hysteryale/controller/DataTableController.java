package com.hysteryale.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hysteryale.model.competitor.CompetitorPricing;
import net.minidev.json.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("table")
public class DataTableController {

    @PostMapping("/indicator")
    public List<CompetitorPricing> getDataCompetitorPricing(@RequestBody String filters,
                                                            @RequestParam(defaultValue = "1") int pageNo,
                                                            @RequestParam(defaultValue = "100") int perPage) throws ParseException, JsonProcessingException, java.text.ParseException {
        return null;
    }

}
