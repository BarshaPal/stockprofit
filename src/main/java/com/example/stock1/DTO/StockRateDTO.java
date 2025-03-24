package com.example.stock1.DTO;

import com.example.stock1.entity.StockRateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor
public class StockRateDTO {
    private String company;
    private String currency;
    private LocalDate date;
    private BigDecimal open;
    private BigDecimal close;

    // Constructor
    public StockRateDTO(StockRateEntity stock) {
        this.company = stock.getCompany();
        this.currency = stock.getCurrency();
        this.date = stock.getDate(); // Convert LocalDate to String
        this.open = stock.getOpen();
        this.close = stock.getClose();
    }

    // Getters & Setters (if needed)
}

