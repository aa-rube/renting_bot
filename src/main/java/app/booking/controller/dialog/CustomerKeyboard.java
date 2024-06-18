package app.booking.controller.dialog;

import app.booking.controller.search.util.ClientDataForKeyboard;
import app.booking.controller.search.util.LastListElement;
import app.booking.sheets.model.Room;
import app.booking.sheets.model.UserSearch;
import app.booking.user.ClientData;
import app.bot.messaging.TelegramData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerKeyboard {

    public InlineKeyboardMarkup getRoomKeyboard(Room room) {
        String[] text = {"Узнать подробности"};
        String[] callBackData = {"USER_DETAILS_" + room.getRoomId()};

        return TelegramData.createInlineKeyboardColumn(text, callBackData);
    }

    public InlineKeyboardMarkup getDetailsKeyboard(UserSearch userSearch, int objId) {
        String[] text = {
                "Забронировать " + getBookingDate(userSearch),
                "Задать вопрос",
                "Удалить из выдачи"
        };

        String[] callBackData = {
                "USER_STARTBOOKING_" + objId,
                "USER_QUESTION_",
                "USER_DELETEROOM_" + objId
        };

        return TelegramData.createInlineKeyboardColumn(text, callBackData);
    }

    private String getBookingDate(UserSearch userSearch) {
        String[] checkIn = userSearch.getCheckIn().toString().split("-");
        String[] checkOut = userSearch.getCheckOut().toString().split("-");

        return "c " + checkIn[1] + "-" + checkIn[2]
                + " до " + checkOut[1] + "-" + checkOut[2];
    }

    public InlineKeyboardMarkup getDetailsKeyboard(int objId) {
        String[] text = {
                "Забронировать ",
                "Задать вопрос",
                "Удалить из выдачи"
        };

        String[] callBackData = {
                "USER_DATE_" + objId,
                "USER_QUESTION_",
                "USER_DELETEROOM_" + objId
        };

        return TelegramData.createInlineKeyboardColumn(text, callBackData);
    }


    public InlineKeyboardMarkup getKeyboardForNameInputMessage(int objId) {
        String[] text = {
                "Задать вопрос",
                "Удалить из выдачи"
        };

        String[] callBackData = {
                "USER_QUESTION_",
                "USER_DELETEROOM_" + objId
        };

        return TelegramData.createInlineKeyboardColumn(text, callBackData);
    }

    public InlineKeyboardMarkup getResumeKeyboard(UserSearch userSearch, ClientData data) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> approveBookingRow = new ArrayList<>();
        InlineKeyboardButton approveBookingBtn = new InlineKeyboardButton();
        approveBookingBtn.setText("Подтвердить бронь");
        approveBookingBtn.setCallbackData("USER_APPROVEBK");
        approveBookingRow.add(approveBookingBtn);

        List<InlineKeyboardButton> changeInfoRow = new ArrayList<>();
        InlineKeyboardButton changeInfoBtn = new InlineKeyboardButton();
        changeInfoBtn.setText("Изменить данные");
        changeInfoBtn.setSwitchInlineQueryCurrentChat(ClientDataForKeyboard.getStringData(data));
        changeInfoRow.add(changeInfoBtn);

        List<InlineKeyboardButton> commentRow = new ArrayList<>();
        InlineKeyboardButton commentBtn = new InlineKeyboardButton();
        commentBtn.setText("Добавить комментарий");
        commentBtn.setCallbackData("USER_COMMENT");
        commentRow.add(commentBtn);

        List<InlineKeyboardButton> questionRow = new ArrayList<>();
        InlineKeyboardButton questionBtn = new InlineKeyboardButton();
        questionBtn.setText("Задать вопрос");
        questionBtn.setCallbackData("USER_QUESTION_");
        questionRow.add(questionBtn);

        List<InlineKeyboardButton> deleteRow = new ArrayList<>();
        InlineKeyboardButton deleteBtn = new InlineKeyboardButton();
        deleteBtn.setText("Удалить из выдачи");
        deleteBtn.setCallbackData("USER_DELETEROOM_" + LastListElement.getLastBooking(data.getMyBooks()).getRoomId());
        deleteRow.add(deleteBtn);

        keyboardMatrix.add(approveBookingRow);
        keyboardMatrix.add(changeInfoRow);
        keyboardMatrix.add(commentRow);
        keyboardMatrix.add(questionRow);
        keyboardMatrix.add(deleteRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public ReplyKeyboard requestContact() {
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Поделиться контактом");
        keyboardButton.setRequestContact(true);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(keyboardButton);
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }


    public ReplyKeyboard getNextPage(int elements) {
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Следующие " + elements + " варианта⏭⏭");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(keyboardButton);
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}