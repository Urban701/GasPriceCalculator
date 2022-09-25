import java.util.Scanner;

import exception.IncorrectInputException;
import helper.Constants;
import static exception.ErrorHandling.checkUserInputs;
import dataservice.DataService;


public class Main {
    private static final DataService dataService = new DataService(Constants.getUrl());

    public static void main(String[] args) throws IncorrectInputException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Gas[g] or Diesel[d] to get the current price in Austria");
        String userInputGasOrDiesel = scanner.nextLine();

        System.out.println("How many km´s did you drive?");
        String kmsDrive = scanner.nextLine();

        System.out.println("How many liters of fuel did your car use per 100 km ?");
        String fuelUsage = scanner.nextLine();

        try {
            checkUserInputs(userInputGasOrDiesel, kmsDrive, fuelUsage);
        } catch (IncorrectInputException e) {
            System.out.println(e.getMessage());
        }

        double price = dataService.getPriceBasedOnFuelType(userInputGasOrDiesel);

        double consumptionPrice;

        try {
            consumptionPrice = dataService.caculatePrice(kmsDrive, fuelUsage, price);
            System.out.println((consumptionPrice <= 0) ? "Please check later" : consumptionPrice + " €");
        } catch (NumberFormatException e) {
            System.out.println("Invalid:d" + e.getMessage());
        }
    }
}

