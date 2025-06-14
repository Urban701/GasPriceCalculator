package com.example.demo.controller;

import com.example.demo.dto.TripCalculationRequest;
import com.example.demo.dto.TripCalculationResponse;
import com.example.demo.service.FuelPriceService;
import com.example.demo.service.TripCostService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FuelController {

    private static final Logger logger = LoggerFactory.getLogger(FuelController.class);

    private final FuelPriceService fuelPriceService;
    private final TripCostService tripCostService;

    public FuelController(FuelPriceService fuelPriceService, TripCostService tripCostService) {
        this.fuelPriceService = fuelPriceService;
        this.tripCostService = tripCostService;
    }

    @GetMapping("/prices")
    public ResponseEntity<Map<String, Double>> getCurrentPrices() {
        try {
            Map<String, Double> prices = fuelPriceService.getCurrentFuelPrices();
            if (prices == null || prices.isEmpty()) {
                logger.warn("/prices endpoint: Fuel prices map is null or empty.");
                // Consider 204 No Content if the process was successful but there's no data,
                // or 503 if the service is unable to fetch data due to external issues.
                // Let's use 503 if it's truly an issue with fetching, but if service works and returns empty, 204 is better.
                // FuelPriceService logs errors if fetching fails, and returns empty map.
                // So an empty map here might mean the upstream service had no prices or structure changed.
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            logger.info("/prices endpoint: Successfully retrieved {} fuel prices.", prices.size());
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            logger.error("/prices endpoint: Error fetching fuel prices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/calculate-cost")
    public ResponseEntity<?> calculateTripCost(@Valid @RequestBody TripCalculationRequest request) {
        try {
            logger.info("/calculate-cost endpoint: Received request: fuelType={}, km={}, usage={}",
                    request.getFuelType(), request.getKilometersDriven(), request.getFuelUsagePer100Km());

            Double totalCost = tripCostService.calculateTripCost(
                    request.getFuelType(),
                    request.getKilometersDriven(),
                    request.getFuelUsagePer100Km()
            );

            if (totalCost == null) {
                // TripCostService returns null for various reasons (e.g., price not found, bad input not caught by validation)
                // These should ideally result in different status codes.
                // For now, a generic 400 or 422 Unprocessable Entity if inputs were valid but logic failed.
                // If FuelPriceService returned empty and that led to null cost, it's more like a 503 backend issue.
                // Let's assume TripCostService logs specifics.
                logger.warn("/calculate-cost endpoint: Trip cost calculation resulted in null. Request: {}", request);
                return ResponseEntity.badRequest().body("Could not calculate trip cost. Ensure fuel type is valid (e.g., 'Diesel', 'Super') and prices are available.");
            }

            logger.info("/calculate-cost endpoint: Calculated cost: {}", totalCost);
            return ResponseEntity.ok(new TripCalculationResponse(totalCost));

        } catch (Exception e) {
            // Catch-all for unexpected errors during processing
            logger.error("/calculate-cost endpoint: Unexpected error processing request: {}", request, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
