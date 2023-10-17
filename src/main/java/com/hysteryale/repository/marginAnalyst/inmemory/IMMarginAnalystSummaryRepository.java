package com.hysteryale.repository.marginAnalyst.inmemory;

import com.hysteryale.model.marginAnalyst.inmemory.IMMarginAnalystSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMMarginAnalystSummaryRepository extends JpaRepository<IMMarginAnalystSummary, Integer> {
}
