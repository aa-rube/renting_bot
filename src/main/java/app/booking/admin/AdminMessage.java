package app.booking.admin;

import app.booking.controller.search.util.CorrectForm;
import app.booking.controller.search.util.LastListElement;
import app.booking.db.MongoDBRoomService;
import app.booking.sheets.model.Booking;
import app.booking.sheets.model.Room;
import app.booking.user.ClientData;
import app.bot.config.BotConfig;
import app.bot.messaging.MessagingService;
import app.bot.messaging.TelegramData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminMessage {
    @Lazy
    @Autowired
    private MessagingService msgService;

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private MongoDBRoomService mongoDBRoomService;

    @Autowired
    private AdminKeyboard adminKeyboard;

    private Long getAdminChatId() {
        return botConfig.getAdminChat();
    }

    public void userMadeBooking(Booking booking, String userName) {
        Room room = mongoDBRoomService.findByRoomId(booking.getRoomId()).get();

        String text = "Гость " + userName + " оставил заявку на бронирование!\n\n"

                + "ФИО: " + booking.getFullUserName() + "\n"
                + "Телефон: " + booking.getPhoneNumber() + "\n"
                + "Количество гостей: " + booking.getPersonsCount() + "\n"
                + "Дата заезда: " + booking.getCheckIn() + ", после 12-00\n"
                + "Дата выезда: " + booking.getCheckOut() + ", до 12 часов\n"

                + "Отель: " + room.getHotel() + "\n\n"
                + "Комната: №" + room.getRoomId() + ", " + room.getPubName() + "\n"
                + "Цена за сутки: " + room.getPrice();

        msgService.processMessage(TelegramData.getSendMessage(getAdminChatId(), text,
                adminKeyboard.getBookingOptions(booking.getBookingId())));
    }

    public static String bookingDescription(String userName, ClientData data, Booking booking, Room room, boolean pay) {
        return (pay ? "✅✅Оплата аренды подтверждена " + userName : "❌‼‼Аренда отменена " + userName)

                + "\n\n"
                + "Гость " + data.getUserName() + " оставил заявку на бронирование!\n\n"

                + "ФИО: " + LastListElement.getLastElement(data.getFullCustomerNames()) + "\n"
                + "Телефон: " + LastListElement.getLastElement(data.getContactNumbers()) + "\n"
                + "Количество гостей: " + booking.getPersonsCount() + "\n"
                + "Дата заезда: " + booking.getCheckIn() + ", после 12-00\n"
                + "Дата выезда: " + booking.getCheckOut() + ", до 12 часов\n"

                + "Отель: " + room.getHotel() + "\n\n"
                + "Комната: №" + room.getRoomId() + ", " + room.getPubName() + "\n"
                + "Цена за сутки: " + room.getPrice() + "\n\n"
                + "Последнее изменение: " + LocalDateTime.now().toString().substring(0, 16)
                .replace("T", ", ");
    }


    public static String messageToUser(Room room, Booking booking, boolean approved) {
        return "Уважаемый Гость!\n\nВаше бронирование в отеле " + room.getHotel()
                + (approved ?" оплачено и подтверждено✅✅✅" :
                " отменено❌❌❌\n\nЕсли вы не отменяли бронь - срочно свяжитесь с нами /helpme")
                + "\n\n"
                + "Бронирование №<code>" + booking.getBookingId() + "</code>\n"
                + "Дата заезда: " + booking.getCheckIn() + "\n"
                + "Дата выезда: " + booking.getCheckOut() + "\n"
                + "Номер на " + booking.getPersonsCount() + " " + CorrectForm.getPersonWord(booking.getPersonsCount()) + "\n"
                + "Гость: " + booking.getFullUserName() + ", телефон для связи: " + booking.getPhoneNumber();

    }

}
