package com.hysteryale.repository;

import com.hysteryale.model.Currencies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CurrenciesRepository extends JpaRepository<Currencies, Integer> {
    @Query("SELECT c FROM Currencies c WHERE c.currency = ?1")
    public Optional<Currencies> getCurrenciesByName(String currencyName);
}
