package com.hysteryale.repository;

import com.hysteryale.model.UnitFlags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitFlagsRepository extends JpaRepository<UnitFlags, String> {
    @Query("SELECT u FROM UnitFlags u WHERE u.readyForDistribution = ?1")
    public List<UnitFlags> getUnitFlagsByReadyState(String readyState);
}
