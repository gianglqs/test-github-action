package com.hysteryale.service;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.Shipment;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.repository.ShipmentRepository;
import com.hysteryale.repository.bookingorder.BookingOrderRepository;
import com.hysteryale.utils.ConvertDataFilterUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.text.ParseException;

@Service
public class ShipmentService extends BasedService {
    @Resource
    ShipmentRepository shipmentRepository;

    @Resource
    BookingOrderRepository bookingOrderRepository;

    public Map<String, Object> getShipmentByFilter(FilterModel filterModel) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        //Get FilterData
        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        logInfo(filterMap.toString());

        List<Shipment> shipmentList = shipmentRepository.findShipmentByFilterForTable(
                filterMap.get("orderNoFilter"), filterMap.get("regionFilter"), filterMap.get("plantFilter"),
                filterMap.get("metaSeriesFilter"), filterMap.get("classFilter"), filterMap.get("modelFilter"),
                filterMap.get("segmentFilter"), filterMap.get("dealerNameFilter"), filterMap.get("aopMarginPercentageFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Calendar) filterMap.get("fromDateFilter"), (Calendar) filterMap.get("toDateFilter"),
                (Pageable) filterMap.get("pageable"));
        result.put("listShipment", shipmentList);
        //get total Recode
        int countAll = shipmentRepository.getCount(filterMap.get("orderNoFilter"), filterMap.get("regionFilter"), filterMap.get("plantFilter"),
                filterMap.get("metaSeriesFilter"), filterMap.get("classFilter"), filterMap.get("modelFilter"),
                filterMap.get("segmentFilter"), filterMap.get("dealerNameFilter"), filterMap.get("aopMarginPercentageFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Calendar) filterMap.get("fromDateFilter"), (Calendar) filterMap.get("toDateFilter"));
        result.put("totalItems", countAll);

        // get total
        List<Shipment> getTotal = shipmentRepository.getTotal(filterMap.get("orderNoFilter"), filterMap.get("regionFilter"),
                filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"), filterMap.get("classFilter"), filterMap.get("modelFilter"),
                filterMap.get("segmentFilter"), filterMap.get("dealerNameFilter"), filterMap.get("aopMarginPercentageFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Calendar) filterMap.get("fromDateFilter"), (Calendar) filterMap.get("toDateFilter"));
        result.put("total", getTotal);

        // get totalMarginPercentage of bookingOrder
        double getTotalBookingMarginPercentage = bookingOrderRepository.getTotalMarginPercentage(filterMap.get("orderNoFilter"), filterMap.get("regionFilter"),
                filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"), filterMap.get("classFilter"), filterMap.get("modelFilter"),
                filterMap.get("segmentFilter"), filterMap.get("dealerNameFilter"), filterMap.get("aopMarginPercentageFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Calendar) filterMap.get("fromDateFilter"), (Calendar) filterMap.get("toDateFilter"));
        getTotal.get(0).setBookingMarginPercentageAfterSurCharge(getTotalBookingMarginPercentage);

        return result;
    }

    public Shipment getShipmentByOrderNo(String orderNo) {
        Optional<Shipment> optionalShipment = shipmentRepository.findShipmentByOrderNo(orderNo);
        if (optionalShipment.isPresent())
            return optionalShipment.get();
        return null;
    }



//    private Time
}
