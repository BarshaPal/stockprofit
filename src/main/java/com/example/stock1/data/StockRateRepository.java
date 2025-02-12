package com.example.stock1.data;

import com.example.stock1.entity.ExchangeRateEntity;
import com.example.stock1.entity.StockRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Repository
public interface StockRateRepository extends JpaRepository<StockRateEntity, String> {
    Optional<StockRateEntity> findById(Date date);
//    Optional<ExchangeRateEntity> findByDate(LocalDate date);
}
