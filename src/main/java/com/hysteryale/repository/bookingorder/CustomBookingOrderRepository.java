package com.hysteryale.repository.bookingorder;

import com.hysteryale.model.BookingOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Repository
public class CustomBookingOrderRepository {
    @PersistenceContext
    EntityManager entityManager;

    /**
     * Create JPQL query based on filters
     * If list of which filter it not null, then append to JPQL query
     */
    public Query createQueryByFilters(String queryString, String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries,
                                      List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, String AOPMarginPercetage, List<String> marginPercentage) throws ParseException {
        // Add more filters if the List containing value is not empty
        if (!regions.isEmpty())
            queryString += "AND b.region.region IN :regions ";
        if (!dealers.isEmpty())
            queryString += "AND b.dealerName IN :dealers ";
        if (!plants.isEmpty())
            queryString += "AND b.productDimension.plant IN :plants ";
        if (!metaSeries.isEmpty())
            queryString += "AND SUBSTRING(c.series, 2,3) IN :metaSeries ";
        if (!classes.isEmpty())
            queryString += "AND b.productDimension.clazz IN :classes ";
        if (!models.isEmpty())
            queryString += "AND b.model IN :models ";
        if (!segments.isEmpty())
            queryString += "AND b.productDimension.segment IN :segments ";
        if (!strFromDate.isEmpty())
            queryString += "AND b.date >= :fromDate ";
        if (!strToDate.isEmpty())
            queryString += "AND b.date <= :toDate ";


        if (!AOPMarginPercetage.isEmpty()) {
            if (AOPMarginPercetage.equals("Above AOP Margin %")) {
                queryString += "AND b.marginPercentageAfterSurCharge >= b.AOPMarginPercentage ";
            } else if (AOPMarginPercetage.equals("Below AOP Margin %")) {
                queryString += "AND b.marginPercentageAfterSurCharge < b.AOPMarginPercentage ";
            }
        }
        if (!marginPercentage.isEmpty()) {
            // queryString += "AND b.marginPercentageAfterSurCharge <> 'NaN' ";
            if(marginPercentage != null){
            switch (marginPercentage.get(0)) {
                case "<":
                    queryString += "AND b.marginPercentageAfterSurCharge < :marginPercentageGroup "; //
                    break;
                case ">":
                    queryString += "AND b.marginPercentageAfterSurCharge > :marginPercentageGroup ";
                    break;
                case "=":
                    queryString += "AND b.marginPercentageAfterSurCharge = :marginPercentageGroup ";
                    break;
                case ">=":
                    queryString += "AND b.marginPercentageAfterSurCharge >= :marginPercentageGroup ";
                    break;
                case "<=":
                    queryString += "AND b.marginPercentageAfterSurCharge <= :marginPercentageGroup ";
                    break;
            }}
        }

        Query query = entityManager.createQuery(queryString);
        query.setParameter("orderNo", orderNo);


        Calendar calendar;

        // Set value if parameter is existed
        if (!regions.isEmpty())
            query.setParameter("regions", regions);
        if (!dealers.isEmpty())
            query.setParameter("dealers", dealers);
        if (!plants.isEmpty())
            query.setParameter("plants", plants);
        if (!metaSeries.isEmpty())
            query.setParameter("metaSeries", metaSeries);
        if (!classes.isEmpty())
            query.setParameter("classes", classes);
        if (!models.isEmpty())
            query.setParameter("models", models);
        if (!segments.isEmpty())
            query.setParameter("segments", segments);
        if (!strFromDate.isEmpty()) {
            calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strFromDate));
            query.setParameter("fromDate", calendar);
        }
        if((!marginPercentage.isEmpty())){
            query.setParameter("marginPercentageGroup", marginPercentage.get(1));
        }

        if (!strToDate.isEmpty()) {
            calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strToDate));
            query.setParameter("toDate", calendar);
        }

        log.info(queryString);

        return query;
    }

//    private Query setParameter(List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, String AOPMarginPercetage, String MarginPercetage){
//
//    }

    private String addGroupByClaude(String subQuery){
        subQuery += " GROUP BY b.region.region, b.productDimension.plant, b.productDimension.clazz, b.series, b.model";
        return subQuery;
    }

    private List<BookingOrder> getTotalBookingOrder(List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, String AOPMarginPercetage, String MarginPercetage, int perPage, int offSet) throws ParseException {
        String queryString = "SELECT DISTINCT b FROM BookingOrder b  WHERE ) ";
return null;
    }
    /**
     * Get BookingOrder by filters (orderNo and List<String> ... ) and pagination (pageNo, perPage)
     */
//    public List<BookingOrder>
//    getBookingOrdersByFiltersByPage(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, String AOPMarginPercetage, String MarginPercetage, int perPage, int offSet) throws ParseException {
//
//        // Query for getting BookingOrder
//        // String queryString = "SELECT b FROM BookingOrder b WHERE b.orderNo LIKE CONCAT('%', :orderNo, '%')";
//        String queryString = "SELECT DISTINCT b FROM BookingOrder b  WHERE b.orderNo LIKE CONCAT('%', :orderNo, '%') ";
//
//        // Append filters
//        Query query = createQueryByFilters(queryString, orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate, AOPMarginPercetage, MarginPercetage);
//
//        // Setting pagination
//        query.setFirstResult(offSet);
//        query.setMaxResults(perPage);
//
//        // Get result list and parse into BookingOrder then add into a list
//        List bookingOrderList = query.getResultList();
//        List<BookingOrder> bookingOrders = new ArrayList<>();
//
//        for (Object object : bookingOrderList) {
//            BookingOrder bookingOrder = (BookingOrder) object;
//            bookingOrders.add(bookingOrder);
//        }
//        return bookingOrders;
//    }





}
