package com.hysteryale.repository.bookingorder;

import com.hysteryale.model.BookingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingOrderRepository extends JpaRepository<BookingOrder, String> {

    @Query("SELECT DISTINCT b.dealerName FROM BookingOrder b")
    public List<String> getAllDealerName();

    @Query("SELECT DISTINCT b.apacSerial.model FROM BookingOrder b") // dang bi loi
    List<String> getAllModel();
}
