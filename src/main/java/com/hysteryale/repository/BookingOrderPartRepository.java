package com.hysteryale.repository;

import com.hysteryale.model.BookingOrderPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface BookingOrderPartRepository extends JpaRepository<BookingOrderPart, Integer > {

    @Query(value = "SELECT DISTINCT p FROM BookingOrderPart p WHERE p.orderNo = ?1")
    Set<BookingOrderPart> findByOrderNo(String orderNo);
}