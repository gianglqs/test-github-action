package com.hysteryale.repository;

import com.hysteryale.model.CostUplift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Calendar;
import java.util.Optional;

public interface CostUpliftRepository extends JpaRepository<CostUplift, Integer> {
    @Query("SELECT c FROM CostUplift c WHERE c.plant = ?1 AND c.date = ?2")
    public Optional<CostUplift> getCostUpliftByPlantAndDate(String plant, Calendar date);
}
