package com.example.stock1.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExchangeRateService {

    String processPDF(MultipartFile file) throws IOException;
}
