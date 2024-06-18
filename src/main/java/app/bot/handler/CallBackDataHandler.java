package app.bot.handler;

import app.booking.admin.AdminMessageController;
import app.booking.user_controller.controller.BookingController;
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
    private BookingController bookingController;

    @Autowired
    private AdminMessageController adminMessageController;

    public void updateHandler(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String data = update.getCallbackQuery().getData();

        if (data.contains("USER_")) {
            bookingController.handle(update, chatId, data);
        }

        if (data.contains("ADM_")) {
            adminMessageController.handler(update, chatId, data);
        }
    }
}