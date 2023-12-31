package com.hysteryale.service;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.filters.CalculatorModel;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.model.payLoad.AdjustmentPayLoad;
import com.hysteryale.repository.bookingorder.BookingOrderRepository;
import com.hysteryale.utils.ConvertDataFilterUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

@Service
public class AdjustmentService extends BasedService {

    @Resource
    BookingOrderRepository bookingOrderRepository;

    /**
     * load OrderByFilter
     */
    public Map<String, Object> getAdjustmentByFilter(FilterModel filterModel, CalculatorModel calculatorModel) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);

        // '000 USD -> USD to calculate
        calculatorModel.setFreightAdj(calculatorModel.getFreightAdj() * 1000);
        calculatorModel.setFxAdj(calculatorModel.getFxAdj() * 1000);

        logInfo(filterMap.toString());
        //TODO : set Margin after Adj for filter
        logInfo("marginPercentageAfterAdjFilter" + filterMap.get("marginPercentageAfterAdjFilter"));

        List<BookingOrder> bookingOrderList = bookingOrderRepository.selectForAdjustmentByFilter(
                filterMap.get("regionFilter"), filterMap.get("dealerNameFilter"), filterMap.get("plantFilter"), filterMap.get("segmentFilter"),
                filterMap.get("classFilter"), filterMap.get("metaSeriesFilter"), filterMap.get("modelFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                ((List) filterMap.get("marginPercentageAfterAdjFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageAfterAdjFilter")).get(0),
                ((List) filterMap.get("marginPercentageAfterAdjFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageAfterAdjFilter")).get(1),
                calculatorModel.getCostAdjPercentage(), calculatorModel.getFreightAdj(), calculatorModel.getFxAdj(), calculatorModel.getDnAdjPercentage(),
                (Pageable) filterMap.get("pageable"));

        //convert booking to adjustment
        List<AdjustmentPayLoad> listAdj = convertToListAdjustment(bookingOrderList, calculatorModel);
        setIdForList(listAdj);
        result.put("listAdjustment", listAdj);
        // get total
        List<BookingOrder> getAll = bookingOrderRepository.selectTotalForAdjustment(filterMap.get("regionFilter"), filterMap.get("dealerNameFilter"), filterMap.get("plantFilter"), filterMap.get("segmentFilter"),
                filterMap.get("classFilter"), filterMap.get("metaSeriesFilter"), filterMap.get("modelFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                ((List) filterMap.get("marginPercentageAfterAdjFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageAfterAdjFilter")).get(0),
                ((List) filterMap.get("marginPercentageAfterAdjFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageAfterAdjFilter")).get(1),
                calculatorModel.getCostAdjPercentage(), calculatorModel.getFreightAdj(), calculatorModel.getFxAdj(), calculatorModel.getDnAdjPercentage());
        List<AdjustmentPayLoad> totalAdj = convertToListAdjustment(getAll, calculatorModel);
        List<AdjustmentPayLoad> calculateAll = calculateTotal(totalAdj, calculatorModel);
        result.put("total", calculateAll);

        List<Integer> countAll = bookingOrderRepository.getCountAllForAdjustmentByFilter(
                filterMap.get("regionFilter"), filterMap.get("dealerNameFilter"), filterMap.get("plantFilter"), filterMap.get("segmentFilter"),
                filterMap.get("classFilter"), filterMap.get("metaSeriesFilter"), filterMap.get("modelFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                ((List) filterMap.get("marginPercentageAfterAdjFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageAfterAdjFilter")).get(0),
                ((List) filterMap.get("marginPercentageAfterAdjFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageAfterAdjFilter")).get(1),
                calculatorModel.getCostAdjPercentage(), calculatorModel.getFreightAdj(), calculatorModel.getFxAdj(), calculatorModel.getDnAdjPercentage());

        result.put("totalItems", countAll.size());

//        List<BookingOrder> getTotalAdjustment = bookingOrderRepository.selectTotalForAdjustment(
//                filterMap.get("regionFilter"), filterMap.get("dealerNameFilter"), filterMap.get("plantFilter"), filterMap.get("segmentFilter"),
//                filterMap.get("classFilter"), filterMap.get("metaSeriesFilter"), filterMap.get("modelFilter"),
//                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
//                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
//                ((List) filterMap.get("marginPercentageAfterAdjFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageAfterAdjFilter")).get(0),
//                ((List) filterMap.get("marginPercentageAfterAdjFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageAfterAdjFilter")).get(1),
//                calculatorModel.getCostAdjPercentage(), calculatorModel.getFreightAdj(), calculatorModel.getFxAdj(), calculatorModel.getDnAdjPercentage());

        //       System.out.println(getTotalAdjustment.size());
        // result.put("totalItems", countAll.size());


        return result;
    }

    private List<AdjustmentPayLoad> setIdForList(List<AdjustmentPayLoad> list) {
        int id = 0;
        for (AdjustmentPayLoad adjustmentPayLoad : list) {
            adjustmentPayLoad.setId(id);
            id++;
        }
        return list;
    }

    private List<AdjustmentPayLoad> convertToListAdjustment(List<BookingOrder> bookingOrders, CalculatorModel calculatorModel) {
        List<AdjustmentPayLoad> result = new ArrayList<>();
        bookingOrders.forEach(b -> result.add(convertToAdjustment(b, calculatorModel)));
        return result;
    }

    private List<AdjustmentPayLoad> calculateTotal(List<AdjustmentPayLoad> list, CalculatorModel calculatorModel) {
        List<AdjustmentPayLoad> result = new ArrayList<>();
        AdjustmentPayLoad adjustmentPayLoad = new AdjustmentPayLoad();
        adjustmentPayLoad.setManualAdjFX(calculatorModel.getFxAdj());
        adjustmentPayLoad.setManualAdjFreight(calculatorModel.getFreightAdj());
        for (AdjustmentPayLoad adj : list) {
            adjustmentPayLoad.setManualAdjCost(adjustmentPayLoad.getManualAdjCost() + adj.getManualAdjCost());
            adjustmentPayLoad.setTotalManualAdjCost(adjustmentPayLoad.getTotalManualAdjCost() + adj.getTotalManualAdjCost());

            adjustmentPayLoad.setOriginalDN(adjustmentPayLoad.getOriginalDN() + adj.getOriginalDN());
            adjustmentPayLoad.setOriginalMargin(adjustmentPayLoad.getOriginalMargin() + adj.getOriginalMargin());

            adjustmentPayLoad.setNewDN(adjustmentPayLoad.getNewDN() + adj.getNewDN());
            adjustmentPayLoad.setNewMargin(adjustmentPayLoad.getNewMargin() + adj.getNewMargin());
            adjustmentPayLoad.setNoOfOrder(adjustmentPayLoad.getNoOfOrder() + adj.getNoOfOrder());
            adjustmentPayLoad.setAdditionalVolume(adjustmentPayLoad.getAdditionalVolume() + adj.getAdditionalVolume());
        }
        adjustmentPayLoad.setOriginalMarginPercentage(adjustmentPayLoad.getOriginalMargin() / adjustmentPayLoad.getOriginalDN());
        adjustmentPayLoad.setNewMarginPercentage(adjustmentPayLoad.getNewMargin() / adjustmentPayLoad.getNewDN());
        result.add(adjustmentPayLoad);
        return result;
    }

    private AdjustmentPayLoad convertToAdjustment(BookingOrder booking, CalculatorModel calculatorModel) {
        AdjustmentPayLoad adjustmentPayLoad = new AdjustmentPayLoad();
        if (booking.getRegion() != null) {
            adjustmentPayLoad.setRegion(booking.getRegion().getRegion());
        }
        if (booking.getProductDimension() != null) {
            adjustmentPayLoad.setPlant(booking.getProductDimension().getPlant());
            adjustmentPayLoad.setClazz(booking.getProductDimension().getClazz());
        }

        adjustmentPayLoad.setModel(booking.getModel());
        if (booking.getSeries() != null)
            adjustmentPayLoad.setMetaSeries(booking.getSeries().substring(1));

        adjustmentPayLoad.setNoOfOrder(booking.getQuantity());

        adjustmentPayLoad.setOriginalDN(booking.getDealerNetAfterSurCharge());
        adjustmentPayLoad.setOriginalMargin(booking.getMarginAfterSurCharge());

        //recalculate MarginPercentageAfterSurCharge of booking in group
        double marginPercentageAfterSurcharge = booking.getMarginAfterSurCharge() / booking.getDealerNetAfterSurCharge();
        booking.setMarginPercentageAfterSurCharge(marginPercentageAfterSurcharge);
        adjustmentPayLoad.setOriginalMarginPercentage(booking.getMarginPercentageAfterSurCharge());

        //calculate
        // Manual Adj Cost (‘000 USD) = Total Cost * Cost Adj% (1) - (rename to Adjusted Cost)
        double totalCost = booking.getTotalCost();
        adjustmentPayLoad.setManualAdjCost(totalCost * (1 + calculatorModel.getCostAdjPercentage() / 100));

        //Manual Freight Adj (‘000 USD) = get from the Adjustment Controller  (2) - (rename to Adjusted Freight)
        adjustmentPayLoad.setManualAdjFreight(calculatorModel.getFreightAdj());

        //Manual FX Adj (‘000 USD) = get from the Adjustment Controller (3) - (rename to Adjusted FX)
        adjustmentPayLoad.setManualAdjFX(calculatorModel.getFxAdj());

        //Total Manual Adj Cost = (1) + (2) + (3) (4)
        adjustmentPayLoad.setTotalManualAdjCost(adjustmentPayLoad.getManualAdjCost() + adjustmentPayLoad.getManualAdjFreight() + adjustmentPayLoad.getManualAdjFX());

        //New DN (‘000 USD) - After manual Adj = Original DN * DN Adj % (rename to Adjusted Dealer Net) (5)
        adjustmentPayLoad.setNewDN(booking.getDealerNetAfterSurCharge() * (1 + calculatorModel.getDnAdjPercentage() / 100));

        //New margin $ (‘000 USD) - After  manual Adj  =  (5) - (4) (6)
        adjustmentPayLoad.setNewMargin(adjustmentPayLoad.getNewDN() - adjustmentPayLoad.getTotalManualAdjCost());

        // Margin %
        adjustmentPayLoad.setNewMarginPercentage(adjustmentPayLoad.getNewMargin() / adjustmentPayLoad.getNewDN());

        //Additional Volume at BEP For Discount =  ABS( margin) / (DN* % DN adj) - (Original total cost/no of order)
        adjustmentPayLoad.setAdditionalVolume((int) (Math.round(booking.getMarginAfterSurCharge() / (adjustmentPayLoad.getNewDN() / booking.getQuantity() - (adjustmentPayLoad.getTotalManualAdjCost() / booking.getQuantity())) - booking.getQuantity())));

        return adjustmentPayLoad;
    }

}
