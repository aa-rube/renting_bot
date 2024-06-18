package app.booking.db;

import app.booking.sheets.model.Room;
import app.booking.sheets.repository.GoogleSheetsObjectReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateLocalRooms {
    @Autowired
    private MongoDBRoomService mongoDBRoomService;

    @Autowired
    private GoogleSheetsObjectReader googleSheetsObjectReader;

    @Scheduled(fixedRate = 6000 * 10 * 5) //5min
    public void update() {
        List<Room> allRooms;
        try {
            allRooms = googleSheetsObjectReader.getRecordsFromSheet();
        } catch (Exception ignored) {
            return;
        }

        mongoDBRoomService.deleteAllRooms();

        for (Room room : allRooms) {
            mongoDBRoomService.save(room);
        }
    }
}
