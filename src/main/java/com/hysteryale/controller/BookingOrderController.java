package com.hysteryale.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hysteryale.model.filters.BookingOrderFilter;
import com.hysteryale.service.APACSerialService;
import com.hysteryale.service.APICDealerService;
import com.hysteryale.service.BookingOrderService;
import com.hysteryale.service.MetaSeriesService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
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
    public Map<String, Object> getFilters() {
        Map<String, Object> filters = new HashMap<>();

        //filters.put("dealers", apicDealerService.getAllAPICDealers());
        filters.put("dealers", bookingOrderService.getAllDealer());
        filters.put("plants", apacSerialService.getAllPlants());
        filters.put("metaSeries", metaSeriesService.getAllMetaSeries());
        filters.put("classes", metaSeriesService.getMetaSeriesClasses());
        filters.put("segments", metaSeriesService.getMetaSeriesSegments());
     //  filters.put("models", apacSerialService.getAllAPACSerialModels());
        filters.put("models", bookingOrderService.getAllModel());
        filters.put("AOPMarginPercetage", bookingOrderService.getAPOMarginPercentageForFilter());
        filters.put("MarginPercetage", bookingOrderService.getMarginPercentageForFilter());

        return filters;
    }

    /**
     * Get BookingOrders based on filters and pagination
     *
     * @param filters from FE
     * @param pageNo  current page
     * @param perPage number of items per page
     */
    @GetMapping(path = "/bookingOrders", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getBookingOrders(@RequestBody String filters,
                                                @RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "100") int perPage) throws ParseException, JsonProcessingException, java.text.ParseException {

        BookingOrderFilter bookingOrderFilter = handleFilterData(filters);
        // Get BookingOrder
        Map<String, Object> bookingOrderPage = bookingOrderService.getBookingOrdersByFilters(bookingOrderFilter, pageNo - 1, perPage);

        // totalPages for all BookingOrders based on totalItems and perPage
        long totalPages = (long) bookingOrderPage.get("totalItems") / perPage;

        // if (totalPages % perPage) != 0 then need one more page to show data
        if ((long) bookingOrderPage.get("totalItems") % perPage != 0)
            totalPages = ((long) bookingOrderPage.get("totalItems") / perPage) + 1;

        bookingOrderPage.put("totalPages", totalPages);

        // assign number of items per page and currentPage number
        bookingOrderPage.put("perPage", perPage);
        bookingOrderPage.put("page", pageNo);

        return bookingOrderPage;
    }

    private BookingOrderFilter handleFilterData(String rawJsonFilters) throws ParseException, JsonProcessingException {
        //Parse rawJsonFilters from String to JSONObject
        JSONParser parser = new JSONParser();
        JSONObject filters = (JSONObject) parser.parse(rawJsonFilters);

        // Use ObjectMapper to Map JSONObject value into List<String
        ObjectMapper mapper = new ObjectMapper();
        String orderNo = filters.get("orderNo").toString();


        // Parse all filters into ArrayList<String>
        List<String> regions = Arrays.asList(mapper.readValue(filters.get("regions").toString(), String[].class));
        List<String> dealers = Arrays.asList(mapper.readValue(filters.get("dealers").toString(), String[].class));
        List<String> plants = Arrays.asList(mapper.readValue(filters.get("plants").toString(), String[].class));
        List<String> metaSeries = Arrays.asList(mapper.readValue(filters.get("metaSeries").toString(), String[].class));
        List<String> classes = Arrays.asList(mapper.readValue(filters.get("classes").toString(), String[].class));
        List<String> models = Arrays.asList(mapper.readValue(filters.get("models").toString(), String[].class));
        List<String> segments = Arrays.asList(mapper.readValue(filters.get("segments").toString(), String[].class));

        String AOPMarginPercetage = filters.get("AOPMarginPercetage").toString();
        String MarginPercetage = filters.get("MarginPercetage").toString();
        // Get from DATE to DATE
        String strFromDate = filters.get("fromDate").toString();
        String strToDate = filters.get("toDate").toString();

        return new BookingOrderFilter(orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate, AOPMarginPercetage, MarginPercetage);
    }
}
