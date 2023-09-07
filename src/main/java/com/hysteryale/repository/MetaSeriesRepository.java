package com.hysteryale.repository;


import com.hysteryale.model.MetaSeries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaSeriesRepository extends JpaRepository<MetaSeries, String> {
}
