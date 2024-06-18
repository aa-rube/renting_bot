package app.booking.util;

public class IntParser {
    public static int getIntByString(String input, int numberInTheLine) {
        return Integer.parseInt(input.split("_")[numberInTheLine]);
    }
}
