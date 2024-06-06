package app.bot.adminpanel;


import app.bot.adminpanel.data.AdminKeyboard;
import app.bot.adminpanel.invite.InviteRedisRepository;
import app.bot.telegramdata.TelegramData;
import app.bot.data.Text;
import app.bot.util.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class AdminPanelController {
    @Autowired
    @Lazy
    private MessagingService msgService;
    @Autowired
    private AdminKeyboard keyboard;
    @Autowired
    private InviteRedisRepository invite;
    private boolean waitForTheInviteUser;

    public void textHandler(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if (text.equals("/start")) {
            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    Text.ADMIN_GREETING.getText(), keyboard.adminPanelMain()));
            return;
        }

        if (waitForTheInviteUser) {
            int saved = userNameTextString(text);
            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Всего сохранено: ".concat(String.valueOf(saved)).concat("\n\nМожно добавить еще")
                    ,keyboard.backToMainPanel()));
            return;
        }
    }

    public void callBackDataHandler(Update update) {
        Long chatId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        int msgId = update.getCallbackQuery().getMessage().getMessageId();

        if (data.equals("invite")) {
            sendListInvitedUsers(chatId, msgId);
            return;
        }

        if (data.equals("clear_list")) {
            invite.clearAllInviteUsers();
            sendListInvitedUsers(chatId, msgId);
        }

        if (data.equals("back")) {
            waitForTheInviteUser = false;
            msgService.processMessage(TelegramData.getEditMessage(chatId,
                    Text.ADMIN_GREETING.getText(), keyboard.adminPanelMain(), msgId));
            return;
        }

        if (data.equals("static")) {

        }
    }

    private int userNameTextString(String text) {
        if (text.split("\n").length == 1) {
            invite.saveInviteUser(text.replaceAll("@", ""));
            return 1;
        }

        int i = 0;
        for (String s : text.split("\n")) {
            invite.saveInviteUser(s.replaceAll("@", ""));
            i++;
        }

        return i;
    }

    private void sendListInvitedUsers(Long chatId, int msgId) {
        waitForTheInviteUser = true;

        List<String> inviteUsers = invite.getAllInviteUsers();
        String message = "Список приглашенных пользователей:\n\n" +
                (inviteUsers.isEmpty() ? "\t\tКажется список еще пуст" :
                        String.join("\n", inviteUsers)) +
                "\n\n" + Text.INVITE.getText();

        msgService.processMessage(TelegramData.getEditMessage(chatId, message,
                keyboard.backToMainPanel(), msgId));
    }
}
