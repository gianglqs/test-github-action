package com.hysteryale.controller;

import com.hysteryale.model.filters.OrderFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ShipmentController {
        @PostMapping("/shipment")
    public Map<String, Object> getDataFinancialShipment(@RequestBody OrderFilter filters,
                                                        @RequestParam(defaultValue = "0") int pageNo,
                                                        @RequestParam(defaultValue = "100") int perPage) {
        filters.setPageNo(pageNo);
        filters.setPerPage(perPage);

      //  return dataTableService.getShipmentByFilter(filters);
            return null;
    }
}
