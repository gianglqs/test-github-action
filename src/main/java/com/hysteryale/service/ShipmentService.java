package com.hysteryale.service;

import com.hysteryale.model.Shipment;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.repository.ShipmentRepository;
import com.hysteryale.utils.ConvertDataFilterUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShipmentService extends BasedService {
    @Resource
    ShipmentRepository shipmentRepository;

    public Map<String, Object> getShipmentByFilter(FilterModel filterModel) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        //Get FilterData
        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        logInfo(filterMap.toString());

        List<Shipment> shipmentList = shipmentRepository.findShipmentByFilterForTable(
                filterMap.get("orderNoFilter"), filterMap.get("regionFilter"), filterMap.get("plantFilter"),
                filterMap.get("metaSeriesFilter"), filterMap.get("classFilter"), filterMap.get("modelFilter"),
                filterMap.get("segmentFilter"), filterMap.get("dealerNameFilter"), filterMap.get("aopMarginPercentageFilter"),
                filterMap.get("marginPercentageFilter"), (Date) filterMap.get("fromDateFilter"),(Date)  filterMap.get("toDateFilter"),
                (Pageable) filterMap.get("pageable")
        );
        result.put("listShipment", shipmentList);
        //get total Recode
        int totalCompetitor = shipmentRepository.getCount(filterMap.get("orderNoFilter"), filterMap.get("regionFilter"), filterMap.get("plantFilter"),
                filterMap.get("metaSeriesFilter"), filterMap.get("classFilter"), filterMap.get("modelFilter"),
                filterMap.get("segmentFilter"), filterMap.get("dealerNameFilter"), filterMap.get("aopMarginPercentageFilter"),
                filterMap.get("marginPercentageFilter"),(Date)  filterMap.get("fromDateFilter"), (Date) filterMap.get("toDateFilter"));
        result.put("totalItems", totalCompetitor);
        return result;
    }

//    private Time
}
