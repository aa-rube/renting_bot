package app.bot.handler;

import app.booking.admin.AdminMessageController;
import app.booking.handler.UserDialogHandler;
import app.bot.messaging.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class CallBackDataHandler {
    @Autowired
    @Lazy
    private MessagingService msgService;

    @Autowired
    private UserDialogHandler userDialogHandler;

    @Autowired
    private AdminMessageController adminMessageController;

    public void updateHandler(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String data = update.getCallbackQuery().getData();

        if (data.contains("USER_")) {
            userDialogHandler.handle(update, chatId, data);
        }

        if (data.contains("ADM_")) {
            adminMessageController.handler(update, chatId, data);
        }
    }
}