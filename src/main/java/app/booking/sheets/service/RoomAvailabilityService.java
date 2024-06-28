package app.booking.sheets.service;

import app.booking.room.service.MongoDBRoomService;
import app.booking.sheets.model.Room;
import app.booking.user_controller.model.UserSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RoomAvailabilityService {

    @Autowired
    private MongoDBRoomService mongoDBRoomService;

    public List<Room> searchAvailableRooms(UserSearch userSearch) {
        List<Room> availableRooms = new ArrayList<>();

        try {
            List<Room> allRooms = mongoDBRoomService.findAllRooms();

            for (Room room : allRooms) {
                if (userSearch.getPersons() > room.getMaxPersonsCount()) continue;
                availableRooms.add(room);
            }

            availableRooms.sort(Comparator.comparing(Room::getPrice).thenComparing(Room::getMaxPersonsCount));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableRooms;
    }

}