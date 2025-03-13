package com.example.stock1.service;

import com.example.stock1.data.StockRateRepository;
import com.example.stock1.entity.StockRateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface StockRateService {

    String processExcel(MultipartFile file) throws IOException;

}
