package com.example.stock1.controller;

import com.example.stock1.entity.ExchangeRateEntity;
import com.example.stock1.service.ExchangeRateService;
import com.example.stock1.service.ForexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private ForexService forexService;


@GetMapping("/get")
public ResponseEntity<String> getPDF() {
    String message = "Hello";
    return ResponseEntity.ok(message);
}


    @GetMapping("/currency/{currency}")
    public ResponseEntity<?> getCurrencyRates(@PathVariable String currency) {
        try {
            List<Map<String, Object>> data = exchangeRateService.getRatesByCurrency(currency.toLowerCase());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Invalid currency or server error.");
        }
    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadPDF(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
        }

        try {
            exchangeRateService.processPDF(file);
            return ResponseEntity.ok("Excel data uploaded and saved to database successfully!");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }
    @GetMapping("/{date}")
    public ExchangeRateEntity getExchangeRate(@PathVariable String date) throws Exception {

        return exchangeRateService.getExchangeRateByDate(date);
    }
    @GetMapping("/currentRate")
    public ExchangeRateEntity getCurretnExchangeRate() throws Exception {
        return exchangeRateService.exchangeRateofCurrentDate();
    }


    @GetMapping("/forex")
    public void getForexCard() throws Exception {
         forexService.fetchAndStoreForexRates();
    }

    @GetMapping("/PurchaseSellingRate/{date}")
    public List<ExchangeRateEntity> getPurchaseSellingRate(@PathVariable String date) throws Exception {
        return exchangeRateService.purchasesellingRate(date);
    }



    @GetMapping("/stock_price")
    public Double getStockPrice(@RequestParam String symbol) {
        String url = "http://localhost:5000/get_stock_price?symbol=" + symbol;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("price")) {
            return (Double) response.getBody().get("price");
        }
        return null; // Handle invalid responses
    }
}