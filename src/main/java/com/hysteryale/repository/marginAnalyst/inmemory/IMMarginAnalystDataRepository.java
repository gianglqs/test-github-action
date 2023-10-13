package com.hysteryale.repository.marginAnalyst.inmemory;

import com.hysteryale.model.marginAnalyst.inmemory.IMMarginAnalystData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMMarginAnalystDataRepository extends JpaRepository<IMMarginAnalystData, Integer> {
}
