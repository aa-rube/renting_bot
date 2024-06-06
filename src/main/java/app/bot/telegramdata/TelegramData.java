package app.bot.telegramdata;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TelegramData {
    public static Object getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        msg.setReplyMarkup(markup);
        return msg;
    }

    public static Object getSendPhoto(Long chatId, String text, InlineKeyboardMarkup markup, File file) {
        SendPhoto msg = new SendPhoto();
        msg.setChatId(chatId);
        msg.setCaption(text);
        msg.setParseMode(ParseMode.HTML);
        msg.setReplyMarkup(markup);
        msg.setPhoto(new InputFile(file));
        return msg;
    }

    public static Object getEditMessage(Long chatId, String text, InlineKeyboardMarkup markup, int msgId) {
        EditMessageText msg = new EditMessageText();
        msg.setChatId(chatId);
        msg.setMessageId(msgId);
        msg.setText(text);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        msg.setReplyMarkup(markup);
        return msg;
    }


    public static Object getDeleteMessage(Long chatId, int msgId) {
        DeleteMessage delete = new DeleteMessage();
        delete.setChatId(chatId);
        delete.setMessageId(msgId);
        return delete;
    }

    public static Object getEditMessageReplyMarkup(Long chatId, InlineKeyboardMarkup markup, int msgId) {
        EditMessageReplyMarkup msg = new EditMessageReplyMarkup();

        msg.setChatId(chatId);
        msg.setMessageId(msgId);
        msg.setReplyMarkup(markup);
        return msg;
    }

    public static AnswerCallbackQuery getCallbackQueryAnswer(Update update) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(update.getCallbackQuery().getId());
        return answer;
    }

    public static InlineKeyboardMarkup createInlineKeyboardLine(String[] buttonTexts, String[] callbackData) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < buttonTexts.length; i++) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(buttonTexts[i]);
            btn.setCallbackData(callbackData[i]);
            row.add(btn);
        }

        keyboardMatrix.add(row);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public static InlineKeyboardMarkup createInlineKeyboardColumn(String[] buttonTexts, String[] callbackData) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        for (int i = 0; i < buttonTexts.length; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(buttonTexts[i]);
            btn.setCallbackData(callbackData[i]);
            row.add(btn);
            keyboardMatrix.add(row);
        }

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

}
