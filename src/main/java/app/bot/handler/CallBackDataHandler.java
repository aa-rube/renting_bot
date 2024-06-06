package app.bot.handler;

import app.bot.adminpanel.AdminPanelController;
import app.bot.config.BotConfig;
import app.bot.telegramdata.TelegramData;
import app.bot.util.MessagingService;
import app.security.Authorisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Service
public class CallBackDataHandler {
    @Autowired
    @Lazy
    private MessagingService msgService;
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private AdminPanelController adminPanel;
    @Autowired
    private Authorisation authorisation;

    private Long getAdminChatId() {
        return botConfig.getAdmin();
    }

    public void updateHandler(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        System.out.println(LocalDateTime.now() + ", " + update.getCallbackQuery().getData());

        if (chatId.equals(getAdminChatId())) {
            adminPanel.callBackDataHandler(update);
            return;
        }

        authorisation.handleCallbackQuery(update);
    }
}