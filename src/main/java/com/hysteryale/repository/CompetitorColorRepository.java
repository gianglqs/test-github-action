package com.hysteryale.repository;

import com.hysteryale.model.competitor.CompetitorColor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CompetitorColorRepository extends JpaRepository<CompetitorColor, Integer> {

    @Query("SELECT c FROM CompetitorColor c WHERE c.competitorName = ?1")
    Optional<CompetitorColor> getCompetitorColor(String competitorName);

    @Query("SELECT c FROM CompetitorColor c WHERE c.competitorName LIKE CONCAT ('%', ?1, '%')")
    Page<CompetitorColor> searchCompetitorColor(String search, Pageable pageable);
}
