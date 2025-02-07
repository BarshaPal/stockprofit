package com.example.stock1;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
// Class
public class ExchangeRateSchedular {

    // Method
    // To trigger the scheduler every one minute
    // between 19:00 PM to 19:59 PM
    @Scheduled(cron = "0 49 13 * * ?")
    public void scheduleTask()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd-MM-yyyy HH:mm:ss.SSS");

        String strDate = dateFormat.format(new Date());

        System.out.println(
            "Cron job Scheduler: Job running at - "
            + strDate);
    }
}