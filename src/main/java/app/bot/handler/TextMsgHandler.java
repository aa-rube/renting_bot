package app.bot.handler;

import app.booking.admin.AdminMessageController;
import app.booking.user_controller.data.message.CustomerMessage;
import app.booking.user_controller.controller.SearchController;
import app.booking.user_controller.controller.BookingController;
import app.booking.user_controller.model.service.UserDataService;
import app.bot.config.BotConfig;
import app.bot.handler.helpcentre.HelpCentre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Service
public class TextMsgHandler {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private SearchController searchController;

    @Autowired
    private CustomerMessage customerMessage;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private AdminMessageController adminMessageController;

    @Autowired
    private HelpCentre helpCentre;


    private Long getAdminChatId() {
        return botConfig.getAdminChat();
    }

    public void updateHandler(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if (text != null && (text.equals("/start") || text.equals("/new_search"))) {
            helpCentre.stopSupport(chatId);

            searchController.startSearch(chatId);

            if (!userDataService.clientDataExist(chatId)) {
                userDataService.createClient(update);
            }

            return;
        }

        if (!helpCentre.getChattingWithAdmin().containsKey(chatId)
                && text != null && text.startsWith("/help_me")) {
            helpCentre.startSupport(chatId);
            return;
        }

        if (text != null && helpCentre.getChattingWithAdmin().containsKey(chatId)
        && text.equals("Завершить чат с поддержкой")) {
            helpCentre.stopSupport(chatId);
            return;
        }

        if (helpCentre.getChattingWithAdmin().containsKey(chatId)) {
            helpCentre.getChattingWithAdmin().put(chatId, LocalDateTime.now());
            helpCentre.forwardMessage(update.getMessage());
            return;
        }

        if (text != null && chatId.equals(getAdminChatId())) {
            adminMessageController.textHandler(update, chatId, text);
            return;
        }

        bookingController.textHandler(update);
    }

}