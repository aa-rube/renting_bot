package app.booking.user_controller.model;

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
    private List<Integer> escapingRooms;
    private int serviceMsgId;
    private String inlineId;
    private int page = 0;
    private boolean searchFilled;

    public UserSearch(Long userId, LocalDate checkIn, LocalDate checkOut, int persons,
                      List<Integer> escapingRooms, boolean searchFilled) {

        this.userId = userId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.persons = persons;
        this.escapingRooms = escapingRooms;
        this.searchFilled = searchFilled;
    }
}
