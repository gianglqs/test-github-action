package com.hysteryale.controller;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.service.BookingOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BookingOrderController {
  
    @Resource
    BookingOrderService bookingOrderService;

    /**
     *  {for testing only -> will be removed} Import BookingOrder from Excel file
     */
    @PostMapping(path = "/order/import")
    public void importOrder() throws FileNotFoundException, IllegalAccessException {
        bookingOrderService.importOrder();
    }

    /**
     * Get list of BookingOrder from database
     * @return Map contains list of BookingOrder
     */
    @GetMapping(path = "/bookingOrder/getAll")
    public Map<String, List<BookingOrder>> getAllOrder() {
        Map<String, List<BookingOrder>> bookingOrderListMap = new HashMap<>();
        bookingOrderListMap.put("bookingOrderList", bookingOrderService.getAllBookingOrders());

        return bookingOrderListMap;
    }

}
