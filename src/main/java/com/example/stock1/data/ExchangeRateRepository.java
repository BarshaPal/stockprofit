package com.example.stock1.data;

import com.example.stock1.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, LocalDate> {
//    Optional<ExchangeRateEntity> findByDate(LocalDate date);
}
