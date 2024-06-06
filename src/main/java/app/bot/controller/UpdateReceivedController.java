package app.bot.controller;

import app.bot.config.BotConfig;
import app.bot.handler.CallBackDataHandler;
import app.bot.handler.TextMsgHandler;
import app.bot.telegramdata.TelegramData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Controller
public class UpdateReceivedController extends TelegramLongPollingBot {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private TextMsgHandler textMsgHandler;

    @Autowired
    private CallBackDataHandler callBackDataHandler;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            new Thread(() -> callBackDataHandler.updateHandler(update)).start();
            try {
                executeAsync(TelegramData.getCallbackQueryAnswer(update));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        } else if (update.hasMessage()) {
            new Thread(() -> textMsgHandler.updateHandler(update)).start();
        }
    }
}
