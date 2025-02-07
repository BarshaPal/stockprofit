package com.example.stock1.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "exchange_rates")
public class ExchangeRateEntity {

    @Id
    private String date;
    private Double usd, eur, gbp, jpy, aud, cad, sgd, chf, cny, aed;

    // Getters and Setters


    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Double getUsd() { return usd; }
    public void setUsd(Double usd) { this.usd = usd; }

    public Double getEur() { return eur; }
    public void setEur(Double eur) { this.eur = eur; }

    public Double getGbp() { return gbp; }
    public void setGbp(Double gbp) { this.gbp = gbp; }

    public Double getJpy() { return jpy; }
    public void setJpy(Double jpy) { this.jpy = jpy; }

    public Double getAud() { return aud; }
    public void setAud(Double aud) { this.aud = aud; }

    public Double getCad() { return cad; }
    public void setCad(Double cad) { this.cad = cad; }

    public Double getSgd() { return sgd; }
        public void setSgd(Double sgd) { this.sgd = sgd; }

    public Double getChf() { return chf; }
    public void setChf(Double chf) { this.chf = chf; }

    public Double getCny() { return cny; }
    public void setCny(Double cny) { this.cny = cny; }

    public Double getAed() { return aed; }
    public void setAed(Double aed) { this.aed = aed; }
}