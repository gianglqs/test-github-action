package com.hysteryale.repository;

import com.hysteryale.model.Currencies;
import com.hysteryale.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.TemporalType;
import java.util.Calendar;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    @Query("SELECT e FROM ExchangeRate e WHERE e.from.id = ?1 AND e.to.id = ?2 AND e.date = ?3")
    public Optional<ExchangeRate> getExchangeRateByFromToCurrencyAndDate(int fromId, int toId, Calendar date);
}
