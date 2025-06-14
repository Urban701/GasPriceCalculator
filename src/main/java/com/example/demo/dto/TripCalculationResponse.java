package com.example.demo.dto;

public class TripCalculationResponse {

    private Double totalCost;

    public TripCalculationResponse(Double totalCost) {
        this.totalCost = totalCost;
    }

    // Getter and Setter
    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
}
