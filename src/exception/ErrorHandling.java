package exception;

import helper.Constants;

import java.util.Objects;

public class ErrorHandling {

    public ErrorHandling(){}

    public static void checkUserInputs(String userInput, String kmsDrive, String fuelUsage) throws IncorrectInputException {
        if(!Objects.equals(userInput, Constants.getGasUserRequest()) && !Objects.equals(userInput, Constants.getDieselUserRequest()) ) {
            throw new IncorrectInputException("Please check your inputs. Valid values are g|d");
        }
        if(isNotDoubleValue(kmsDrive)) {
            throw new IncorrectInputException("Please enter a valid number for driven kms [for example: 167.4]");
        }
        if(isNotDoubleValue(fuelUsage)) {
            throw new IncorrectInputException("Please enter a valid number for fuel usage! [for example: 6.5]");
        }
    }

    public static boolean isNotDoubleValue(String str ){
        try{
            Double.parseDouble( str );
            return false;
        } catch( Exception e ){
            return true;
        }
    }
}
