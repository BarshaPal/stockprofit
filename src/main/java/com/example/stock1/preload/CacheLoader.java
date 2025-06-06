package com.example.stock1.preload;

import com.example.stock1.service.ExchangeRateService;
import com.example.stock1.service.StockRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class CacheLoader {

    @Autowired
    private StockRateService stockRateService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @EventListener(ApplicationReadyEvent.class)
    public void loadDataIntoRedis() {
        stockRateService.clearOldRedisStockKeys();
        stockRateService.preloadStockRatesToRedis();
        exchangeRateService.preloadExchangeRatesToRedis();
    }
}
