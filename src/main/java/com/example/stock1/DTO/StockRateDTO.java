package com.example.stock1.DTO;

import com.example.stock1.entity.StockRateEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockRateDTO {
    private String company;
    private String currency;
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private String date;
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

