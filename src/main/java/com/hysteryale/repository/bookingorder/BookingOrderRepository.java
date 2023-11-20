package com.hysteryale.repository.bookingorder;

import com.hysteryale.model.BookingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingOrderRepository extends JpaRepository<BookingOrder, String> {

    @Query("SELECT DISTINCT b.dealerName FROM BookingOrder b ORDER BY b.dealerName")
    public List<String> getAllDealerName();

    @Query("SELECT DISTINCT b.model FROM BookingOrder b ORDER BY b.model ASC ")
    List<String> getAllModel();

    @Query("SELECT b FROM BookingOrder b WHERE b.orderNo = ?1")
    Optional<BookingOrder> getBookingOrderByOrderNo(String orderNo);

    // it is not including condition on currency due to missing currency data
    @Query(value = "SELECT * FROM booking_order WHERE model = ?1 LIMIT 1", nativeQuery = true)
    Optional<BookingOrder> getDistinctBookingOrderByModelCode(String modelCode);
}
