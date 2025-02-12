package com.example.stock1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "stock_rates")
public class StockRateEntity {
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getSap() {
        return sap;
    }

    public void setSap(Double sap) {
        this.sap = sap;
    }

    public Double getGoogle() {
        return google;
    }

    public void setGoogle(Double google) {
        this.google = google;
    }

    public Double getMicrosoft() {
        return microsoft;
    }

    public void setMicrosoft(Double microsoft) {
        this.microsoft = microsoft;
    }

    @Id
    private String date;
    private Double sap,google,microsoft;

}
