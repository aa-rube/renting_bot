package app.bot.adminpanel.data;

import app.bot.telegramdata.TelegramData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class AdminKeyboard {

    public InlineKeyboardMarkup adminPanelMain() {
        String[] buttonTexts = {"Пригласить", "Статистика"};
        String[] callBackData = {"invite", "static"};
        return TelegramData.createInlineKeyboardLine(buttonTexts, callBackData);
    }

    public InlineKeyboardMarkup backToMainPanel() {
        String[] buttonTexts = {"Назад", "Очистить список"};
        String[] callBackData = {"back", "clear_list"};
        return TelegramData.createInlineKeyboardLine(buttonTexts, callBackData);
    }
}
