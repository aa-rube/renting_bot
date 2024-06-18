package app.bot.controller;

import app.booking.handler.UserDialogHandler;
import app.bot.config.BotConfig;
import app.bot.handler.CallBackDataHandler;
import app.bot.handler.TextMsgHandler;
import app.bot.helpcentre.HelpCentre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
public class UpdateReceivedController extends TelegramLongPollingBot {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private TextMsgHandler textMsgHandler;

    @Autowired
    private CallBackDataHandler callBackDataHandler;

    @Autowired
    private UserDialogHandler userDialogHandler;

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
        } else if(update.hasMessage() && update.getMessage().isReply()) {
            helpCentre.replayHandle(update);
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            new Thread(() -> userDialogHandler.contactHandler(update)).start();
        } else if (update.hasMessage()) {
            new Thread(() -> textMsgHandler.updateHandler(update)).start();
        }
    }
}