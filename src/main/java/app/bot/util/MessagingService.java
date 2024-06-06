package app.bot.util;

import app.bot.controller.UpdateReceivedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class MessagingService {

    private final UpdateReceivedController updateReceivedController;

    @Autowired
    public MessagingService(UpdateReceivedController updateReceivedController) {
        this.updateReceivedController = updateReceivedController;
    }

    public void processMessage(Object msg) {
        try {
            if (msg instanceof SendMessage) {
                updateReceivedController.executeAsync((SendMessage) msg);
            } else if (msg instanceof SendPhoto) {
                updateReceivedController.executeAsync((SendPhoto) msg);
            } else if (msg instanceof EditMessageText) {
                updateReceivedController.executeAsync((EditMessageText) msg);
            } else if (msg instanceof DeleteMessage) {
                updateReceivedController.executeAsync((DeleteMessage) msg);
            } else if (msg instanceof EditMessageReplyMarkup) {
                updateReceivedController.executeAsync((EditMessageReplyMarkup) msg);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void processCallBackAnswer(AnswerCallbackQuery answer) {
        try {
            updateReceivedController.executeAsync(answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public int processSendMessageGetInt(SendMessage msg) {
        try {
           return updateReceivedController.execute(msg).getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
