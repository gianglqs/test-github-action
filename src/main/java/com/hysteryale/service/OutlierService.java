package com.hysteryale.service;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.repository.bookingorder.BookingOrderRepository;
import com.hysteryale.utils.ConvertDataFilterUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OutlierService extends BasedService {

    @Resource
    BookingOrderRepository bookingOrderRepository;

    public Map<String, Object> getDataForTable(FilterModel filterModel) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        //convert data filter
        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        List<BookingOrder> listOrder = bookingOrderRepository.getOrderForOutline(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("dealerNameFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Date) filterMap.get("fromDateFilter"), (Date) filterMap.get("toDateFilter"),
                (Pageable) filterMap.get("pageable"));


        // count
//        long countAll = bookingOrderRepository.countAll(filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
//                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("dealerNameFilter"),
//                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
//                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
//                (Date) filterMap.get("fromDateFilter"), (Date) filterMap.get("toDateFilter"));
        List<BookingOrder> listAllOrder = bookingOrderRepository.getAllForOutlier(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("dealerNameFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Date) filterMap.get("fromDateFilter"), (Date) filterMap.get("toDateFilter"));
        result.put("totalItems", listAllOrder.size());
        result.put("listOutlier", setIdForData(listOrder));
        return result;
    }

    public List<BookingOrder> setIdForData(List<BookingOrder> bookings) {
        long i = 0;
        for (BookingOrder booking : bookings) {
            booking.setOrderNo(String.valueOf(i));
            booking.setMarginPercentageAfterSurCharge(booking.getMarginAfterSurCharge() / booking.getDealerNetAfterSurCharge());
            i++;
        }
        return bookings;
    }

}
