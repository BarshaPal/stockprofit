package com.example.stock1.controller;

import com.example.stock1.entity.ExchangeRateEntity;
import com.example.stock1.service.ExchangeRateService;
import com.example.stock1.service.StockRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    @PostMapping("/upload")
    public ResponseEntity<String> uploadPDF(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
        }

        try {
            stockrateService.processExcel(file);
            return ResponseEntity.ok("Excel data uploaded and saved to database successfully!");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }

//    @GetMapping("/{date}")
//    public ExchangeRateEntity getExchangeRate(@PathVariable String date) throws Exception {
////        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
////        Date parsedDate = dateFormat.parse(date);
////        return exchangeRateService.getExchangeRateByDate(date);
//    }
}