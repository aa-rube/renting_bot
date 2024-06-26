package app.bot.controller;

import app.booking.user_controller.controller.BookingController;
import app.bot.config.BotConfig;
import app.bot.handler.CallBackDataHandler;
import app.bot.handler.TextMsgHandler;
import app.bot.handler.helpcentre.HelpCentre;
import app.bot.messaging.TelegramData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
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

    @Autowired
    private BookingController bookingController;

    @Autowired
    private HelpCentre helpCentre;

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
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            new Thread(() -> bookingController.contactHandler(update)).start();
        } else if(update.hasMessage() && update.getMessage().isReply()) {
            helpCentre.replayHandle(update);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            new Thread(() -> textMsgHandler.updateHandler(update)).start();
        }
    }
}