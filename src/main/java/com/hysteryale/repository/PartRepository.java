package com.hysteryale.repository;

import com.hysteryale.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface PartRepository extends JpaRepository<Part, String> {

    @Query("SELECT DISTINCT p FROM Part p WHERE p.orderNumber = ?1 ")
    public Set<Part> getPartByOrderNumber(String orderNumber) ;

    @Query(value = "SELECT * FROM Part p WHERE p.model_code = ?1 AND p.part_number = ?2 AND p.currency_currency = ?3 AND p.recorded_time = ?4 AND p.bill_to = ?5 LIMIT 1", nativeQuery = true)
    Optional<Part> getPartForMarginAnalysis(String modelCode, String partNumber, String currency, Calendar recordedTime, String dealer);

    @Query("SELECT DISTINCT p FROM Part p WHERE p.modelCode = ?1 AND p.partNumber = ?2 AND p.quoteId = ?3 AND p.recordedTime = ?4 AND p.currency.currency = ?5")
    Optional<Part> getPartForCheckingExisted(String modelCode, String partNumber, String quoteId, Calendar recordedTime, String currency);

    @Query("SELECT p FROM Part p WHERE p.modelCode = ?1 AND p.currency.currency = ?2 AND p.recordedTime = ?3 AND p.partNumber = ?4")
    List<Part> getNetPriceInPart(String modelCode, String currency, Calendar recordedTime, String partNumber);

    @Query("SElECT DISTINCT p.billTo FROM Part p WHERE p.modelCode = ?1 AND p.currency.currency = ?2 AND p.recordedTime = ?3 AND p.partNumber = ?4")
    List<String> getDealerNames(String modelCode, String currency, Calendar recordedTime, String partNumber);

    @Query("SElECT DISTINCT p FROM Part p WHERE p.partNumber = ?1 AND p.series = ?2 ")
    public Set<Part> getPartByPartNumberAndSeries(String partNumber, String series);


    @Query("SELECT DISTINCT p FROM Part p WHERE p.partNumber IN ?1 ")
    public Set<Part> getPartsByPartNumbers(List<String> partNumbers, Calendar date, String series);

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
