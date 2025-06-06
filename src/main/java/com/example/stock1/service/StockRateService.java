package com.example.stock1.service;

import com.example.stock1.DTO.StockRateDTO;
import com.example.stock1.entity.StockRateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StockRateService {

    List<StockRateDTO> fetchAndStoreStockData(String company, String date);
    void preloadStockRatesToRedis();
    void fetchAndStoreAllStockData();
    void clearOldRedisStockKeys();
//    StockRateEntity getStockRate(String symbol, LocalDate date);
    List<StockRateDTO> fetchAndStoreStockDataRedis(String company, String date);

    //    @Override
    //    public StockRateEntity getStockRate(String symbol, LocalDate date) {
    //        String redisKey = "stock:" + symbol + ":" + date;
    //        StockRateEntity cachedStock = (StockRateEntity) redisTemplate.opsForValue().get(redisKey);
    //
    //        if (cachedStock != null) {
    //                        System.out.println("Stock rate fetched from Redis for {} on {}"+ symbol+" "+ date);
    //        }
    //        return cachedStock;
    //    }
    List<StockRateDTO> fetchAndStoreStockData(String company, LocalDate date);
}
