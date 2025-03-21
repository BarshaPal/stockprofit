package com.example.stock1.service;

import com.example.stock1.data.ExchangeRateRepository;
import com.example.stock1.entity.ExchangeRateEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ExchangeRateRepository repository;
    @Autowired
    private RedisTemplate<String, ExchangeRateEntity> redisTemplate;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy"); // Adjusted format

    @Autowired
    public ExchangeRateServiceImpl(RedisTemplate<String, ExchangeRateEntity> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Transactional
    public String processPDF(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("summary");
            List<ExchangeRateEntity> currencyRates = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                ExchangeRateEntity rate = new ExchangeRateEntity();
                String date = null;

                Map<String, Consumer<Double>> rateSetters = new HashMap<>();
                rateSetters.put("USD", rate::setUsd);
                rateSetters.put("EUR", rate::setEur);
                rateSetters.put("GBP", rate::setGbp);
                rateSetters.put("JPY", rate::setJpy);
                rateSetters.put("AUD", rate::setAud);
                rateSetters.put("CAD", rate::setCad);
                rateSetters.put("SGD", rate::setSgd);
                rateSetters.put("CHF", rate::setChf);
                rateSetters.put("CNY", rate::setCny);
                rateSetters.put("AED", rate::setAed);

                for (Cell cell : row) {
                    int columnIndex = cell.getColumnIndex();

                    Cell titleCell = sheet.getRow(0).getCell(columnIndex);
                    if (titleCell == null || titleCell.getCellType() != CellType.STRING) continue;
                    String columnName = titleCell.getStringCellValue().trim();

                    if (columnIndex == 0) {

                        Date extractedDate = getCellValueAsDate(cell);
                        if (extractedDate != null) {
                            String formattedDate = formatDate(extractedDate);
                            date=formattedDate;
                            rate.setDate(formattedDate);
                        }
                    }
                    else {
                        Double calculatedRate = getCellValueAsDouble(cell);
                        if (calculatedRate != null) {
                            rateSetters.getOrDefault(columnName, v -> {}).accept(calculatedRate);
                        }
                    }
                }

                if (date != null) {
                    currencyRates.add(rate);
                }
            }

            System.out.println("Total records parsed: " + currencyRates.size());
            repository.saveAll(currencyRates);
            System.out.println("Data saved successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Fetched";
    }

    @Override
    public void preloadExchangeRatesToRedis() {
        List<ExchangeRateEntity> rates = repository.findAll();

        for (ExchangeRateEntity rate : rates) {
            String key = "exchange:" + rate.getDate();
            redisTemplate.opsForValue().set(key, rate);
        }

        System.out.println("Exchange rate data cached in Redis");
    }


@Override
public List<Map<String, Object>> getRatesByCurrency(String currency) {
    // Whitelist currency names to prevent SQL injection
    List<String> allowedCurrencies = Arrays.asList("usd", "eur", "gbp", "jpy", "aud", "cad", "sgd", "chf", "cny", "aed");
    if (!allowedCurrencies.contains(currency.toLowerCase())) {
        throw new IllegalArgumentException("Invalid currency code: " + currency);
    }

    // Build native SQL query safely
    String sql = "SELECT date, " + currency + " FROM exchange_rates WHERE " + currency + " IS NOT NULL";

    Query query = entityManager.createNativeQuery(sql);
    List<Object[]> results = query.getResultList();

    // Map query results into List of maps (date + rate)
    List<Map<String, Object>> response = new ArrayList<>();
    for (Object[] row : results) {
        Map<String, Object> data = new HashMap<>();
        data.put("date", row[0]);
        data.put("rate", row[1]);
        response.add(data);
    }

    return response;
}

@Override
public ExchangeRateEntity getExchangeRateByDate(String date) {
    try {
        ExchangeRateEntity rate = repository.findByDate(date);
        if (rate == null) {
            throw new Exception("Exchange rate not found for date: " + date);
        }
        return rate;
    } catch (Exception e) {
        throw new RuntimeException("Error fetching exchange rate", e);
    }
}


@Override
public ExchangeRateEntity exchangeRateofCurrentDate() {
    try {
        ExchangeRateEntity rate = repository.findPresentRate();
        if (rate == null) {
            throw new Exception("Exchange rate not found for date: " );
        }
        return rate;
    } catch (Exception e) {
        throw new RuntimeException("Error fetching exchange rate", e);
    }
}


@Override
public List<ExchangeRateEntity>  purchasesellingRate(String date) {
    try {
        Stock stock=YahooFinance.get("AAPL");
        if (stock != null) {
            logger.info("Stock Details - Symbol: {}, Currency: {}, Price: {}",
                    stock.getSymbol(),
                    stock.getCurrency(),
                    stock.getQuote().getPrice());
        } else {
            logger.warn("Stock data for SAP is null");
        }

        List<ExchangeRateEntity> rate = repository.findPurchaseSellingRate(date);
        if (rate == null) {
            throw new Exception("Exchange rate not found for date: " );
        }
        return rate;
    } catch (Exception e) {
        throw new RuntimeException("Error fetching exchange rate", e);
    }
}




private String formatDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return sdf.format(date).toString();
}

private Date getCellValueAsDate(Cell cell) {
    if (DateUtil.isCellDateFormatted(cell)) {
        return cell.getDateCellValue();
    } else {
        return DateUtil.getJavaDate(cell.getNumericCellValue());
    }
}


private Double getCellValueAsDouble(Cell cell) {
    switch (cell.getCellType()) {
        case STRING:
            if ("NULL".equalsIgnoreCase(cell.getStringCellValue().trim())) {
                return null;
            }
            try {
                return Double.parseDouble(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format in cell: " + cell.getStringCellValue());
                return null;
            }
        case NUMERIC:
            return cell.getNumericCellValue();
        case FORMULA:
            return getFormulatedData(cell);
        default:
            return null;
    }
}

private Double getFormulatedData(Cell cell) {
    switch (cell.getCachedFormulaResultType()) {
        case NUMERIC:
            return cell.getNumericCellValue();
        default:
            return null;
    }
}
}
