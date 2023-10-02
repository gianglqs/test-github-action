package com.hysteryale.repository;

import com.hysteryale.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PartRepository extends JpaRepository<Part, String> {

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

    public Set<Part> getPartsByPartNumbers(List<String> partNumbers);
}
