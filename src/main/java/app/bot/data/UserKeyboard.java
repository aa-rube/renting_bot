package app.bot.data;

import app.bot.telegramdata.TelegramData;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class UserKeyboard {
    public static InlineKeyboardMarkup marketOptions() {
        return TelegramData.createInlineKeyboardLine(TradingPlatforms.PLATFORMS.getNames(),
                TradingPlatforms.PLATFORMS.getNames());
    }
}
