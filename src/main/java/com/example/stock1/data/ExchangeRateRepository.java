package com.example.stock1.data;

import com.example.stock1.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, LocalDate> {
    @Query("SELECT e FROM ExchangeRateEntity e WHERE e.date = :date")
    ExchangeRateEntity findByDate(@Param("date") String date);
    @Query("SELECT e FROM ExchangeRateEntity e ORDER BY e.date DESC LIMIT 1")
    ExchangeRateEntity findPresentRate();
    @Query(value = "SELECT * FROM exchange_rates " +
            "WHERE date = :date OR date = (SELECT MAX(date) FROM exchange_rates) " +
            "ORDER BY date DESC LIMIT 2",
            nativeQuery = true)
    List<ExchangeRateEntity> findPurchaseSellingRate(@Param("date") String date);




}
