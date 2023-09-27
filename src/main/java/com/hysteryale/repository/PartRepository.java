package com.hysteryale.repository;

import com.hysteryale.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, String> {

    @Query("SELECT p FROM Part p WHERE p.modelCode = ?1 AND p.currency.currency = ?2 AND p.recordedTime = ?3")
    public List<Part> getPartForMarginAnalysis(String modelCode, String currency, Calendar recordedTime);

    @Query("SELECT DISTINCT p FROM Part p WHERE p.modelCode = ?1 AND p.partNumber = ?2 AND p.quoteId = ?3 AND p.recordedTime = ?4 AND p.currency.currency = ?5")
    public Optional<Part> getPartForCheckingExisted(String modelCode, String partNumber, String quoteId, Calendar recordedTime, String currency);

    @Query("SELECT p FROM Part p WHERE p.modelCode = ?1 AND p.currency.currency = ?2")
    public List<Part> getPartsByModelCodeAndCurrency(String modelCode, String currency);
}
