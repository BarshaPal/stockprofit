package com.example.stock1.Scheduler;

import com.example.stock1.service.StockRateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class StockDataScheduler {

    private final StockRateService stockDataService;

    public StockDataScheduler(StockRateService stockDataService) {
        this.stockDataService = stockDataService;
    }

    @Scheduled(cron = "0 0 0 * * ?")  // Runs daily at midnight
    public void scheduledStockDataFetch() {
        LocalDate today = LocalDate.now();
        List<String> companies = List.of("SAP", "GOOGLE");

        for (String company : companies) {
            stockDataService.fetchAndStoreStockData(company, today);
        }
    }
}

