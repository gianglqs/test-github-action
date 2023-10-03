package com.hysteryale.repository;

import com.hysteryale.model.APACSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface APACSerialRepository extends JpaRepository<APACSerial, String> {
    @Query("SELECT DISTINCT a.plant FROM APACSerial a ORDER BY a.plant")
    List<String> getPlants();
    @Query("SELECT a.model FROM APACSerial a ORDER BY a.model")
    List<String> getModels();
}
