package app.bot.handler;

import app.booking.admin.AdminMessageController;
import app.booking.controller.dialog.CustomerMessage;
import app.booking.controller.search.StartBookingSearch;
import app.booking.handler.UserDialogHandler;
import app.booking.user.service.UserDataService;
import app.bot.config.BotConfig;
import app.bot.helpcentre.HelpCentre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Service
public class TextMsgHandler {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private StartBookingSearch startBookingSearch;

    @Autowired
    private CustomerMessage customerMessage;

    @Autowired
    private UserDialogHandler userDialogHandler;

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

            startBookingSearch.startSearch(chatId);

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

        userDialogHandler.textHandler(update);
    }

}