package com.hysteryale.controller;

import com.hysteryale.model.filters.IndicatorFilter;
import com.hysteryale.service.DataTableService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("table")
public class DataTableController {

    @Resource
    DataTableService dataTableService;

    @PostMapping("/indicator")
    public Map<String, Object> getDataCompetitorPricing(@RequestBody IndicatorFilter filters,
                                                        @RequestParam(defaultValue = "1") int pageNo,
                                                        @RequestParam(defaultValue = "100") int perPage) {
        filters.setPageNo(pageNo);
        filters.setPerPage(perPage);

        return dataTableService.getCompetitorPriceForTableByFilter(filters);
    }

}
