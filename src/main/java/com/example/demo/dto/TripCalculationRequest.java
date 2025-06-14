package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;

public class TripCalculationRequest {

    @NotBlank(message = "Fuel type cannot be blank")
    private String fuelType;

    @NotNull(message = "Kilometers driven cannot be null")
    @Positive(message = "Kilometers driven must be positive")
    private Double kilometersDriven;

    @NotNull(message = "Fuel usage per 100km cannot be null")
    @Positive(message = "Fuel usage per 100km must be positive")
    private Double fuelUsagePer100Km;

    // Getters and Setters
    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Double getKilometersDriven() {
        return kilometersDriven;
    }

    public void setKilometersDriven(Double kilometersDriven) {
        this.kilometersDriven = kilometersDriven;
    }

    public Double getFuelUsagePer100Km() {
        return fuelUsagePer100Km;
    }

    public void setFuelUsagePer100Km(Double fuelUsagePer100Km) {
        this.fuelUsagePer100Km = fuelUsagePer100Km;
    }
}
