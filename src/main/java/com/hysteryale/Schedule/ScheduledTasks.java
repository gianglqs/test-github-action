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

    final BookingOrderService bookingOrderService;
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public ScheduledTasks(BookingOrderService bookingOrderService) {
        this.bookingOrderService = bookingOrderService;
    }

    /**
     * To import booking order from Excel files at midnight everyday
     */

    //This is for testing only
    //@Scheduled(fixedDelay = 1000)

    //@Scheduled(cron = "0 0 0 * * *")
    public void autoUpdateBookingOrder() {
        log.info("Start import booking orders at {}", dateFormat.format(new Date()));
        try {
            bookingOrderService.importOrder();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("Import booking orders finished at {}", dateFormat.format(new Date()));
    }
}
