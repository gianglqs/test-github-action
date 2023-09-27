package com.hysteryale.repository;

import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import org.apache.poi.hssf.record.Margin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Calendar;
import java.util.List;

public interface MarginAnalystDataRepository extends JpaRepository<MarginAnalystData, Integer> {
    @Query("SELECT m FROM MarginAnalystData m WHERE m.modelCode = ?1 AND m.currency.currency = ?2 AND m.monthYear = ?3")
    public List<MarginAnalystData> getMarginDataForAnalysis(String modelCode, String currency, Calendar monthYear);

    @Query("SELECT DISTINCT m.modelCode FROM MarginAnalystData m WHERE m.monthYear = ?1 AND m.currency.currency = ?2")
    public List<String> getModelCodesByMonthYearAndCurrency(Calendar monthYear, String currency);
}
