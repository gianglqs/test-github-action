package com.hysteryale.repository;

import com.hysteryale.model.BookingOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingOrderRepository extends JpaRepository<BookingOrder, String> {
}
