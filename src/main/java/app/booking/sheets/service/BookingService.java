package app.booking.sheets.service;

import app.booking.admin.AdminMessage;
import app.booking.room.service.MongoDBRoomService;
import app.booking.sheets.model.Room;
import app.booking.sheets.repository.GoogleSheetsBookingManager;
import app.booking.sheets.model.Booking;
import app.booking.user_controller.model.UserSearch;
import app.booking.user_controller.model.ClientData;
import app.booking.user_controller.model.service.UserDataService;
import app.bot.messaging.MessagingService;
import app.bot.messaging.TelegramData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class BookingService {

    @Lazy
    @Autowired
    private MessagingService msgService;

    @Autowired
    private GoogleSheetsBookingManager bookingManager;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private MongoDBRoomService mongoDBRoomService;

    public Booking findBookingObjectByBookingId() {

        return null;
    }

    public Booking createBookingObject(UserSearch userSearch, int objId) {
        Booking booking = new Booking();
        booking.setUserId(String.valueOf(userSearch.getUserId()));
        booking.setBookingId(userSearch.getUserId() + "_" + objId + "_" + System.currentTimeMillis());
        booking.setRoomId(objId);
        booking.setPersonsCount(userSearch.getPersons());

        booking.setCheckIn(userSearch.getCheckIn().toString());
        booking.setCheckOut(userSearch.getCheckOut().toString());
        booking.setStatus(Booking.BookingStatus.PROCESSING.getStatus());

        return booking;
    }

    public void saveToTheSheet(Booking booking, String userName) {
        try {
            bookingManager.addBooking(booking, userName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTheBooking(Booking booking) {
        try {
            bookingManager.updateBooking(booking);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String changeTheStatus(Long userId, String bookingId, String userName, boolean approve) {
        Optional<ClientData> clientDataOptional = userDataService.findClientByLongUserId(userId);
        if (clientDataOptional.isEmpty()) return null;

        ClientData clientData = clientDataOptional.get();

        for (Booking booking : clientData.getMyBooks()) {

            if (booking.getBookingId().equals(bookingId)) {

                booking.setStatus(approve ? Booking.BookingStatus.CONFIRMED.getStatus()
                        : Booking.BookingStatus.CANCELED.getStatus());

                updateTheBooking(booking);
                userDataService.save(clientData);

                Room room = mongoDBRoomService.findByRoomId(booking.getRoomId()).get();

                msgService.processMessage(TelegramData.getSendMessage(userId,
                        AdminMessage.messageToUser(room, booking, approve), null));

                return AdminMessage.bookingDescription(userName, clientData, booking, room, approve);
            }
        }
        return null;
    }

}
