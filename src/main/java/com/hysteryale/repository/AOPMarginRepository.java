package com.hysteryale.repository;

import com.hysteryale.model.AOPMargin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Set;

public interface AOPMarginRepository extends JpaRepository<AOPMargin, String> {
    @Query("SELECT DISTINCT aopMargin FROM AOPMargin aopMargin WHERE aopMargin.year= :year")
    Set< AOPMargin> findByYear(@Param("year") int year);
}
