package app.bot.helpcentre;

import app.bot.config.BotConfig;
import app.bot.messaging.MessagingService;
import app.bot.messaging.TelegramData;
import app.bot.messaging.data.Text;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class HelpCentre {

    @Lazy
    @Autowired
    private MessagingService msgService;

    @Autowired
    private BotConfig botConfig;

    @Getter
    private final Map<Long, LocalDateTime> chattingWithAdmin = Collections.synchronizedMap(new HashMap<>());


    @Scheduled(fixedRate = 10000)
    public void autoCloseSupportChat() {
        List<Long> idList = new ArrayList<>();

        chattingWithAdmin.entrySet().stream()
                .filter(data -> LocalDateTime.now().isAfter(data.getValue().plusMinutes(10)))
                .forEach(data -> idList.add(data.getKey()));

        idList.forEach(this::stopSupport);

        idList.clear();
    }

    public void replayHandle(Update update) {
        Long replyToMessageForwardFromChatId = update.getMessage().getReplyToMessage().getForwardFrom().getId();
        msgService.processMessage(getSupportMessage(replyToMessageForwardFromChatId, update.getMessage().getText()));
    }

    private Object getSupportMessage(Long replyToMessageForwardFromChatId, String text) {
        text = "Ответ оператора:\n\n" + text;
        return TelegramData.getSendMessage(replyToMessageForwardFromChatId, text, stopSupportChatKeyboard());
    }

    public void forwardMessage(Message messageContent) {
        ForwardMessage forwardMessage = new ForwardMessage();

        forwardMessage.setChatId(botConfig.getAdminChat());
        forwardMessage.setFromChatId(messageContent.getChatId().toString());
        forwardMessage.setMessageId(messageContent.getMessageId());

        msgService.processMessage(forwardMessage);
    }

    public void startSupport(Long chatId) {
        if (chattingWithAdmin.containsKey(chatId)) return;

        chattingWithAdmin.put(chatId, LocalDateTime.now());
        msgService.processMessage(TelegramData.getSendMessage(chatId,
                Text.START_SUPPORT.getText(), null));
    }

    public void stopSupport(Long chatId) {
        if (!chattingWithAdmin.containsKey(chatId)) return;

        chattingWithAdmin.remove(chatId);
        msgService.processMessage(TelegramData.getSendMessage(chatId,
                "Чат с поддержкой завершен.", null));
    }

    public ReplyKeyboard stopSupportChatKeyboard() {
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Завершить чат с поддержкой");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(keyboardButton);
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
