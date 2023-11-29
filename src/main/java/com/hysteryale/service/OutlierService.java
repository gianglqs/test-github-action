package com.hysteryale.service;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.ChartOutlier;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.repository.bookingorder.BookingOrderRepository;
import com.hysteryale.utils.ConvertDataFilterUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

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
                (Calendar) filterMap.get("fromDateFilter"), (Calendar) filterMap.get("toDateFilter"),
                (Pageable) filterMap.get("pageable"));

        // count
        List<Integer> countAll = bookingOrderRepository.countAllForOutline(filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("dealerNameFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Calendar) filterMap.get("fromDateFilter"), (Calendar) filterMap.get("toDateFilter"));

        result.put("totalItems", countAll.size());
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

    public Map<String, Object> getDataForChart(FilterModel filters) throws ParseException {
        Map<String, Object> outliersData = new HashMap<>();

        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filters);
        List<BookingOrder> listOrder = bookingOrderRepository.getOrderForOutline(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("dealerNameFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Calendar) filterMap.get("fromDateFilter"), (Calendar) filterMap.get("toDateFilter"), null);

        List<Object> listRegionData = getListRegionData(listOrder);

        outliersData.put("chartOutliersData", listRegionData);
        return outliersData;
    }

    private List<Object> getListRegionData(List<BookingOrder> listOrder) {
        List<ChartOutlier> asiachartOutlierList = new ArrayList<>();
        List<ChartOutlier> pacificchartOutlierList = new ArrayList<>();
        List<ChartOutlier> chinachartOutlierList = new ArrayList<>();
        List<ChartOutlier> indiachartOutlierList = new ArrayList<>();

        for(BookingOrder order : listOrder) {
            ChartOutlier chartOutlier = new ChartOutlier(
                    order.getRegion().getRegion(),
                    order.getDealerNet(),
                    order.getDealerNetAfterSurCharge() == 0 ? 0 : order.getMarginAfterSurCharge() / order.getDealerNetAfterSurCharge(),
                    order.getModel()
            );
            switch (order.getRegion().getRegion()) {
                case "Asia":
                    asiachartOutlierList.add(chartOutlier);
                    break;
                case "Pacific":
                    pacificchartOutlierList.add(chartOutlier);
                    break;
                case "China":
                    chinachartOutlierList.add(chartOutlier);
                    break;
                case "India":
                    indiachartOutlierList.add(chartOutlier);
                    break;
            }
        }
        List<Object> listRegionData = new ArrayList<>();
        listRegionData.add(asiachartOutlierList);
        listRegionData.add(pacificchartOutlierList);
        listRegionData.add(chinachartOutlierList);
        listRegionData.add(indiachartOutlierList);
        return listRegionData;
    }

}
