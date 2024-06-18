package app.booking.admin;

import app.bot.messaging.TelegramData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminKeyboard {

    public InlineKeyboardMarkup getBookingOptions(String bookingId) {
        String[] buttons = {
                "Подтвердить бронь и оплату",
                "Отменить"
        };

        String[] callBackData = {
                "ADM_AP_" + bookingId,
                "ADM_RJ_" + bookingId
        };

        return TelegramData.createInlineKeyboardColumn(buttons, callBackData);
    }

    public ReplyKeyboard getMainOptions() {
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Добавить новый объект");

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
