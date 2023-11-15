package com.hysteryale.service;

import com.hysteryale.model.filters.OrderFilter;
import com.hysteryale.repository.ShipmentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShipmentService extends BasedService{
    @Resource
    ShipmentRepository shipmentRepository;
//        public Map<String, Object> getShipmentByFilter(OrderFilter orderFilter) {
//        logInfo(orderFilter.toString());
//        Map<String, Object> result = new HashMap<>();
//        Pageable pageable = PageRequest.of(orderFilter.getPageNo(), orderFilter.getPerPage());
//        List<CompetitorPricing> shipmentList = shipmentRepository.findShipmentByFilterForTable(
//                orderFilter.getOrderNo() == null || orderFilter.getOrderNo().isEmpty() ? null : orderFilter.getOrderNo(),
//                orderFilter.getDealers() == null || orderFilter.getDealers().isEmpty() ? null : orderFilter.getDealers(),
//                orderFilter.getRegions() == null || orderFilter.getRegions().isEmpty() ? null : orderFilter.getRegions(),
//                orderFilter.getPlants() == null || orderFilter.getPlants().isEmpty() ? null : orderFilter.getPlants(),
//                orderFilter.getMetaSeries() == null || orderFilter.getMetaSeries().isEmpty() ? null : orderFilter.getMetaSeries(),
//                orderFilter.getClasses() == null || orderFilter.getClasses().isEmpty() ? null : orderFilter.getClasses(),
//                orderFilter.getModels() == null || orderFilter.getModels().isEmpty() ? null : orderFilter.getModels(),
//                orderFilter.getSegments() == null || orderFilter.getSegments().isEmpty() ? null : orderFilter.getSegments(),
////                orderFilter.getAOPMarginPercetage() == null || orderFilter.getAOPMarginPercetage().isEmpty() ? null : orderFilter.getAOPMarginPercetage(),
////                orderFilter.getMarginPercetage() == null || orderFilter.getMarginPercetage().isEmpty() ? null : orderFilter.getMarginPercetage(),
////                orderFilter.getMarginPercetage() == null || orderFilter.getMarginPercetage().isEmpty() ? null : orderFilter.getMarginPercetage(),
//                pageable
//        );
//        result.put("listShipment", shipmentList);
//        //get total Recode
//        int totalCompetitor = shipmentRepository.getCountAll();
//        result.put("totalItems", totalCompetitor);
//        return result;
//    }
//
//    private Time
}
