package com.hysteryale.controller;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.service.BookingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
public class OrderController {
  
    @Resource
    BookingOrderService bookingOrderService;

    public OrderController(BookingOrderService bookingOrderService) {
        this.bookingOrderService = bookingOrderService;
    }

    @PostMapping(path = "/order/import")
    public void importOrder() throws FileNotFoundException, IllegalAccessException {
        bookingOrderService.importOrder();
    }
    @GetMapping(path = "/order/getAll")
    public List<BookingOrder> getAllOrder() {
        return bookingOrderService.getAllOrders();
    }

}
