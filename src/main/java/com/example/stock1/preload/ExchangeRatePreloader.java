package com.example.stock1.preload;

import com.example.stock1.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRatePreloader implements CommandLineRunner {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Override
    public void run(String... args) {
        exchangeRateService.preloadExchangeRatesToRedis();
    }
}
