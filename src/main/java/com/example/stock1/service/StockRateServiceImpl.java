package com.example.stock1.service;

import com.example.stock1.DTO.StockRateDTO;
import com.example.stock1.data.StockDataRepository;
import com.example.stock1.entity.StockRateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class StockRateServiceImpl implements StockRateService {

    @Autowired

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy"); // Adjusted format
    private final RestTemplate restTemplate;
    private final StockDataRepository stockDataRepository;

    @Value("${google.script.api.url}")  // Store the URL in application.properties
    private String apiUrl;

    public StockRateServiceImpl(RestTemplate restTemplate, StockDataRepository stockDataRepository) {
        this.restTemplate = restTemplate;
        this.stockDataRepository = stockDataRepository;
    }


    private boolean isValidStock(StockRateEntity stock) {
        return stock != null && stock.getCompany() != null && stock.getDate() != null
                && stock.getOpen() != null && stock.getClose() != null;
    }

    public void fetchAndStoreAllStockData() {
        ResponseEntity<StockRateEntity[]> response = restTemplate.getForEntity(apiUrl, StockRateEntity[].class);
        System.out.println(response.getBody());

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<StockRateEntity> stocks = Arrays.asList(response.getBody());

            for (StockRateEntity stock : stocks) {
                if (!isValidStock(stock)) continue; // Skip invalid rows

                stockDataRepository.findByCompanyAndDate(stock.getCompany(), stock.getDate())
                        .ifPresentOrElse(existing -> {
                            System.out.println("Data already exists for " + stock.getCompany() + " on " + stock.getDate());
                        }, () -> {
                            stockDataRepository.save(stock);
                            System.out.println("Saved: " + stock);
                        });
            }
        }
    }

    @Override

    public List<StockRateDTO> fetchAndStoreStockData(String company, LocalDate date) {
        String url = String.format("%s?company=%s&date=%s", apiUrl, company, date);

        ResponseEntity<StockRateEntity[]> response = restTemplate.getForEntity(url, StockRateEntity[].class);

        List<StockRateDTO> savedStocks = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            for (StockRateEntity stock : response.getBody()) {

                StockRateDTO stockDTO = new StockRateDTO(
                        stock.getCompany(),
                        stock.getCurrency(),
                        stock.getDate(),
                        stock.getOpen(),
                        stock.getClose()
                );

                savedStocks.add(stockDTO);
                System.out.println("Saved: " + stockDTO);
            }
        }

        return savedStocks; // Return list of DTOs
    }




}
