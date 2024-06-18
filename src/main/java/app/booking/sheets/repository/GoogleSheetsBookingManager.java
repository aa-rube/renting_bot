package app.booking.sheets.repository;

import app.booking.sheets.config.GoogleSheetsConfig;
import app.booking.sheets.model.Booking;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsBookingManager {

    @Autowired
    GoogleSheetsConfig googleSheetsConfig;

    @Autowired
    private Sheets sheetsService;

    @Value("${google.sheets.spreadsheet.id}")
    private String spreadsheetId;

    @Value("${google.sheets.application.name}")
    private String sheetName;

    public void addBooking(Booking booking, String userName) throws IOException {
        List<List<Object>> values = List.of(
                Arrays.asList(
                        booking.getRoomId(),
                        booking.getBookingId(),
                        booking.getPersonsCount(),
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getStatus(),
                        userName,
                        booking.getFullUserName(),
                        booking.getPhoneNumber(),
                        booking.getUserComment()
                )
        );

        ValueRange body = new ValueRange().setValues(values);

        sheetsService.spreadsheets().values()
                .append(spreadsheetId, "Занятость объекта", body)
                .setValueInputOption("RAW")
                .execute();
    }

    public void updateBooking(Booking updatedBooking) throws IOException {
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, "Занятость объекта" + "!A:J")
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return;
        }

        int rowIndex = -1;
        for (int i = 1; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (row.size() > 1 && row.get(1).toString().equals(updatedBooking.getBookingId())) {
                rowIndex = i;
                break;
            }
        }

        if (rowIndex == -1) {
            System.out.println("BookingId not found.");
            return;
        }

        List<Object> row = values.get(rowIndex);
        boolean isUpdated = false;

        // Ensure the row has at least 10 elements
        while (row.size() < 10) {
            row.add("");
        }

        if (!row.get(0).toString().equals(String.valueOf(updatedBooking.getRoomId()))) {
            row.set(0, updatedBooking.getRoomId());
            isUpdated = true;
        }
        if (!row.get(2).toString().equals(String.valueOf(updatedBooking.getPersonsCount()))) {
            row.set(2, updatedBooking.getPersonsCount());
            isUpdated = true;
        }
        if (!row.get(3).toString().equals(updatedBooking.getCheckIn())) {
            row.set(3, updatedBooking.getCheckIn());
            isUpdated = true;
        }
        if (!row.get(4).toString().equals(updatedBooking.getCheckOut())) {
            row.set(4, updatedBooking.getCheckOut());
            isUpdated = true;
        }
        if (!row.get(5).toString().equals(updatedBooking.getStatus())) {
            row.set(5, updatedBooking.getStatus());
            isUpdated = true;
        }
        if (!row.get(9).toString().equals(updatedBooking.getUserComment())) {
            row.set(9, updatedBooking.getUserComment());
            isUpdated = true;
        }

        if (isUpdated) {
            ValueRange body = new ValueRange().setValues(Collections.singletonList(row));
            String range = "Занятость объекта!A" + (rowIndex + 1) + ":J" + (rowIndex + 1);
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, range, body)
                    .setValueInputOption("RAW")
                    .execute();
        }
    }

    private List<Booking> getBookingsForId(int id) throws IOException {
        List<Booking> bookings = new ArrayList<>();

        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, "Занятость объекта" + "!A:J")
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (int i = 0; i < values.size(); i++) {
                if (i == 0) continue;

                List<Object> row = values.get(i);

                if (Integer.parseInt(row.get(0).toString()) == id
                        && (Booking.BookingStatus.PROCESSING.getStatus().equals(row.get(5).toString())
                        || Booking.BookingStatus.CONFIRMED.getStatus().equals(row.get(5).toString()))) {

                    Booking booking = new Booking();
                    booking.setRoomId(Integer.parseInt(row.get(0).toString()));
                    booking.setBookingId(row.get(1).toString());
                    booking.setPersonsCount(Integer.parseInt(row.get(2).toString()));
                    booking.setCheckIn(row.get(3).toString());
                    booking.setCheckOut(row.get(4).toString());
                    booking.setStatus(row.get(5).toString());
                    booking.setUserComment(row.get(9).toString());

                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }
}
