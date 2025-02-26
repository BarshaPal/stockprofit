package com.example.stock1. Scheduler;

import com.example.stock1.service.ForexService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForexScheduler {
    private final ForexService forexService;

    // Runs every day at 10:00 AM
    @Scheduled(cron = "0 0 12 * * ?")
    public void scheduledForexUpdate() {
        forexService.fetchAndStoreForexRates();
    }
}
