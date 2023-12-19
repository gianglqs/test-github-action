package com.hysteryale.repository_h2;

import com.hysteryale.model_h2.IMMarginAnalystData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMMarginAnalystDataRepository extends JpaRepository<IMMarginAnalystData, Integer> {
    @Query("SELECT m from IMMarginAnalystData m WHERE m.modelCode = :model_code AND m.currency = :currency AND m.type = :type AND m.fileUUID = :fileuuid")
    List<IMMarginAnalystData> getIMMarginAnalystData(@Param("model_code") String modelCode, @Param("currency") String strCurrency,
                                                     @Param("fileuuid") String fileUUID, @Param("type") Integer type);

    @Query("SELECT m from IMMarginAnalystData m WHERE m.modelCode = :model_code AND m.orderNumber = :order_number AND m.currency = :currency AND m.type = :type AND m.fileUUID = :fileuuid")
    List<IMMarginAnalystData> getUSPlantIMMarginAnalystData(@Param("model_code") String modelCode, @Param("order_number") String orderNumber,
                                                            @Param("currency") String currency, @Param("type") Integer type,
                                                            @Param("fileuuid") String fileUUID);

    @Query("SELECT DISTINCT m.modelCode FROM IMMarginAnalystData m WHERE m.fileUUID = ?1")
    List<String> getModelCodesByFileUUID(String fileUUID);

    @Query("SELECT m FROM IMMarginAnalystData m WHERE m.fileUUID = ?1")
    List<IMMarginAnalystData> getMargin(String fileUUID);

}
