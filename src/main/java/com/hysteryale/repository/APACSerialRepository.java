package com.hysteryale.repository;

import com.hysteryale.model.APACSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface APACSerialRepository extends JpaRepository<APACSerial, String> {
    @Query("SELECT DISTINCT a.plant FROM APACSerial a")
    List<String> getPlants();

    @Query("SELECT a FROM APACSerial a WHERE a.metaSeries = ?1")
    Optional<APACSerial> findByMetaSeries(String metaSeries);

    @Query("SELECT DISTINCT a.metaSeries FROM APACSerial a")
    List<String> getAllMetaSeries();

    @Query("SELECT DISTINCT a.clazz FROM APACSerial a")
    List<String> getAllClass();
}
