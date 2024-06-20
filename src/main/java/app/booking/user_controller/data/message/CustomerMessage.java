package app.booking.user_controller.data.message;

import app.booking.admin.AdminMessage;
import app.booking.user_controller.data.keyboard.CustomerKeyboard;
import app.booking.util.CorrectForm;
import app.booking.util.Last;
import app.booking.room.service.MongoDBRoomService;
import app.booking.sheets.model.Booking;
import app.booking.sheets.repository.GoogleSheetsBookingManager;
import app.booking.sheets.service.RoomAvailabilityService;
import app.booking.sheets.model.Room;
import app.booking.user_controller.model.UserSearch;
import app.booking.user_controller.model.ClientData;
import app.booking.util.*;
import app.booking.util.img.CollectImg;
import app.bot.messaging.GroupMediaMessage;
import app.bot.messaging.MessagingService;
import app.bot.messaging.TelegramData;
import app.bot.messaging.data.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerMessage {
    @Lazy
    @Autowired
    private MessagingService msgService;

    @Autowired
    private CustomerKeyboard customerKeyboard;

    @Autowired
    private GoogleSheetsBookingManager bookingManager;

    @Autowired
    private RoomAvailabilityService roomAvailabilityService;

    @Autowired
    private MongoDBRoomService mongoDBRoomService;

    @Autowired
    private AdminMessage adminMessage;

    private static final int PAGE_SIZE = 3;

    public void buildRoomPages(String inlineId, UserSearch userSearch) {
        msgService.processMessage(TelegramData.getPopupMessage(inlineId,
                Text.READING_THE_TABLE.getText(), false ));

        List<Room> rooms = roomAvailabilityService.searchAvailableRooms(userSearch);

        if (rooms.isEmpty()) {
            msgService.processMessage(TelegramData.getPopupMessage(inlineId,
                    Text.NO_ONE_ROOM.getText(), false));
            return;
        }

        int page = userSearch.getPage();
        int totalRooms = rooms.size();

        int startIndex = page * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalRooms);

        if (page == 0) {
            msgService.processMessage(TelegramData.getSendMessage(userSearch.getUserId(),
                    "Нажмите кнопку снизу экрана, чтобы загрузить следующие варианты",
                    customerKeyboard.getNextPage(PAGE_SIZE)));
        }

        if (startIndex >= totalRooms || endIndex > totalRooms) {
            msgService.processMessage(TelegramData.getPopupMessage(inlineId,"Больше вариантов нет",false));
            return;
        }

        List<Room> roomsOnPage = rooms.subList(startIndex, endIndex);

        for (Room room : roomsOnPage) {
            buildAndSendMessage(userSearch.getUserId(), room);
        }

    }


    private void buildAndSendMessage(Long chatId, Room room) {
        String text = textBuilder(room);
        msgService.processMessage(TelegramData.getSendMessage(chatId,
                text, customerKeyboard.getRoomKeyboard(room)));
    }


    private String textBuilder(Room room) {
        StringBuilder builder = new StringBuilder();

        String name = room.getPubName() + ", " + room.getHotel();
        builder.append(LinkWrapper.wrapTextInLink(name, room.getLinks().get(0))).append("\n\n");

        builder.append(room.getPubDescription()).append("\n\n")
                .append(room.getPrice());
        return builder.toString();
    }


    public void sendDetailsById(UserSearch userSearch, int objId, int msgId) {
        msgService.processMessage(TelegramData.getPopupMessage(userSearch.getInlineId(),
                "Загружаем фотографии...", false ));

        Optional<Room> roomOpt = mongoDBRoomService.findByRoomId(objId);

        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            String description = getRoomDescription(room);

            java.io.File[] filesArray = CollectImg.threadsDownload(room);

            GroupMediaMessage.MediaGroupData data = new GroupMediaMessage.MediaGroupData();
            data.setListPhotoFilesId(Arrays.stream(filesArray).toList());

            msgService.processMessage(GroupMediaMessage.getMediaGroupMessage(userSearch.getUserId(),
                    data, "Фото-галерея для номера №" + objId));

            msgService.processMessage(TelegramData.getSendMessage(userSearch.getUserId(), description,
                    customerKeyboard.getDetailsKeyboard(userSearch, objId)));

            msgService.processMessage(TelegramData.getDeleteMessage(userSearch.getUserId(), msgId));
        }
    }


    private String getRoomDescription(Room room) {
        return "Описание номера " + room.getRoomId() + "\n"
                + room.getHotel() + ", " + room.getPubName() + "\n\n"
                + room.getPubDescription() + "\n"
                + room.getComment() + "\n\n"
                + room.getPrice();
    }


    public void backToDescription(Long chatId, int objId, int msgId) {
        Optional<Room> roomOpt = mongoDBRoomService.findByRoomId(objId);
        if (roomOpt.isEmpty()) return;

        msgService.processMessage(TelegramData.getEditCaption(chatId, getRoomDescription(roomOpt.get()),
                customerKeyboard.getDetailsKeyboard(objId), msgId));

    }

    public void makeBooking(Booking booking, UserSearch userSearch, String userName, int msgId) {
        msgService.processMessage(TelegramData.getEditMessage(userSearch.getUserId(),
                "Ваш номер предварительно забронирован✨\uD83E\uDD73\uD83C\uDF89\uD83C\uDF8A" +
                        "\n\nНаш оператор свяжется с вами для подтверждения и оплаты!",
                null, msgId));

        adminMessage.userMadeBooking(booking, userName);
    }

    public void startInputRealClientData(UserSearch userSearch, int objId, int msgId) {
        msgService.processMessage(TelegramData.getEditMessage(userSearch.getUserId(),
                "Для создания брони, пожалуйста, напишите ваши ФИО:",
                customerKeyboard.getKeyboardForNameInputMessage(objId), msgId));
    }

    public void shareYourPhone(UserSearch userSearch) {
        msgService.processMessage(TelegramData.getSendMessage(userSearch.getUserId(),
                "Поделитесь Вашим номером телефона по специальной кнопке снизу или введите ваш телефон в международном формате",
                customerKeyboard.requestContact()));
    }

    public void sendBookingResume(UserSearch userSearch, ClientData data) {
        msgService.processMessage(TelegramData.getEditMessage(userSearch.getUserId(),
                getResume(Last.getLastBooking(data.getMyBooks()), userSearch, data),
                customerKeyboard.getResumeKeyboard(userSearch, data), userSearch.getServiceMsgId()));
    }

    public void sendBookingResume(UserSearch userSearch, ClientData data, int msgId) {
        msgService.processMessage(TelegramData.getDeleteMessage(userSearch.getUserId(), msgId));
        String resume = getResume(Last.getLastBooking(data.getMyBooks()), userSearch, data);
        msgService.processMessage(TelegramData.getEditMessage(userSearch.getUserId(), resume,
                customerKeyboard.getResumeKeyboard(userSearch, data), userSearch.getServiceMsgId()));
    }


    private String getResume(Booking booking, UserSearch userSearch, ClientData data) {
        return "Информация для проверки:\n\nВы бронируете номер № " + booking.getRoomId() + "\n"
                + "Номер на " + userSearch.getPersons() + CorrectForm.getPersonWord(userSearch.getPersons()) + "\n"
                + "Дата заезда: " + userSearch.getCheckIn() + ", после 12-00\n"
                + "Дата выезда: " + userSearch.getCheckOut() + ", до 12-00\n\n"
                + "Ваша контактная информация:\n"
                + "ФИО: " + Last.getLast(data.getFullCustomerNames()) + "\n"
                + "Телефон: " + Last.getLast(data.getContactNumbers()) + "\n\n"

                + (booking.getUserComment() == null ? "" : "Комментарий: " + booking.getUserComment() + "\n\n")

                + "Для подтверждения нажмите \"Подтвердить бронирование\".\n\n"
                + "Если необходимо изменить данные нажмите \"Изменить данные\" и следуйте инструкции." + "\n\n"

                + "Вы также можете удалить этот вариант из поисковой выдачи и продолжить поиск или выбрать другую комнату";
    }

    public void addCommentMessage(UserSearch userSearch) {
        msgService.processMessage(TelegramData.getPopupMessage(userSearch.getInlineId(),
                "Введите дополнительный комментарий по вашему бронированию для менеджера", false));
    }

    public void completeAddComment(UserSearch userSearch, ClientData data, int msgId) {
        msgService.processMessage(TelegramData.getPopupMessage(userSearch.getInlineId(),
                "Комментарий успешно добавлен!", false));
        msgService.processMessage(TelegramData.getDeleteMessage(userSearch.getUserId(), msgId));
        sendBookingResume(userSearch, data);
    }
}