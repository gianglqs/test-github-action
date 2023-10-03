package com.hysteryale.repository;


import com.hysteryale.model.MetaSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MetaSeriesRepository extends JpaRepository<MetaSeries, String> {
    @Query("SELECT m.id FROM MetaSeries m ORDER BY m.id")
    List<String> getSeries();

    @Query("SELECT DISTINCT m.clazz FROM MetaSeries m ORDER BY m.clazz")
    List<String> getClasses();

    @Query("SELECT DISTINCT m.segment1 FROM MetaSeries m ORDER BY m.segment1")
    List<String> getMetaSeriesSegments();
}
