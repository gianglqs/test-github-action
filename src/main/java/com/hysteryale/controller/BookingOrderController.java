package com.hysteryale.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hysteryale.service.APACSerialService;
import com.hysteryale.service.APICDealerService;
import com.hysteryale.service.BookingOrderService;
import com.hysteryale.service.MetaSeriesService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class BookingOrderController {
  
    @Resource
    BookingOrderService bookingOrderService;
    @Resource
    APICDealerService apicDealerService;
    @Resource
    APACSerialService apacSerialService;
    @Resource
    MetaSeriesService metaSeriesService;

    /**
     * Get filters' value as: dealers, plants, metaSeries, classes, segments, models for BookingOrders' filtering
     */
    @GetMapping(path = "/filters")
    public Map<String, List<String>> getFilters() {
        Map<String, List<String>> filters = new HashMap<>();

        filters.put("dealers", apicDealerService.getAllAPICDealers());
        filters.put("plants", apacSerialService.getAllPlants());
        filters.put("metaSeries", metaSeriesService.getAllMetaSeries());
        filters.put("classes", metaSeriesService.getMetaSeriesClasses());
        filters.put("segments", metaSeriesService.getMetaSeriesSegments());
        filters.put("models", apacSerialService.getAllAPACSerialModels());

        return filters;
    }

    /**
     * Get BookingOrders based on filters and pagination
     * @param filters from FE
     * @param pageNo current page
     * @param perPage number of items per page
     */
    @GetMapping(path = "/bookingOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getBookingOrders(@RequestBody String filters,
                                                            @RequestParam(defaultValue = "1") int pageNo,
                                                            @RequestParam(defaultValue = "100") int perPage) throws ParseException, JsonProcessingException, java.text.ParseException {

        // Get BookingOrder
        Map<String, Object> bookingOrderPage = bookingOrderService.getBookingOrdersByFilters(filters, pageNo - 1, perPage);

        // totalPages for all BookingOrders based on totalItems and perPage
        long totalPages = (long)bookingOrderPage.get("totalItems") / perPage;

        // if (totalPages % perPage) != 0 then need one more page to show data
        if( (long)bookingOrderPage.get("totalItems") % perPage != 0)
            totalPages = ((long)bookingOrderPage.get("totalItems") / perPage) + 1;

        bookingOrderPage.put("totalPages", totalPages);

        // assign number of items per page and currentPage number
        bookingOrderPage.put("perPage", perPage);
        bookingOrderPage.put("page", pageNo);

        return bookingOrderPage;
    }
}
