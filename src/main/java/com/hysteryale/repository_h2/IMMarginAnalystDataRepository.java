package com.hysteryale.repository_h2;

import com.hysteryale.model_h2.IMMarginAnalystData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMMarginAnalystDataRepository extends JpaRepository<IMMarginAnalystData, Integer> {
    @Query(value = "select m.id, m.plant, m.model_code, m.price_list_region, m.clazz, m.option_code, m.std_otp, " +
            "m.description, m.list_price, m.margin_aop, m. month_year, m.currency, m.manufacturingcost, m.dealer, m.dealer_net, m.fileuuid, m.ordernumber, m.issped " +
            "from (select *, row_number() over (partition by model_code, option_code order by option_code asc) rn from immarginanalystdata) m \n" +
            "where rn = 1 and model_code = :model_code and currency = :currency and fileuuid = :fileuuid", nativeQuery = true)
    List<IMMarginAnalystData> getIMMarginAnalystData(@Param("model_code") String modelCode, @Param("currency") String strCurrency, @Param("fileuuid") String fileUUID);

    @Query("SELECT m FROM IMMarginAnalystData m WHERE m.modelCode = ?1 AND m.orderNumber = ?2 AND m.currency = ?3")
    List<IMMarginAnalystData> getEUPlantIMMarginAnalystData(String modelCode, String orderNumber, String currency);

    @Query("SELECT DISTINCT m.modelCode FROM IMMarginAnalystData m WHERE m.fileUUID = ?1")
    List<String> getModelCodesByFileUUID(String fileUUID);


}
