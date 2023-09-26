package com.hysteryale.repository;

import com.hysteryale.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Calendar;

public interface PartRepository extends JpaRepository<Part, String> {
    @Query("SELECT DISTINCT p FROM Part p WHERE p.modelCode = ?1 AND p.partNumber = ?2 AND p.recordedTime = ?3")
    public Part getPartForMarginAnalysis(String modelCode, String partNumber, Calendar recordedTime);
}
