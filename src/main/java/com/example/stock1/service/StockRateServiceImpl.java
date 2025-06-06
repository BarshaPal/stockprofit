package com.example.stock1.service;

import com.example.stock1.DTO.StockRateDTO;
import com.example.stock1.data.StockDataRepository;
import com.example.stock1.entity.StockRateEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

//import static com.example.stock1.service.ExchangeRateServiceImpl.logger;

@Service
public class StockRateServiceImpl implements StockRateService {
    @Autowired
    @Qualifier("stockRateRedisTemplate")
    private RedisTemplate<String, StockRateDTO> redisTemplate;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy"); // Adjusted format
    private final RestTemplate restTemplate;
    private final StockDataRepository stockDataRepository;

    @Value("${google.script.api.url}")  // Store the URL in application.properties
    private String apiUrl;

    public StockRateServiceImpl(RestTemplate restTemplate, StockDataRepository stockDataRepository) {
        this.restTemplate = restTemplate;
        this.stockDataRepository = stockDataRepository;
    }
    public void clearOldRedisStockKeys() {
        Set<String> keys = redisTemplate.keys("stock:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            System.out.println("🧹 Cleared stale Redis stock:* keys");
        }
    }

    @Override
    public List<StockRateDTO> fetchAndStoreStockDataRedis(String company, String date) {
        String redisKey = "stock:" + company + ":" + date;

        // Read from Redis
        Object value = redisTemplate.opsForValue().get(redisKey);
        if (value instanceof StockRateDTO) {
            System.out.println("Fetched stock data from Redis: " + redisKey);
            return Collections.singletonList((StockRateDTO) value);
        } else if (value instanceof LinkedHashMap) {
            // Convert from raw JSON map
            ObjectMapper mapper = new ObjectMapper();
            StockRateDTO dto = mapper.convertValue(value, StockRateDTO.class);
            return Collections.singletonList(dto);
        }

        // Fetch from DB
        Optional<StockRateEntity> stockEntityOpt = stockDataRepository.findByCompanyAndDate(company, date);
        if (stockEntityOpt.isPresent()) {
            StockRateEntity stock = stockEntityOpt.get();

            StockRateDTO dto = new StockRateDTO(
                    stock.getCompany(),
                    stock.getCurrency(),
                    stock.getDate(),
                    stock.getOpen(),
                    stock.getClose()
            );

            // Store in Redis as DTO
            redisTemplate.opsForValue().set(redisKey, dto);
            System.out.println("🟢 Cached stock data in Redis: " + redisKey);

            return Collections.singletonList(dto);
        }

        return Collections.emptyList();

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

    public List<StockRateDTO> fetchAndStoreStockData(String company, String date) {
        // 1. Construct the API URL with query params if needed
        String url = apiUrl + "?company=" + company + "&date=" + date;

        ResponseEntity<StockRateEntity[]> response = restTemplate.getForEntity(url, StockRateEntity[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<StockRateEntity> stocks = Arrays.asList(response.getBody());

            List<StockRateDTO> storedDTOs = new ArrayList<>();

            for (StockRateEntity stock : stocks) {
                if (!isValidStock(stock)) continue;

                stockDataRepository.findByCompanyAndDate(stock.getCompany(), stock.getDate())
                        .ifPresentOrElse(existing -> {
                            System.out.println("Already exists: " + stock.getCompany() + " on " + stock.getDate());
                        }, () -> {
                            stockDataRepository.save(stock);
                            System.out.println("Saved to DB: " + stock);

                            // Cache in Redis as DTO
                            StockRateDTO dto = new StockRateDTO(
                                    stock.getCompany(),
                                    stock.getCurrency(),
                                    stock.getDate(),
                                    stock.getOpen(),
                                    stock.getClose()
                            );
                            String redisKey = "stock:" + company + ":" + stock.getDate();
                            redisTemplate.opsForValue().set(redisKey, dto);
                            storedDTOs.add(dto);
                        });
            }

            return storedDTOs;
        }

        return Collections.emptyList();
    }


    @Override
    public void preloadStockRatesToRedis() {
        List<StockRateEntity> stockRates = stockDataRepository.findAll();  // Fetch from PostgreSQL
        for (StockRateEntity stock : stockRates) {
            String redisKey = "stock:" + stock.getCompany() + ":" + stock.getDate();
            Boolean exists = redisTemplate.hasKey(redisKey);

            if (Boolean.FALSE.equals(exists)) {
                // Convert entity to DTO
                StockRateDTO dto = new StockRateDTO(
                        stock.getCompany(),
                        stock.getCurrency(),
                        stock.getDate(),
                        stock.getOpen(),
                        stock.getClose()
                );

                redisTemplate.opsForValue().set(redisKey, dto);  // Store DTO in Redis
                System.out.println("Preloaded to Redis: " + redisKey);
            }
        }
    }


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
