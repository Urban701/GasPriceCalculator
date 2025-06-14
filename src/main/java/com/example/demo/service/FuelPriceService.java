package com.example.demo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class FuelPriceService {

    private static final Logger logger = LoggerFactory.getLogger(FuelPriceService.class);

    @Value("${fuel.price.url}")
    private String fuelPriceUrl;

    // Constants from the original project, adapted for local use
    // These were originally used to identify table rows.
    // DIESEL_ROW_INDEX (original DIESEL_PRICE = 0) for the first relevant row
    // SUPER_ROW_INDEX (original GAS_PRICE = 1) for the second relevant row
    private static final int DIESEL_ROW_INDEX = 0;
    private static final int SUPER_ROW_INDEX = 1;

    public static final String DIESEL_KEY = "Diesel";
    public static final String SUPER_KEY = "Super";


    public Map<String, Double> getCurrentFuelPrices() {
        Map<String, Double> fuelPrices = new HashMap<>();

        try {
            logger.info("Fetching fuel prices from URL: {}", fuelPriceUrl);
            Document document = Jsoup.connect(fuelPriceUrl).get();
            Elements table = document.getElementsByTag("table");
            if (table.isEmpty()) {
                logger.error("No table found on the page: {}", fuelPriceUrl);
                return fuelPrices; // Return empty map
            }

            Elements tBody = table.first().select("tbody");
            if (tBody.isEmpty() || tBody.first().select("tr").size() < 2) {
                logger.error("Table body (tbody) or sufficient rows not found.");
                return fuelPrices;
            }

            Elements tableRows = tBody.first().select("tr");

            // Logic adapted from the original GasPriceCalculator
            // It assumes Diesel is in the row at DIESEL_ROW_INDEX (0)
            // and Super (Gas) is in the row at SUPER_ROW_INDEX (1)

            // Extract Diesel Price
            if (tableRows.size() > DIESEL_ROW_INDEX) {
                Element dieselRow = tableRows.get(DIESEL_ROW_INDEX);
                Elements dieselCols = dieselRow.select("td");
                if (dieselCols.size() > 1) { // Expecting name in col 0, price in col 1
                    String dieselPriceStr = dieselCols.get(1).text(); // text() is safer
                    // Further check if the first column indeed says "Diesel" or similar
                    String fuelName = dieselCols.get(0).text();
                    if (fuelName.toLowerCase().contains("diesel")) {
                         try {
                            double price = parsePrice(dieselPriceStr);
                            fuelPrices.put(DIESEL_KEY, price);
                            logger.info("Extracted Diesel price: {}", price);
                        } catch (NumberFormatException e) {
                            logger.error("Error parsing Diesel price string: '{}'", dieselPriceStr, e);
                        }
                    } else {
                        logger.warn("Row at index {} does not seem to be Diesel. Found: {}", DIESEL_ROW_INDEX, fuelName);
                    }
                } else {
                    logger.warn("Diesel price row (index {}) does not have enough columns.", DIESEL_ROW_INDEX);
                }
            } else {
                 logger.warn("Not enough rows in table to find Diesel price at index {}.", DIESEL_ROW_INDEX);
            }

            // Extract Super (Gasoline) Price
            if (tableRows.size() > SUPER_ROW_INDEX) {
                Element superRow = tableRows.get(SUPER_ROW_INDEX);
                Elements superCols = superRow.select("td");
                if (superCols.size() > 1) { // Expecting name in col 0, price in col 1
                    String superPriceStr = superCols.get(1).text();
                     // Further check if the first column indeed says "Super" or similar
                    String fuelName = superCols.get(0).text();
                    if (fuelName.toLowerCase().contains("super")) { // ADAC uses "Super E5" or "Super E10"
                        try {
                            double price = parsePrice(superPriceStr);
                            fuelPrices.put(SUPER_KEY, price);
                            logger.info("Extracted Super price: {}", price);
                        } catch (NumberFormatException e) {
                            logger.error("Error parsing Super price string: '{}'", superPriceStr, e);
                        }
                    } else {
                         logger.warn("Row at index {} does not seem to be Super. Found: {}", SUPER_ROW_INDEX, fuelName);
                    }
                } else {
                    logger.warn("Super price row (index {}) does not have enough columns.", SUPER_ROW_INDEX);
                }
            } else {
                logger.warn("Not enough rows in table to find Super price at index {}.", SUPER_ROW_INDEX);
            }

        } catch (IOException e) {
            logger.error("IOException while fetching fuel prices from {}: {}", fuelPriceUrl, e.getMessage(), e);
            // Return empty map or throw a custom exception
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching fuel prices: {}", e.getMessage(), e);
            // Return empty map or throw a custom exception
        }

        if (fuelPrices.isEmpty()) {
            logger.warn("Fuel price map is empty. Check logs for errors during scraping.");
        }
        return fuelPrices;
    }

    private double parsePrice(String priceString) throws NumberFormatException {
        // Original: priceString[0] = tr.select("td").get(1).childNode(0).toString();
        // Original: priceString[0] = priceString[0].replace("€", "").replace(",", ".").trim();
        // Using .text() directly is often cleaner with Jsoup.
        return Double.parseDouble(priceString.replace("€", "").replace(",", ".").trim());
    }
}
