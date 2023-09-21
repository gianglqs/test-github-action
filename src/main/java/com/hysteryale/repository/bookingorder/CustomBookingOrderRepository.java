package com.hysteryale.repository.bookingorder;

import com.hysteryale.model.BookingOrder;
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

@Repository
public class CustomBookingOrderRepository {
    @PersistenceContext
    EntityManager entityManager;

    /**
     * Create JPQL query based on filters
     * If list of which filter it not null, then append to JPQL query
     */
    public Query createQueryByFilters(String queryString, String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate) throws ParseException {
        // Add more filters if the List containing value is not empty
        if(!regions.isEmpty())
            queryString += "AND b.region IN :regions ";
        if(!dealers.isEmpty())
            queryString += "AND b.billTo.dealerDivison IN :dealers ";
        if(!plants.isEmpty())
            queryString += "AND b.apacSerial.plant IN :plants ";
        if(!metaSeries.isEmpty())
            queryString += "AND b.apacSerial.metaSeries.series IN :metaSeries ";
        if(!classes.isEmpty())
            queryString += "AND b.apacSerial.metaSeries.clazz IN :classes ";
        if(!models.isEmpty())
            queryString += "AND b.apacSerial.model IN :models ";
        if(!segments.isEmpty())
            queryString += "AND b.apacSerial.metaSeries.segment1 IN :segments ";
        if(!strFromDate.isEmpty() && !strToDate.isEmpty())
            queryString += "AND b.date BETWEEN :fromDate AND :toDate";

        Query query = entityManager.createQuery(queryString);
        query.setParameter("orderNo", orderNo);


        Calendar calendar = Calendar.getInstance();

        // Set value if parameter is existed
        if(!regions.isEmpty())
            query.setParameter("regions", regions);
        if(!dealers.isEmpty())
            query.setParameter("dealers", dealers);
        if(!plants.isEmpty())
            query.setParameter("plants", plants);
        if(!metaSeries.isEmpty())
            query.setParameter("metaSeries", metaSeries);
        if(!classes.isEmpty())
            query.setParameter("classes", classes);
        if(!models.isEmpty())
            query.setParameter("models", models);
        if(!segments.isEmpty())
            query.setParameter("segments", segments);
        if(!strFromDate.isEmpty() && !strToDate.isEmpty()) {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strFromDate));
            query.setParameter("fromDate", calendar);

            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strToDate));
            query.setParameter("toDate", calendar);
        }

        return query;
    }

    /**
     * Get BookingOrder by filters (orderNo and List<String> ... ) and pagination (pageNo, perPage)
     */
    public List<BookingOrder> getBookingOrdersByFiltersByPage(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, int perPage, int offSet) throws ParseException {

        // Query for getting BookingOrder
        String queryString = "SELECT b FROM BookingOrder b WHERE b.orderNo LIKE CONCAT('%', :orderNo, '%')";

        // Append filters
        Query query = createQueryByFilters(queryString, orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate);

        // Setting pagination
        query.setFirstResult(offSet);
        query.setMaxResults(perPage);

        // Get result list and parse into BookingOrder then add into a list
        List bookingOrderList = query.getResultList();
        List<BookingOrder> bookingOrders = new ArrayList<>();

        for (Object object : bookingOrderList) {
            BookingOrder bookingOrder = (BookingOrder) object;
            bookingOrders.add(bookingOrder);
        }
        return bookingOrders;
    }

    /**
     * Get the number of BookingOrders returned based on filters
     */
    public long getNumberOfBookingOrderByFilters(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate) throws ParseException {
        String queryString = "SELECT COUNT(b) FROM BookingOrder b WHERE b.orderNo LIKE CONCAT('%', :orderNo, '%')";
        Query query = createQueryByFilters(queryString, orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate);
        return (long) query.getSingleResult();
    }
}
