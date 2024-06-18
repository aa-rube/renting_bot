package app.bot.messaging;

import app.booking.util.Sleep;
import app.bot.controller.UpdateReceivedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
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
            } else if (msg instanceof SendMediaGroup) {
                updateReceivedController.execute((SendMediaGroup) msg);
            } else if (msg instanceof EditMessageCaption) {
                updateReceivedController.executeAsync((EditMessageCaption) msg);
            } else if (msg instanceof ForwardMessage) {
                updateReceivedController.executeAsync((ForwardMessage) msg);
            } else if (msg instanceof AnswerCallbackQuery) {
                updateReceivedController.executeAsync((AnswerCallbackQuery) msg);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void deleteSomeMessageFromChat(Long chatId, int msgId, int count) {
        for (int i = 0; i < count; i++) {
            processMessage(TelegramData.getDeleteMessage(chatId, msgId - i));
        }
    }

}
