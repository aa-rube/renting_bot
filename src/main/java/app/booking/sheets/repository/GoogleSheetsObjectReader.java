package app.booking.sheets.repository;

import app.booking.sheets.model.Room;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GoogleSheetsObjectReader {

    @Autowired
    private Sheets sheetsService;

    @Value("${google.sheets.spreadsheet.id}")
    private String spreadsheetId;

    private final String range = "Объекты!A1:J";

    public List<Room> getRecordsFromSheet() throws IOException {
        List<Room> records = new ArrayList<>();

        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (int i = 1; i < values.size(); i++) {
                List<Object> row = values.get(i);
                Room room = Room.getRoomFromSheetsRow(row);
                if (room == null) continue;
                records.add(room);
            }
        }

        return records;
    }

    public Optional<Room> getRecordsFromSheetById(int id) {
        ValueRange response = null;
        try {
            response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
        } catch (IOException e) {
            return Optional.of(new Room());
        }

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return Optional.empty();
        } else {
            for (int i = 1; i < values.size(); i++) {
                List<Object> row = values.get(i);
                Room room = Room.getRoomFromSheetsRow(row);
                if (room == null) continue;
                return Optional.of(room);
            }
        }
        return Optional.empty();
    }

    public void addRoom(Room room) throws IOException {
        List<Room> rooms = getRecordsFromSheet();

        int lastRoomId = rooms.stream()
                .map(Room::getRoomId)
                .max(Integer::compare)
                .orElse(0);

        int newRoomId = lastRoomId + 1;
        room.setRoomId(newRoomId);

        List<Object> newRow = room.toSheetsRow();

        ValueRange appendBody = new ValueRange().setValues(List.of(newRow));
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, range, appendBody)
                .setValueInputOption("RAW")
                .execute();
    }
}
