package com.hysteryale.controller;

import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.model.filters.OrderFilter;
import com.hysteryale.service.ShipmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Map;

@RestController
public class ShipmentController {

    @Resource
    ShipmentService shipmentService;

    @PostMapping("/getShipmentData")
    public Map<String, Object> getDataFinancialShipment(@RequestBody FilterModel filters,
                                                        @RequestParam(defaultValue = "1") int pageNo,
                                                        @RequestParam(defaultValue = "100") int perPage) throws ParseException {
        filters.setPageNo(pageNo);
        filters.setPerPage(perPage);

        return shipmentService.getShipmentByFilter(filters);

    }
}
