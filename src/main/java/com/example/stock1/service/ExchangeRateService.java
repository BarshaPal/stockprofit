package com.example.stock1.service;

import com.example.stock1.entity.ExchangeRateEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ExchangeRateService {
    void preloadExchangeRatesToRedis();
    List<Map<String, Object>> getRatesByCurrency(String currency);
    String processPDF(MultipartFile file) throws IOException;
    ExchangeRateEntity getExchangeRateByDate(String date);
    ExchangeRateEntity exchangeRateofCurrentDate();
    List<ExchangeRateEntity> purchasesellingRate(String date);

}
