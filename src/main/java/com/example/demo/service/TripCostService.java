package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TripCostService {

    private static final Logger logger = LoggerFactory.getLogger(TripCostService.class);

    private final FuelPriceService fuelPriceService;

    // Using constructor injection for FuelPriceService
    public TripCostService(FuelPriceService fuelPriceService) {
        this.fuelPriceService = fuelPriceService;
    }

    /**
     * Calculates the total cost for a trip based on fuel type, distance, and fuel consumption.
     *
     * @param fuelType          The type of fuel (e.g., "Diesel" or "Super").
     *                          Should match keys from FuelPriceService (FuelPriceService.DIESEL_KEY, FuelPriceService.SUPER_KEY).
     * @param kilometersDriven  The total distance of the trip in kilometers.
     * @param fuelUsagePer100Km The vehicle's fuel consumption in liters per 100 km.
     * @return The total calculated trip cost as a Double, or null if the cost cannot be determined
     *         (e.g., fuel price unavailable, invalid fuel type).
     */
    public Double calculateTripCost(String fuelType, double kilometersDriven, double fuelUsagePer100Km) {
        if (fuelType == null || fuelType.trim().isEmpty()) {
            logger.error("Fuel type cannot be null or empty.");
            return null;
        }
        if (kilometersDriven < 0) {
            logger.error("Kilometers driven cannot be negative. Value: {}", kilometersDriven);
            return null;
        }
        if (fuelUsagePer100Km < 0) {
            logger.error("Fuel usage per 100km cannot be negative. Value: {}", fuelUsagePer100Km);
            return null;
        }

        logger.info("Calculating trip cost for fuel type: {}, km: {}, usage/100km: {}",
                fuelType, kilometersDriven, fuelUsagePer100Km);

        Map<String, Double> currentPrices = fuelPriceService.getCurrentFuelPrices();

        if (currentPrices == null || currentPrices.isEmpty()) {
            logger.error("Unable to retrieve current fuel prices. Map is null or empty.");
            return null;
        }

        Double pricePerLiter = currentPrices.get(fuelType);

        if (pricePerLiter == null) {
            logger.error("Price for fuel type '{}' not available. Available types: {}", fuelType, currentPrices.keySet());
            return null;
        }

        if (pricePerLiter <= 0) {
            logger.error("Retrieved price per liter for {} is zero or negative ({}). Cannot calculate cost.", fuelType, pricePerLiter);
            return null;
        }

        // Calculate total fuel needed
        double totalFuelNeeded = (fuelUsagePer100Km / 100.0) * kilometersDriven;

        // Calculate total cost
        double totalCost = totalFuelNeeded * pricePerLiter;

        logger.info("Calculated total trip cost: {}", totalCost);
        return totalCost;
    }
}
