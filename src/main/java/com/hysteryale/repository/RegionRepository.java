package com.hysteryale.repository;

import com.hysteryale.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, String> {
    @Query("SELECT r FROM Region r WHERE r.id_region = ?1 ")
    public Optional<Region> findByRegionId(String id_region);
}
