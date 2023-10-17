package com.hysteryale.repository_h2;

import com.hysteryale.model_h2.IMMarginAnalystData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IMMarginAnalystDataRepository extends JpaRepository<IMMarginAnalystData, Integer> {
    @Query("SELECT m FROM IMMarginAnalystData m WHERE m.modelCode = ?1 AND m.currency.currency = ?2 AND m.plant = ?3")
    List<IMMarginAnalystData> getIMMarginAnalystData(String modelCode, String strCurrency, String plant);
}
