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

    List<StockRateDTO> fetchAndStoreStockData(String company, LocalDate date);

    void fetchAndStoreAllStockData();
}
