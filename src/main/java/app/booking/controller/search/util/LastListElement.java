package app.booking.controller.search.util;

import app.booking.sheets.model.Booking;

import java.util.List;

public class LastListElement {

    public static String getLastElement(List<String> list) {
        return list.get(list.size() - 1);
    }

    public static void removeLastElement(List<String> list) {
        if (list != null && !list.isEmpty()) {
            list.remove(list.size() - 1);
        }
    }

    public static Booking getLastBooking(List<Booking> list) {
        return list.get(list.size() - 1);
    }

    public static void removeLastBooking(List<Booking> list) {
        if (list != null && !list.isEmpty()) {
            list.remove(list.size() - 1);
        }
    }
}