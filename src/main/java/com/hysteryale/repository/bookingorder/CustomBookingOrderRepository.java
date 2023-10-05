package com.hysteryale.repository.bookingorder;

import com.hysteryale.dto.BookingOrderDTO;
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
import java.util.Date;
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
                                      List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, String AOPMarginPercetage, String MarginPercetage) throws ParseException {
        // Add more filters if the List containing value is not empty
        if (!regions.isEmpty())
            queryString += "AND b.region IN :regions ";
        if (!dealers.isEmpty())
            queryString += "AND b.dealerName IN :dealers ";
//        if (!plants.isEmpty())
//            queryString += "AND b.apacSerial.plant IN :plants "; //
        if (!metaSeries.isEmpty())
            queryString += "AND b.Series IN :metaSeries ";
        if (!classes.isEmpty())
            queryString += "AND m.clazz IN :classes ";
        if (!models.isEmpty())
            queryString += "AND b.model IN :models ";
//        if (!segments.isEmpty())
//            queryString += "AND b.apacSerial.metaSeries.segment1 IN :segments ";
        if (!strFromDate.isEmpty())
            queryString += "AND b.date >= :fromDate ";

        if (!strToDate.isEmpty())
            queryString += "AND b.date <= :toDate";


        if (!AOPMarginPercetage.isEmpty()) {
            if (AOPMarginPercetage.equals("Above AOP Margin %")) {
                queryString += "AND b.marginPercentageAfterSurCharge >= b.AOPMarginPercentage";
            } else if (AOPMarginPercetage.equals("Below AOP Margin %")) {
                queryString += "AND b.marginPercentageAfterSurCharge < b.AOPMarginPercentage";
            }
        }
        if (!MarginPercetage.isEmpty()) {
            queryString += "AND b.marginPercentageAfterSurCharge <> 'NaN'";
            switch (MarginPercetage) {
                case "<10% Margin":
                    queryString += "AND b.marginPercentageAfterSurCharge < 0.1 "; //
                    break;
                case "<20% Margin":
                    queryString += "AND b.marginPercentageAfterSurCharge < 0.2";
                    break;
                case "<30% Margin":
                    queryString += "AND b.marginPercentageAfterSurCharge < 0.3";
                    break;
                case ">=30% Margin":
                    queryString += "AND b.marginPercentageAfterSurCharge >= 0.3";
                    break;
            }
        }

        Query query = entityManager.createQuery(queryString);
        query.setParameter("orderNo", orderNo);


        Calendar calendar;

        // Set value if parameter is existed
        if (!regions.isEmpty())
            query.setParameter("regions", regions);
        if (!dealers.isEmpty())
            query.setParameter("dealers", dealers);
//        if (!plants.isEmpty())
//            query.setParameter("plants", plants);
        if (!metaSeries.isEmpty())
            query.setParameter("metaSeries", metaSeries);
        if (!classes.isEmpty())
            query.setParameter("classes", classes);
        if (!models.isEmpty())
            query.setParameter("models", models);
//        if (!segments.isEmpty())
//            query.setParameter("segments", segments);
        if (!strFromDate.isEmpty()) {
            calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strFromDate));
            query.setParameter("fromDate", calendar);

        }
        if (!strToDate.isEmpty()) {
            calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strToDate));
            query.setParameter("toDate", calendar);
        }

        log.error(query.unwrap(org.hibernate.query.Query.class).getQueryString());
        log.info(queryString);
        log.info("AOPMarginPercetage:      " + MarginPercetage);

        return query;
    }

    /**
     * Get BookingOrder by filters (orderNo and List<String> ... ) and pagination (pageNo, perPage)
     */
    public List<BookingOrderDTO>
    getBookingOrdersByFiltersByPage(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, String AOPMarginPercetage, String MarginPercetage, int perPage, int offSet) throws ParseException {

        // Query for getting BookingOrder
        // String queryString = "SELECT b FROM BookingOrder b WHERE b.orderNo LIKE CONCAT('%', :orderNo, '%')";
        String queryString = "SELECT new com.hysteryale.dto.BookingOrderDTO(b.orderNo, b.date, b.currency, b.orderType, b.region, b.ctryCode, b.dealerPO, b.dealerName, b.comment, b.Series, b.billTo, b.model, b.truckClass, m.clazz, b.quantity, b.totalCost, b.dealerNet, b.dealerNetAfterSurCharge, b.marginAfterSurCharge, b.marginPercentageAfterSurCharge, b.AOPMarginPercentage) FROM BookingOrder b LEFT JOIN MetaSeries m ON SUBSTRING(b.Series, 2, LENGTH(m.series)) = m.series WHERE b.orderNo LIKE CONCAT('%', :orderNo, '%')";

        // Append filters
        Query query = createQueryByFilters(queryString, orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate, AOPMarginPercetage, MarginPercetage);

        // Setting pagination
        query.setFirstResult(offSet);
        query.setMaxResults(perPage);

        // Get result list and parse into BookingOrder then add into a list
        List bookingOrderList = query.getResultList();
        List<BookingOrderDTO> bookingOrders = new ArrayList<>();

        for (Object object : bookingOrderList) {
            BookingOrderDTO bookingOrder = (BookingOrderDTO) object;
            bookingOrders.add(bookingOrder);
        }
        return bookingOrders;
    }

    /**
     * Get the number of BookingOrders returned based on filters
     */
    public long getNumberOfBookingOrderByFilters(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, String AOPMarginPercetage, String MarginPercetage) throws ParseException {
        String queryString = "SELECT COUNT(b) FROM BookingOrder b LEFT JOIN MetaSeries m ON SUBSTRING(b.Series, 2, LENGTH(m.series)) = m.series WHERE b.orderNo LIKE CONCAT('%', :orderNo, '%')";
        Query query = createQueryByFilters(queryString, orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate, AOPMarginPercetage, MarginPercetage);
        return (long) query.getSingleResult();
    }
}
