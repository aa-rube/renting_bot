package app.booking.sheets.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearch {
    private Long userId;

    private LocalDate checkIn;
    private LocalDate checkOut;
    private int persons;

    private List<Integer> deletedRoomsBySearch;

    private int serviceMsgId;
    private String inlineId;
    private int page = 0;

    public UserSearch(Long userId, LocalDate checkIn, LocalDate checkOut, int persons, List<Integer> deletedRoomsBySearch) {
        this.userId = userId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.persons = persons;
        this.deletedRoomsBySearch = deletedRoomsBySearch;
    }
}
