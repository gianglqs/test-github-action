package com.hysteryale.repository;

import com.hysteryale.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    @Query("SELECT c FROM Currency c WHERE c.currencyName = ?1")
    public Optional<Currency> getCurrenciesByName(String currencyName);
}
