package com.example.stock1.service;

import com.example.stock1.data.ExchangeRateRepository;
import com.example.stock1.entity.ExchangeRateEntity;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Autowired
    private ExchangeRateRepository repository;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy"); // Adjusted format

    @Transactional
    public String processPDF(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("rates");
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
                            System.out.println("Formatted Date: " + formattedDate);
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

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    private Date getCellValueAsDate(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue(); // Directly returns a Java Date
        } else {
            // Convert Excel serial number to Java Date manually
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
