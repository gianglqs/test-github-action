package com.hysteryale.repository;

import com.hysteryale.model.Shipment;
import com.hysteryale.model.competitor.CompetitorPricing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {

    @Query("SELECT c FROM Shipment c WHERE " +
            "((:regions) IS Null OR c.region IN :regions )" +
            " AND ((:plants) IS NULL OR c.plant IN (:plants))" +
            " AND ((:metaSeries) IS NULL OR SUBSTRING(c.series, 1,3) IN (:metaSeries))" +
            " AND ((:classes) IS NULL OR c.clazz IN (:classes))" +
            " AND ((:models) IS NULL OR c.model IN (:models))" +
            " AND ((:segments) IS NULL OR c.segment IN (:segments))" +
            " AND ((:fromDate) IS NULL OR c.date >= (:fromDate))" +
            " AND ((:toDate) IS NULL OR c.date <= (:toDate))" +
            " AND ((:chineseBrand) IS NULL OR c.chineseBrand = (:chineseBrand))")
    public List<CompetitorPricing> findShipmentByFilterForTable(@Param("orderNo") String orderNo,
                                                                @Param("regions") List<String> regions,
                                                                @Param("plants") List<String> plants,
                                                                @Param("metaSeries") List<String> metaSeries,
                                                                @Param("classes") List<String> classes,
                                                                @Param("models") List<String> models,
                                                                @Param("segments") List<String> segments,
                                                                @Param("fromDate") String fromDate,
                                                                @Param("toDate") String toDate,
                                                                @Param("pageable") Pageable pageable);

}
