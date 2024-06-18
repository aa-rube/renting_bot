package app.booking.controller.search.util;

public class CorrectForm {

    public static String getPersonWord(int number) {
        if (number % 10 == 1 && number % 100 != 11) {
            return " персону";
        } else if ((number % 10 >= 2 && number % 10 <= 4) && !(number % 100 >= 12 && number % 100 <= 14)) {
            return " персоны";
        } else {
            return " персон";
        }
    }
}
