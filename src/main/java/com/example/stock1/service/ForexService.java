package com.example.stock1. service;




import com.example.stock1.data.ExchangeRateRepository;
import com.example.stock1.entity.ExchangeRateEntity;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ForexService {
    private final ExchangeRateRepository exchangeRateRepository;

    private static final String PDF_URL = "https://sbi.co.in/documents/16012/1400784/FOREX_CARD_RATES.pdf"; // Replace with actual URL


    public void extractAndStoreData() throws IOException {


        try (InputStream in = new URL(PDF_URL).openStream();
             PDDocument document = PDDocument.load(in)) {

            // Extract text from the PDF
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Parse and store data
            ExchangeRateEntity exchangeRateEntity = parseForexData(text);
            if (exchangeRateEntity != null) {
                exchangeRateRepository.save(exchangeRateEntity);
                System.out.println("Exchange rates saved successfully!");
            } else {
                System.out.println("Failed to extract exchange rate data.");
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


//        File file = new File(FILE_PATH);
//        PDDocument document = PDDocument.load(file);
//        PDFTextStripper stripper = new PDFTextStripper();
//        String text = stripper.getText(document);
//        document.close();
//
//        ExchangeRateEntity exchangeRateEntity = parseForexData(text);
//        if (exchangeRateEntity != null) {
//            exchangeRateRepository.save(exchangeRateEntity);
//        }
    }
    private String formatDate(String date_string) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(sdf2.format(sdf.parse(date_string)));
        return sdf2.format(sdf.parse(date_string)).toString();
    }
    // 3. Parse text and extract required data
    private ExchangeRateEntity parseForexData(String text) throws ParseException {
        ExchangeRateEntity exchangeRate = new ExchangeRateEntity();

        Pattern datePattern = Pattern.compile("Date\\s+(\\d{2}-\\d{2}-\\d{4})");
        Matcher dateMatcher = datePattern.matcher(text);
        if (dateMatcher.find()) {
            String date=dateMatcher.group(1);
            String savedDate=formatDate(date);
            exchangeRate.setDate(savedDate);
        } else {
            return null; // Invalid data
        }

        exchangeRate.setUsd(extractRate(text, "UNITED STATES DOLLAR USD/INR"));
        exchangeRate.setEur(extractRate(text, "EURO EUR/INR"));
        exchangeRate.setGbp(extractRate(text, "GREAT BRITAIN POUND GBP/INR"));
        exchangeRate.setJpy(extractRate(text, "JAPANESE YEN JPY/INR"));
        exchangeRate.setAud(extractRate(text, "AUSTRALIAN DOLLAR AUD/INR"));
        exchangeRate.setCad(extractRate(text, "CANADIAN DOLLAR CAD/INR"));
        exchangeRate.setSgd(extractRate(text, "SINGAPORE DOLLAR SGD/INR"));
        exchangeRate.setChf(extractRate(text, "SWISS FRANC CHF/INR"));
        exchangeRate.setCny(extractRate(text, "CHINESE YUAN CNY/INR"));
        exchangeRate.setAed(extractRate(text, "UAE DIRHAM AED/INR"));

        return exchangeRate;
    }

    // 4. Extract buy rate using regex
    private Double extractRate(String text, String currency) {
        Pattern pattern = Pattern.compile(currency + "\\s+(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return null;
    }

    public void fetchAndStoreForexRates() {
        try {
//            downloadPDF();
            extractAndStoreData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}











