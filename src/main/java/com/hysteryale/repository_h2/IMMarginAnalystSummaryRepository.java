package com.hysteryale.repository_h2;

import com.hysteryale.model_h2.IMMarginAnalystSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IMMarginAnalystSummaryRepository extends JpaRepository<IMMarginAnalystSummary, Integer> {
    @Query("SELECT m FROM IMMarginAnalystSummary m WHERE m.modelCode = ?1 and m.currency = ?2 and m.fileUUID = ?3")
    List<IMMarginAnalystSummary> getIMMarginAnalystSummary(String modelCode, String currency, String fileUUID);

    @Query("SELECT m FROM IMMarginAnalystSummary m WHERE m.modelCode = ?1 and m.currency = ?2 and YEAR(m.monthYear) = ?3 AND MONTH(m.monthYear) = ?4 AND m.orderNumber = ?5")
    List<IMMarginAnalystSummary> getIMMarginAnalystSummaryMonthlyByMonthYear(String modelCode, String currency, int year, int month, String orderNumber);

    @Query("SELECT m FROM IMMarginAnalystSummary m WHERE m.modelCode = ?1 and m.currency = ?2 and YEAR(m.monthYear) = ?3 AND MONTH(m.monthYear) = 1 AND DAY(m.monthYear) = 28 AND m.orderNumber = ?4")
    List<IMMarginAnalystSummary> getIMMarginAnalystSummaryAnnuallyByMonthYear(String modelCode, String currency, int year, String orderNumber);
}
