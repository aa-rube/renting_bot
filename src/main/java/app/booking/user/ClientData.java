package app.booking.user;

import app.booking.sheets.model.Booking;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "client_data")
public class ClientData {
    @Id
    private String id;
    private Long userIdLong;
    private String accountTelegramName;
    private String userName;

    private List<String> fullCustomerNames;
    private List<String> contactNumbers;

    private List<Booking> myBooks;
}
