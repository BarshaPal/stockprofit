package com.example.stock1.controller;

import com.example.stock1.entity.ExchangeRateEntity;
import com.example.stock1.service.ExchangeRateService;
import com.example.stock1.service.ForexService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    @Qualifier("exchangeRateRedisTemplate")
    private RedisTemplate<String, ExchangeRateEntity> redisTemplate;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private ForexService forexService;


@GetMapping("/get")
public ResponseEntity<String> getPDF() {
    String message = "Hello";
    return ResponseEntity.ok(message);
}


//@GetMapping("/redis/save")
//public String saveValue() {
//    redisTemplate.opsForValue().set("test-key", "Hello SAP BTP Redis!");
//    return "Saved 'test-key' to Redis";
//}
//
//    @GetMapping("/redis/get")
//    public String getValue() {
//        Object value = redisTemplate.opsForValue().get("test-key");
//        return value == null ? "Key not found" : "Value for 'test-key': " + value;
//    }
        @PostMapping("/csv/upload")
        public ResponseEntity<List<Map<String, String>>> uploadCsv(@RequestParam("file") MultipartFile file) {
            List<Map<String, String>> records = new ArrayList<>();
            List<Map<String, String>> errorList = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                 CSVParser parser = new CSVParser(reader,CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
                for (CSVRecord csvRecord : parser) {
                    Map<String, String> row = new HashMap<>();
                    for (String header : parser.getHeaderMap().keySet()) {
                        String value = csvRecord.get(header);
                        if (value == null || value.trim().isEmpty()) {
                            Map<String, String> errorMap = new HashMap<>();
                            errorMap.put("error", "Field '" + header + "' is empty");
                            errorMap.put("record", String.valueOf(csvRecord.getRecordNumber()));
                            errorList.add(errorMap);
                    }
                        row.put(header, csvRecord.get(header));
                    }
                    records.add(row);
                }
                if (!errorList.isEmpty()) {
                    return ResponseEntity.ok(errorList);
                }
                return ResponseEntity.ok(records);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.emptyList());
            }
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

//        return exchangeRateService.getExchangeRateByDate(date);
                return exchangeRateService.getExchangeRateByDateRedis(date);

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