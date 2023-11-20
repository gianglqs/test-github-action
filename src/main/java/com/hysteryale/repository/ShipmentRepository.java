package com.hysteryale.repository;

import com.hysteryale.model.Shipment;
import com.hysteryale.model.competitor.CompetitorPricing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {

    @Query("SELECT c FROM Shipment c WHERE " +
            "((:orderNo) IS Null OR c.orderNo = :orderNo )" +
            " AND ((:regions) IS Null OR c.region.region IN (:regions) )" +
            " AND ((:plants) IS NULL OR c.productDimension.plant IN (:plants))" +
            " AND ((:metaSeries) IS NULL OR SUBSTRING(c.series, 2,3) IN (:metaSeries))" +
            " AND ((:classes) IS NULL OR c.productDimension.clazz IN (:classes))" +
            " AND ((:models) IS NULL OR c.productDimension.model IN (:models))" +
            " AND ((:segments) IS NULL OR c.productDimension.segment IN (:segments))" +
            " AND ((:dealerName) IS NULL OR c.dealerName IN (:dealerName))" +
            " AND ((:AOPMarginPercentage) IS NULL OR " +
            "   (:AOPMarginPercentage = 'Above AOP Margin %' AND c.AOPMarginPercentage < c.marginPercentageAfterSurCharge) OR" +
            "   (:AOPMarginPercentage = 'Below AOP Margin %' AND c.AOPMarginPercentage >= c.marginPercentageAfterSurCharge))" +
            " AND ((:marginPercentageAfterSurCharge) IS NULL OR " +
            "   (:marginPercentageAfterSurCharge = '-ve Margin %' AND c.marginPercentageAfterSurCharge < 0) OR" +
            "   (:marginPercentageAfterSurCharge = '<10% Margin' AND c.marginPercentageAfterSurCharge < 0.1) OR" +
            "   (:marginPercentageAfterSurCharge = '<20% Margin' AND c.marginPercentageAfterSurCharge < 0.2) OR" +
            "   (:marginPercentageAfterSurCharge = '<30% Margin' AND c.marginPercentageAfterSurCharge < 0.3) OR" +
            "   (:marginPercentageAfterSurCharge = '>=30% Margin' AND c.marginPercentageAfterSurCharge >= 0.3))" +
            " AND ((:dealerName) IS NULL OR c.dealerName IN (:dealerName))" +
            " AND (cast(:fromDate as date ) IS NULL OR c.date >= :fromDate)" +
            " AND (cast(:toDate as date) IS NULL OR c.date <= :toDate)"
    )
    List<Shipment> findShipmentByFilterForTable(@Param("orderNo") Object orderNo,
                                                @Param("regions") Object regions,
                                                @Param("plants") Object plants,
                                                @Param("metaSeries") Object metaSeries,
                                                @Param("classes") Object classes,
                                                @Param("models") Object models,
                                                @Param("segments") Object segments,
                                                @Param("dealerName") Object dealerName,
                                                @Param("AOPMarginPercentage") Object AOPMarginPercentage,
                                                @Param("marginPercentageAfterSurCharge") Object marginPercentageAfterSurCharge,
                                                @Param("fromDate") Date fromDate,
                                                @Param("toDate") Date toDate,
                                                @Param("pageable") Pageable pageable);


    @Query("SELECT COUNT(c) FROM Shipment c WHERE " +
            "((:orderNo) IS Null OR c.orderNo = :orderNo )" +
            " AND ((:regions) IS Null OR c.region.region IN (:regions) )" +
            " AND ((:plants) IS NULL OR c.productDimension.plant IN (:plants))" +
            " AND ((:metaSeries) IS NULL OR SUBSTRING(c.series, 2,3) IN (:metaSeries))" +
            " AND ((:classes) IS NULL OR c.productDimension.clazz IN (:classes))" +
            " AND ((:models) IS NULL OR c.productDimension.model IN (:models))" +
            " AND ((:segments) IS NULL OR c.productDimension.segment IN (:segments))" +
            " AND ((:dealerName) IS NULL OR c.dealerName IN (:dealerName))" +
            " AND ((:AOPMarginPercentage) IS NULL OR " +
            "   (:AOPMarginPercentage = 'Above AOP Margin %' AND c.AOPMarginPercentage < c.marginPercentageAfterSurCharge) OR" +
            "   (:AOPMarginPercentage = 'Below AOP Margin %' AND c.AOPMarginPercentage >= c.marginPercentageAfterSurCharge))" +
            " AND ((:marginPercentageAfterSurCharge) IS NULL OR " +
            "   (:marginPercentageAfterSurCharge = '-ve Margin %' AND c.marginPercentageAfterSurCharge < 0) OR" +
            "   (:marginPercentageAfterSurCharge = '<10% Margin' AND c.marginPercentageAfterSurCharge < 0.1) OR" +
            "   (:marginPercentageAfterSurCharge = '<20% Margin' AND c.marginPercentageAfterSurCharge < 0.2) OR" +
            "   (:marginPercentageAfterSurCharge = '<30% Margin' AND c.marginPercentageAfterSurCharge < 0.3) OR" +
            "   (:marginPercentageAfterSurCharge = '>=30% Margin' AND c.marginPercentageAfterSurCharge >= 0.3))" +
            " AND ((:dealerName) IS NULL OR c.dealerName IN (:dealerName))" +
            " AND (cast(:fromDate as date) IS NULL OR c.date >= (:fromDate))" +
            " AND (cast(:toDate as date) IS NULL OR c.date <= (:toDate))"
    )
    int getCount(@Param("orderNo") Object orderNo,
                                     @Param("regions") Object regions,
                                     @Param("plants") Object plants,
                                     @Param("metaSeries") Object metaSeries,
                                     @Param("classes") Object classes,
                                     @Param("models") Object models,
                                     @Param("segments") Object segments,
                                     @Param("dealerName") Object dealerName,
                                     @Param("AOPMarginPercentage") Object AOPMarginPercentage,
                                     @Param("marginPercentageAfterSurCharge") Object marginPercentageAfterSurCharge,
                                     @Param("fromDate") Date fromDate,
                                     @Param("toDate") Date toDate);

    @Query("SELECT s.dealerName from Shipment s ")
    List<String> findAllClass();
}
