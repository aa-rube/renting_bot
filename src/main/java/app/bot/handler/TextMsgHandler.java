package app.bot.handler;

import app.bot.adminpanel.AdminPanelController;
import app.bot.adminpanel.invite.InviteRedisRepository;
import app.bot.config.BotConfig;
import app.trading.db.service.UserTraderService;
import app.trading.util.TraderSeeder;
import app.security.data.AuthMessages;
import app.security.Authorisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TextMsgHandler {
    @Autowired
    private BotConfig config;
    @Autowired
    private AdminPanelController adminPanel;
    @Autowired
    private Authorisation authorisation;
    @Autowired
    private AuthMessages authMessages;
    @Autowired
    private UserTraderService userTraderService;
    @Autowired
    private InviteRedisRepository inviteRepository;

    private Long getAdminCatId() {
       return config.getAdmin();
    }

    public void updateHandler(Update update) {
        if (update.getMessage().getChatId().equals(getAdminCatId())) {
            adminPanel.textHandler(update);
            return;
        }

        if (update.getMessage().getText().equals("/start")) {
            String user = update.getMessage().getFrom().getUserName();
            Long chatId = update.getMessage().getChatId();

            if (userTraderService.findTrader(chatId).isPresent()) {
                authMessages.sendGreetingMessage(chatId, "", -1);
                return;
            }

            if (inviteRepository.getInviteUser(user) == null) return;
            inviteRepository.deleteInviteUser(user);

            userTraderService.save(TraderSeeder.createNewTrader(update, user));
            authMessages.sendGreetingMessage(chatId,
                    "Благодарим за регистрацию в нашем торговом боте! Для продолжения выберите биржу!\n\n",
                    -1);
            return;
        }

        authorisation.handleTextMessage(update);
    }
}