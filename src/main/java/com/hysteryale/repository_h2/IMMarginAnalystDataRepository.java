package com.hysteryale.repository_h2;

import com.hysteryale.model_h2.IMMarginAnalystData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IMMarginAnalystDataRepository extends JpaRepository<IMMarginAnalystData, Integer> {
    @Query("SELECT m FROM IMMarginAnalystData m WHERE m.modelCode = ?1 AND m.currency = ?2 AND m.fileUUID = ?3 AND m.orderNumber = ?4")
    List<IMMarginAnalystData> getIMMarginAnalystData(String modelCode, String strCurrency, String fileUUID, String orderNumber);

    @Query("SELECT DISTINCT m.modelCode FROM IMMarginAnalystData m WHERE m.fileUUID = ?1 AND m.orderNumber = ?2")
    List<String> getModelCodesByFileUUID(String fileUUID, String orderNumber);

    @Query("SELECT DISTINCT m.orderNumber FROM IMMarginAnalystData m WHERE m.fileUUID = ?1")
    List<String> getOrderNumber(String fileUUID);

}
