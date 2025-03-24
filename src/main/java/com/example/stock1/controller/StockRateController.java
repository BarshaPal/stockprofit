package com.example.stock1.controller;

import com.example.stock1.DTO.StockRateDTO;
import com.example.stock1.entity.ExchangeRateEntity;
import com.example.stock1.entity.StockRateEntity;
import com.example.stock1.service.ExchangeRateService;
import com.example.stock1.service.StockRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/stock")
public class StockRateController {

    @Autowired
    private StockRateService stockrateService;
@GetMapping("/get")
public ResponseEntity<String> getPDF() {
    String message = "Hello";
    return ResponseEntity.ok(message);
}

    @GetMapping("/stock_perdate&company")
    public ResponseEntity<?> getStockData(
            @RequestParam String company,
            @RequestParam String date) {

        try {
            LocalDate parsedDate = LocalDate.parse(date);

            // Fetch and store stock data
            List<StockRateDTO> savedStocks = stockrateService.fetchAndStoreStockData(company.toUpperCase(), parsedDate);

            if (savedStocks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No stock data found or stored for " + company + " on " + parsedDate);
            }

            return ResponseEntity.ok(savedStocks);  // Return saved stock data

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid date format. Use 'YYYY-MM-DD'. Error: " + e.getMessage());
        }
    }



    @GetMapping("/fetch-all")
    public ResponseEntity<String> fetchAllStockData() {
        stockrateService.fetchAndStoreAllStockData();
        return ResponseEntity.ok("Stock data fetched and stored successfully.");
    }
}