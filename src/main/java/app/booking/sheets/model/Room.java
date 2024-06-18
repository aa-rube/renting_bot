package app.booking.sheets.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "room_data")
public class Room {
    @Id
    private String mongoId;

    private int roomId;

    private String hotel;
    private String pubName;
    private String pubDescription;
    private List<String> links;
    private String price;
    private String view;
    private String comment;
    private int maxPersonsCount;

    public static Room getRoomFromSheetsRow(List<Object> row) {
        if (row.get(0).toString().isEmpty()) return null;

        String columnA = !row.isEmpty() ? row.get(0).toString() : "";
        String columnB = row.size() > 1 ? row.get(1).toString() : "";
        String columnC = row.size() > 2 ? row.get(2).toString() : "";
        String columnD = row.size() > 3 ? row.get(3).toString() : "";
        String columnE = row.size() > 4 ? row.get(4).toString() : "";
        String columnF = row.size() > 5 ? row.get(5).toString() : "";
        String columnG = row.size() > 6 ? row.get(6).toString() : "";
        String columnH = row.size() > 7 ? row.get(7).toString() : "";
        String columnI = row.size() > 8 ? row.get(8).toString() : "";

        int roomId = Integer.parseInt(columnA);
        List<String> links = List.of(columnE.split(","));

        int persons = Integer.parseInt(columnI);

        Room room = new Room();
        room.setRoomId(roomId);
        room.setHotel(columnB);
        room.setPubName(columnC);
        room.setPubDescription(columnD);
        room.setLinks(links);
        room.setPrice(columnF);
        room.setView(columnG);
        room.setComment(columnH);
        room.setMaxPersonsCount(persons);

        return room;
    }

    public List<Object> toSheetsRow() {
        return List.of(
                roomId,
                hotel,
                pubName,
                pubDescription,
                String.join(",", links),
                price,
                view,
                comment,
                maxPersonsCount
        );
    }
}
