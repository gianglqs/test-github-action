package com.hysteryale.Schedule;

import com.hysteryale.service.BookingOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {
    @Autowired
    BookingOrderService bookingOrderService;
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * Auto-implemented function for importing BookingOrder
     */
    @Scheduled(fixedRate = 15000)
    public void autoUpdateBookingOrder() throws FileNotFoundException, IllegalAccessException {
        log.info("The time is now {}", dateFormat.format(new Date()));
        bookingOrderService.importOrder();
    }
}
