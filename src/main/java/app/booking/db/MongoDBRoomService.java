package app.booking.db;

import app.booking.sheets.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MongoDBRoomService {
    @Autowired
    private RoomRepository repository;

    public void save(Room room) {
        repository.save(room);
    }

    public Optional<Room> findByRoomId(int roomId) {
        return repository.findByRoomId(roomId);
    }

    public List<Room> findAllRooms() {
        return repository.findAll();
    }

    public void deleteAllRooms() {
        repository.deleteAll();
    }
}
