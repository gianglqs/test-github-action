package com.hysteryale.repository;

import com.hysteryale.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface PartRepository extends JpaRepository<Part, String> {

    @Query("SELECT DISTINCT p FROM Part p WHERE p.orderNumber = ?1 ")
    public Set<Part> getPartByOrderNumber(String orderNumber) ;

    @Query("SELECT DISTINCT p FROM Part p WHERE p.modelCode = ?1 AND p.partNumber = ?2 AND p.orderNumber = ?3 AND p.recordedTime = ?4 AND p.currency.currency = ?5")
    Optional<Part> getPartForCheckingExisted(String modelCode, String partNumber, String orderNumber, Calendar recordedTime, String currency);

    @Query("SELECT p FROM Part p WHERE p.modelCode = ?1 AND p.currency.currency = ?2 AND p.recordedTime = ?3 AND p.partNumber = ?4")
    List<Part> getNetPriceInPart(String modelCode, String currency, Calendar recordedTime, String partNumber);

    @Query("SElECT DISTINCT p FROM Part p WHERE p.partNumber = ?1 AND p.series = ?2 ")
    public Set<Part> getPartByPartNumberAndSeries(String partNumber, String series);

    @Query("SELECT DISTINCT p.modelCode FROM Part p WHERE p.recordedTime = ?1")
    public List<String> getDistinctModelCodeByMonthYear(Calendar monthYear);

    @Query(
            value =
            "select p.id, p.bill_to, p.customer_price, p.description, p.discount, p.discount_percentage, " +
                    "p.discount_to_customer_percentage, p.extended_customer_price, p.list_price, p.part_number, p.model_code, " +
                    "p.net_price_each, p.option_type, p.order_booked_date, p.order_request_date, p.order_number, p.quantity, p.quote_id, " +
                    "p.recorded_time, p.series, p.currency_currency " +
            "from (select *, row_number() over (partition by model_code, part_number, currency_currency, recorded_time, bill_to, order_number order by model_code asc) rn from part) p\n" +
            "where rn = 1 and model_code = :modelCode and recorded_time = :monthYear and currency_currency = :currency",
            nativeQuery = true
    )
    public List<Part> getDistinctPart(@Param("modelCode") String modelCode, @Param("monthYear") Calendar monthYear, @Param("currency") String currency);

    @Query("SELECT DISTINCT p FROM Part p WHERE p.partNumber IN ?1 ")
    public Set<Part> getPartsByPartNumbers(List<String> partNumbers, Calendar date, String series);

    @Query("SELECT AVG(p.netPriceEach) FROM Part p WHERE p.region = ?1 AND p.clazz = ?2 AND p.series = ?3")
    Double getAverageDealerNet(String region, String clazz, String series);

//    @Query("SELECT p FROM Part p WHERE p.partNumber = :partNumber AND p.series = :series " +
//            "AND EXTRACT(MONTH FROM p.recordedTime) = :month " +
//            "AND EXTRACT(YEAR FROM p.recordedTime) = :year")
//    List<Part> findPartsByPartNumberAndSeriesAndMonthAndYear(
//            @Param("partNumber") String partNumber,
//            @Param("series") String series,
//            @Param("month") int month,
//            @Param("year") int year
//    );
}
