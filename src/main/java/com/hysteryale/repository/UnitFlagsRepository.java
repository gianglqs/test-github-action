package com.hysteryale.repository;

import com.hysteryale.model.UnitFlags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitFlagsRepository extends JpaRepository<UnitFlags, String> {
}
