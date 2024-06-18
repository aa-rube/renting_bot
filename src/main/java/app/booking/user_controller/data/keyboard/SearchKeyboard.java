package app.booking.user_controller.data.keyboard;

import app.booking.util.CorrectForm;
import app.booking.user_controller.model.UserSearch;
import app.bot.messaging.TelegramData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchKeyboard {

    public InlineKeyboardMarkup getPersonsKeyboard(UserSearch userSearch) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        keyboardMatrix.add(createRow("Номер на " + userSearch.getPersons()
                + CorrectForm.getPersonWord(userSearch.getPersons()), "no_action_2"));

        keyboardMatrix.add(createButtonRow(
                new String[]{"➖", "➕"},
                new String[]{"USER_SRCH_PERSON-", "USER_SRCH_PERSON+"}
        ));
        keyboardMatrix.add(createRow("Следующий шаг ▶".toUpperCase(), "USER_SRCH_NEXT_1"));

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getCheckInKeyboard(UserSearch userSearch) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        keyboardMatrix.add(createRow("Заезжаем " + userSearch.getCheckIn(), "no_action_3"));
        keyboardMatrix.add(createButtonRow(
                new String[]{"➖ день", "➖ неделя", "➖ месяц"},
                new String[]{"USER_SRCH_CHECKIN_DAY-", "USER_SRCH_CHECKIN_WEEK-", "USER_SRCH_CHECKIN_MONTH-"}
        ));
        keyboardMatrix.add(createButtonRow(
                new String[]{"➕ день", "➕ неделя", "➕ месяц"},
                new String[]{"USER_SRCH_CHECKIN_DAY+", "USER_SRCH_CHECKIN_WEEK+", "USER_SRCH_CHECKIN_MONTH+"}
        ));
        keyboardMatrix.add(createButtonRow(
                new String[]{"◀Предыдущий шаг".toUpperCase()},
                new String[]{"USER_SRCH_NEXT_0"}
        ));
        keyboardMatrix.add(createButtonRow(
                new String[]{"Следующий шаг ▶".toUpperCase()},
                new String[]{"USER_SRCH_NEXT_2"}
        ));

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getCheckOutKeyboard(UserSearch userSearch) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        keyboardMatrix.add(createRow("Выезд " + userSearch.getCheckOut(), "no_action_4"));
        keyboardMatrix.add(createButtonRow(
                new String[]{"➖ день", "➖ неделя", "➖ месяц"},
                new String[]{"USER_SRCH_CHECKOUT_DAY-", "USER_SRCH_CHECKOUT_WEEK-", "USER_SRCH_CHECKOUT_MONTH-"}
        ));
        keyboardMatrix.add(createButtonRow(
                new String[]{"➕ день", "➕ неделя", "➕ месяц"},
                new String[]{"USER_SRCH_CHECKOUT_DAY+", "USER_SRCH_CHECKOUT_WEEK+", "USER_SRCH_CHECKOUT_MONTH+"}
        ));
        keyboardMatrix.add(createRow("◀Предыдущий шаг".toUpperCase(), "USER_SRCH_NEXT_1"));
        keyboardMatrix.add(createRow("Показать варианты\uD83D\uDD0E\uD83D\uDD0E\uD83D\uDD0E", "USER_SRCH_START"));

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup clearFilters() {
        String[] buttonText = {"Сбросить фильтр"};
        String[] callBackData = {"USER_SRCH_CLEAR"};
        return TelegramData.createInlineKeyboardColumn(buttonText, callBackData);
    }

    private List<InlineKeyboardButton> createRow(String text, String callbackData) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        row.add(button);
        return row;
    }

    private List<InlineKeyboardButton> createButtonRow(String[] texts, String[] callbackData) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < texts.length; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(texts[i]);
            button.setCallbackData(callbackData[i]);
            row.add(button);
        }
        return row;
    }
}
