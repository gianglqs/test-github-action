package com.hysteryale.repository;

import com.hysteryale.model.APACSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface APACSerialRepository extends JpaRepository<APACSerial, String> {
    @Query("SELECT DISTINCT a.plant FROM APACSerial a ORDER BY a.plant")
    List<String> getPlants();
    @Query("SELECT a.model FROM APACSerial a ORDER BY a.model")
    List<String> getModels();

    @Query("SELECT a FROM APACSerial a WHERE a.model = ?1")
    Optional<APACSerial> findByModel(String model);

    @Query("SELECT a FROM APACSerial a WHERE a.model = ?1 AND a.series = ?2")
    Optional<APACSerial> findByModelAndSeries(String model, String series);

    @Query("SELECT DISTINCT a.plant FROM APACSerial a WHERE SUBSTRING(a.series, 2, 3) = ?1")
    List<String> findPlantsByMetaSeries(String series);

    @Query("SELECT a.plant FROM APACSerial a WHERE a.model = ?1 AND SUBSTRING(a.series, 2, 3) = ?2")
    Optional<String> findByModelAndMetaSeries(String model, String series);
}
