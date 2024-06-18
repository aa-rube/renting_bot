package app.booking.sheets.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private int roomId;
    private String bookingId;
    private int personsCount;
    private String userId;
    private String checkIn;
    private String checkOut;
    private String status;
    private String userComment;

    private String fullUserName;
    private String phoneNumber;

    @Getter
    public enum  BookingStatus{
        CONFIRMED("Confirmed"),
        CANCELED("Canceled"),
        PROCESSING("Processing"),
        NO_RECORDING("No recording");

        private final String status;
        BookingStatus(String status) {
            this.status = status;
        }

    }
}
