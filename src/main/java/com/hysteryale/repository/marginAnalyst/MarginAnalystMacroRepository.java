package com.hysteryale.repository.marginAnalyst;

import com.hysteryale.model.marginAnalyst.MarginAnalystMacro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface MarginAnalystMacroRepository extends JpaRepository<MarginAnalystMacro, Integer> {

    @Query("SELECT m FROM MarginAnalystMacro m WHERE m.modelCode = ?1 AND m.partNumber = ?2 AND m.currency.currency = ?3 AND m.monthYear = ?4")
    Optional<MarginAnalystMacro> getMarginAnalystMacroByMonthYear(String modelCode, String partNumber, String currency, Calendar monthYear);

    @Query("SELECT m FROM MarginAnalystMacro m WHERE m.modelCode = ?1 AND m.partNumber = ?2 AND m.currency.currency = ?3")
    Optional<MarginAnalystMacro> getMarginAnalystMacro(String modelCode, String partNumber, String currency);

    @Query("SELECT m FROM MarginAnalystMacro m WHERE m.modelCode LIKE CONCAT ('%', ?1, '%') AND m.partNumber = ?2 AND m.currency.currency = ?3 AND m.plant = ?4 AND m.monthYear = ?5")
    List<MarginAnalystMacro> getMarginAnalystMacroByPlant(String modelCode, String partNumber, String currency, String plant, Calendar monthYear);


}
