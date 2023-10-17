package com.hysteryale.repository;

import com.hysteryale.model.marginAnalyst.MarginAnalystMacro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Calendar;
import java.util.Optional;

public interface MarginAnalystMacroRepository extends JpaRepository<MarginAnalystMacro, Integer> {

    @Query("SELECT m FROM MarginAnalystMacro m WHERE m.modelCode = ?1 AND m.partNumber = ?2 AND m.currency.currency = ?3 AND m.monthYear = ?4")
    Optional<MarginAnalystMacro> getMarginAnalystMacro(String modelCode, String partNumber, String currency, Calendar monthYear);

}
