package helper;

public final class Constants {
    private Constants(){}
    private static final String GAS_USER_REQUEST = "g";
    private static final String DIESEL_USER_REQUEST = "d";
    private static final int DIESEL_PRICE = 0;
    private static final int GAS_PRICE = 1;
    private static final String URL = "https://www.adac.de/reise-freizeit/reiseplanung/reiseziele/oesterreich/uebersicht/kraftstoffpreise/";
    public static String getUrl() {return URL;}
    public static String getDieselUserRequest() {return DIESEL_USER_REQUEST;}
    public static String getGasUserRequest() {return GAS_USER_REQUEST;}
    public static int getDieselPrice() {return DIESEL_PRICE;}
    public static int getGasPrice() {return GAS_PRICE;}
}
