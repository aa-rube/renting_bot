package app.booking.sheets.repository;

import app.booking.db.MongoDBRoomService;
import app.booking.sheets.model.Room;
import app.booking.sheets.model.UserSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RoomAvailabilityService {

    @Autowired
    private GoogleSheetsBookingManager bookingManager;

    @Autowired
    private MongoDBRoomService mongoDBRoomService;

    public List<Room> searchAvailableRooms(UserSearch userSearch) {
        List<Room> availableRooms = new ArrayList<>();

        try {
            List<Room> allRooms = mongoDBRoomService.findAllRooms();

            for (Room room : allRooms) {
                if (userSearch.getPersons() > room.getMaxPersonsCount()) continue;

                if (isRoomAvailable(room, userSearch)) availableRooms.add(room);
            }

            availableRooms.sort(Comparator.comparing(Room::getPrice).thenComparing(Room::getMaxPersonsCount));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableRooms;
    }

    private boolean isRoomAvailable(Room room, UserSearch userSearch) throws IOException {

        if (userSearch.getDeletedRoomsBySearch().contains(room.getRoomId())) {
            return false;
        }

//        List<String> availableDates = bookingManager.getAvailableDates(room.getRoomId());
//
//        LocalDate checkIn = userSearch.getCheckIn();
//        LocalDate checkOut = userSearch.getCheckOut();
//
//        for (String availableDateRange : availableDates) {
//
//            String[] dates = availableDateRange.split(" to ");
//            LocalDate availableFrom = LocalDate.parse(dates[0]);
//            LocalDate availableTo = LocalDate.parse(dates[1]);
//
//            if (!checkIn.isAfter(availableTo) && !checkOut.isBefore(availableFrom)) {
//                return true;
//            }
//        }

        return true;
    }
}