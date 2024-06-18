package app.booking.controller.search.util;

import app.booking.sheets.model.Booking;

import java.util.List;

public class Last {

    public static String getLast(List<String> list) {
        return list.get(list.size() - 1);
    }

    public static void removeLast(List<String> list) {
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