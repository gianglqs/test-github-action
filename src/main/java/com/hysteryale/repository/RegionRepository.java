package com.hysteryale.repository;

import com.hysteryale.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, String> {
}
