package app.booking.admin;

import app.booking.sheets.model.Room;
import app.booking.sheets.repository.GoogleSheetsObjectReader;
import app.booking.sheets.service.BookingService;
import app.booking.user_controller.model.service.UserDataService;
import app.bot.messaging.MessagingService;
import app.bot.messaging.TelegramData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Service
public class AdminMessageController {

    @Lazy
    @Autowired
    private MessagingService msgService;

    @Lazy
    @Autowired
    private AdminMessage adminMessage;

    @Autowired
    private AdminKeyboard adminKeyboard;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private GoogleSheetsObjectReader googleSheetsObjectReader;

    private final HashMap<Long, Room> newRoom = new HashMap<>();
    private final HashMap<Long, Integer> steps = new HashMap<>();

    public void handler(Update update, Long chatId, String data) {
        int msgId = update.getCallbackQuery().getMessage().getMessageId();

        if (data.contains("ADM_AP_")) {
            String[] splitData = data.split("_");
            Long userId = Long.valueOf(splitData[2]);
            int roomId = Integer.parseInt(splitData[3]);

            String bookingId = userId + "_" + roomId + "_" + splitData[4];
            String newText = bookingService.changeTheStatus(userId, bookingId, getUserName(update), true);
            if (newText == null) return;

            updateAdminMsg(bookingId, newText, chatId, msgId);
            return;
        }

        if (data.contains("ADM_RJ_")) {
            String[] splitData = data.split("_");
            Long userId = Long.valueOf(splitData[2]);
            int roomId = Integer.parseInt(splitData[3]);

            String bookingId = userId + "_" + roomId + "_" + splitData[4];
            String newText = bookingService.changeTheStatus(userId, bookingId, getUserName(update), false);
            if (newText == null) return;

            updateAdminMsg(bookingId, newText, chatId, msgId);
        }
    }


    public void textHandler(Update update, Long chatId, String text) {
        Long userChatId = update.getMessage().getFrom().getId();

        if (text.equals("/admin")) {
            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Бот аренды жилья приветствует Вас!\nВнизу экрана главные кнопки управления ботом",
                    adminKeyboard.getMainOptions()));
            return;
        }

        if (text.equals("Добавить новый объект")) {
            newRoom.put(userChatId, new Room());
            steps.put(userChatId, 1);

            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Шаг 1.\nВведите название отеля", null));
            return;
        }

        if (steps.get(userChatId) == null || !newRoom.containsKey(userChatId)) return;

        if (steps.get(userChatId) == 1 && newRoom.containsKey(userChatId)) {
            newRoom.get(userChatId).setHotel(text);
            steps.put(userChatId, 2);

            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Шаг 2.\nВведите название для комнаты", null));
            return;
        }

        if (steps.get(userChatId) == 2 && newRoom.containsKey(userChatId)) {
            newRoom.get(userChatId).setPubName(text);
            steps.put(userChatId, 3);

            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Шаг 3. \nВведите описание комнаты", null));
            return;
        }

        if (steps.get(userChatId) == 3 && newRoom.containsKey(userChatId)) {
            newRoom.get(userChatId).setPubDescription(text);
            steps.put(userChatId, 4);

            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Шаг 4. \nВведите ссылки на изображения комнаты (вводить нужно через запятую)", null));
            return;
        }

        if (steps.get(userChatId) == 4 && newRoom.containsKey(userChatId)) {

            if (text.contains("https:") || text.contains("http:")) {
                if (text.split(",").length > 1) {
                    newRoom.get(userChatId).setLinks(new ArrayList<>());
                    newRoom.get(userChatId).setLinks(Arrays.stream(text.split(",")).toList());
                }else {
                    newRoom.get(userChatId).setLinks(new ArrayList<>());
                    newRoom.get(userChatId).getLinks().add(text);
                }

                steps.put(userChatId, 5);
                msgService.processMessage(TelegramData.getSendMessage(chatId,
                        "Шаг 5. \nВведите цену за сутки с валютой, например \"100$\"", null));
                return;
            }

            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Вы ввели что-то не похожее на ссылку. Повторите ввод", null));
            return;
        }

        if (steps.get(userChatId) == 5 && newRoom.containsKey(userChatId)) {
            newRoom.get(userChatId).setPrice(text);
            steps.put(userChatId, 6);
            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Шаг 6. \nВведите описание вида из окна в номере", null));
            return;
        }

        if (steps.get(userChatId) == 6 && newRoom.containsKey(userChatId)) {
            newRoom.get(userChatId).setView(text);
            steps.put(userChatId, 7);

            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Шаг 7. \nВведите доп.комментарий", null));
            return;
        }

        if (steps.get(userChatId) == 7 && newRoom.containsKey(userChatId)) {
            newRoom.get(userChatId).setComment(text);

            steps.put(userChatId, 8);
            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Шаг 8. \nУкажите максимально допустимое количество человек", null));
            return;
        }

        if (steps.get(userChatId) == 8 && newRoom.containsKey(userChatId)) {
            int count;
            try {
                count = Integer.parseInt(text);
            } catch (Exception e) {
                msgService.processMessage(TelegramData.getSendMessage(chatId,
                        "Введите только число", null));
                return;
            }

            newRoom.get(userChatId).setMaxPersonsCount(count);
            steps.remove(userChatId);

            try {
                googleSheetsObjectReader.addRoom(newRoom.get(userChatId));
                msgService.processMessage(TelegramData.getSendMessage(chatId,
                        "Данные успешно сохранены!\nКомната появится в боте через 10 минут!",
                        null));
                newRoom.remove(userChatId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    private void updateAdminMsg(String bookingId, String text, Long chatId, int msgId) {
        msgService.processMessage(TelegramData.getEditMessage(chatId, text,
                adminKeyboard.getBookingOptions(bookingId),msgId));
    }

    private String getUserName(Update update) {
        return "@" + update.getCallbackQuery().getFrom().getUserName();
    }

}
