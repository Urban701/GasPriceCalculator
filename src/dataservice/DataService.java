package dataservice;

import helper.Constants;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public record DataService(String url) {

    public double getPriceBasedOnFuelType(String userInputGasOrDiesel) {
        double price = 0;
        if (Objects.equals(userInputGasOrDiesel, Constants.getDieselUserRequest())) {
            try {
                price = getPrice(Constants.getDieselUserRequest());

            } catch (IOException ex) {
                System.out.println("Service not available!");
            }
        } else if(Objects.equals(userInputGasOrDiesel, Constants.getGasUserRequest())) {
            try {
                price = getPrice(Constants.getGasUserRequest());
            } catch (IOException ex) {
                System.out.println("Service not available!");
            }
        }
        return price;
    }


    public double caculatePrice(String kmsDrive, String fuelUsage, double dieselPrice) {
        double consumptionPrice = 0;
        consumptionPrice = Double.parseDouble(kmsDrive)*((Double.parseDouble(fuelUsage)/100))*dieselPrice;
        return consumptionPrice;
    }

    private static Elements getTableRows(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements table = document.getElementsByTag("table");
        Elements tBody = table.select("tbody");
        return tBody.select("tr");
    }

    private double getPrice(String userRequest) throws IOException {
        final String[] priceString = {""};
        Elements tableRows = getTableRows(this.url());
        final AtomicInteger[] x = {new AtomicInteger(0)};

        tableRows.forEach(tr -> {

            if (Objects.equals(x[0].get(), Constants.getDieselPrice()) && Objects.equals(userRequest, Constants.getDieselUserRequest())) {
                priceString[0] = tr.select("td").get(1).childNode(0).toString();
                priceString[0] = priceString[0].replace("€", "").replace(",", ".").trim();
            } else if (Objects.equals(x[0].get(), Constants.getGasPrice()) && Objects.equals(userRequest, Constants.getGasUserRequest())) {
                priceString[0] = tr.select("td").get(1).childNode(0).toString();
                priceString[0] = priceString[0].replace("€", "").replace(",", ".").trim();
            }
            x[0].getAndIncrement();

        });
        return Double.parseDouble(priceString[0]);
    }
}
