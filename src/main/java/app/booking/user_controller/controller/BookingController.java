package app.booking.user_controller.controller;

import app.booking.room.service.MongoDBRoomService;
import app.booking.user_controller.data.message.CustomerMessage;
import app.booking.util.ExtractClientData;
import app.booking.util.Last;
import app.booking.util.PhoneNumberFormatter;
import app.booking.sheets.model.Booking;
import app.booking.user_controller.model.UserSearch;
import app.booking.sheets.repository.GoogleSheetsObjectReader;
import app.booking.sheets.service.BookingService;
import app.booking.user_controller.model.ClientData;
import app.booking.user_controller.model.service.UserDataService;
import app.bot.handler.helpcentre.HelpCentre;
import app.bot.messaging.TelegramData;
import app.booking.util.IntParser;
import app.bot.messaging.MessagingService;
import app.bot.messaging.data.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class BookingController {

    @Lazy
    @Autowired
    private MessagingService msgService;

    @Autowired
    private CustomerMessage customerMessage;

    @Autowired
    private SearchController searchController;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private GoogleSheetsObjectReader googleSheetsObjectReader;

    @Autowired
    private ExtractClientData extractClientData;

    @Autowired
    private HelpCentre helpCentre;

    @Autowired
    private MongoDBRoomService mongoDBRoomService;

    private final CopyOnWriteArraySet<Long> addFullName = new CopyOnWriteArraySet<>();

    private final CopyOnWriteArraySet<Long> shareYourPhone = new CopyOnWriteArraySet<>();

    private final CopyOnWriteArraySet<Long> addComment = new CopyOnWriteArraySet<>();

    public void handle(Update update, Long chatId, String data) {
        int msgId = update.getCallbackQuery().getMessage().getMessageId();
        UserSearch search = searchController.getUserSearch(update, msgId);

        if (data.contains("USER_SRCH_")) {
            searchController.callBackDataHandle(update, chatId, data, msgId);
            return;
        }

        if (data.contains("_DETAILS_")) {
            int objId = IntParser.getIntByString(data, 2);
            customerMessage.sendDetailsById(search, objId, msgId);
        }

        if (data.contains("_STARTBOOKING_")) {
            int objId = IntParser.getIntByString(data, 2);
            search.setServiceMsgId(msgId);
            Booking booking = bookingService.createBookingObject(searchController.getUserSearch(update, msgId), objId);
            ClientData clientData = userDataService.getClientData(chatId, update);

            if (clientData.getMyBooks() == null) {
                clientData.setMyBooks(new ArrayList<>());
            }

            clientData.getMyBooks().add(booking);
            userDataService.save(clientData);

            if (clientData.getFullCustomerNames() != null && clientData.getContactNumbers() != null
                    && !Last.getLast(clientData.getFullCustomerNames()).contains("canceled")
                    && !Last.getLast(clientData.getContactNumbers()).contains("canceled")) {

                customerMessage.sendBookingResume(search, clientData);
                return;
            }

            if (clientData.getFullCustomerNames() == null
                    || Last.getLast(clientData.getFullCustomerNames()).contains("canceled")) {

                addFullName.add(chatId);
                customerMessage.startInputRealClientData(search, objId, msgId);
                return;
            }

            if (clientData.getContactNumbers() == null
                    || Last.getLast(clientData.getContactNumbers()).contains("canceled")) {

                customerMessage.shareYourPhone(searchController.getUserSearch(update, msgId));
                shareYourPhone.add(chatId);
                return;
            }

            return;
        }

        if (data.contains("_DELETEROOM_")) {
            int objId = IntParser.getIntByString(data, 2);
            int linksCount = mongoDBRoomService.findLinksCountByRoomId(objId);

            searchController.getSearchMap().get(chatId).getEscapingRooms().add(objId);
            msgService.deleteSomeMessageFromChat(chatId, msgId, linksCount + 1);
            return;
        }

        if (data.contains("BCKDESCRPTN")) {
            int objId = IntParser.getIntByString(data, 2);
            customerMessage.backToDescription(chatId, objId, msgId);
            return;
        }

        if (data.contains("USER_COMMENT")) {
            addComment.add(chatId);
            customerMessage.addCommentMessage(search);
            return;
        }

        if (data.contains("USER_APPROVEBK")) {
            ClientData clientData = userDataService.getClientData(chatId, update);

            int lastBookingIndex = clientData.getMyBooks().size() - 1;

            clientData.getMyBooks().get(lastBookingIndex)
                    .setFullUserName(Last.getLast(clientData.getFullCustomerNames()));

            clientData.getMyBooks().get(lastBookingIndex)
                    .setPhoneNumber(Last.getLast(clientData.getContactNumbers()));

            Booking booking = Last.getLastBooking(clientData.getMyBooks());
            userDataService.save(clientData);

            customerMessage.makeBooking(booking, search, clientData.getUserName(), msgId);
            bookingService.saveToTheSheet(booking, clientData.getUserName());
        }

        if (data.equals("USER_QUESTION_")) {
            helpCentre.startSupport(chatId);
            msgService.processMessage(TelegramData.getCallbackQueryAnswer(update.getCallbackQuery().getId()));
        }
    }

    public void textHandler(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        int msgId = update.getMessage().getMessageId();

        UserSearch search = searchController.getUserSearch(update, -1);

        if (text.equals("/next")
                || (text.contains("Следующие 3 варианта⏭⏭"))) {

            if (!search.isSearchFilled()) {
                searchController.getUserSearch(update, -1);
                return;
            }

            search.setPage(search.getPage() + 1);
            searchController.getSearchMap().put(chatId, search);
            customerMessage.buildRoomPages(search.getInlineId(), search);
            return;
        }

        if (addFullName.contains(chatId)) {
            ClientData data = userDataService.getClientData(chatId, update);

            if (text.split(" ").length > 1) {

                data.setFullCustomerNames(new ArrayList<>());
                data.getFullCustomerNames().add(text);
                userDataService.save(data);
                addFullName.remove(chatId);
                msgService.deleteSomeMessageFromChat(chatId, msgId, 1);

                if (data.getContactNumbers() == null || Last.getLast(data.getContactNumbers()).contains("canceled")) {
                    shareYourPhone.add(chatId);
                    customerMessage.shareYourPhone(searchController.getUserSearch(update, -1));

                    return;
                } else {
                    customerMessage.sendBookingResume(searchController.getUserSearch(update, -1), data);
                }

            } else {
                msgService.processMessage(TelegramData.getSendMessage(chatId,
                        Text.YOU_GOT_A_TYPO.getText(), null));
            }

            return;
        }

        if (shareYourPhone.contains(chatId)) {
            ClientData data = userDataService.getClientData(chatId, update);

            String phone = PhoneNumberFormatter.formatNumber(text);

            if (phone == null || phone.equals("null")) {
                msgService.processMessage(TelegramData.getPopupMessage(search.getInlineId(),
                        Text.WRONG_PHONE_FORMAT.getText(), false));

                return;
            }

            if (data.getContactNumbers() == null) {
                data.setContactNumbers(new ArrayList<>());
            }

            data.getContactNumbers().add(phone);
            userDataService.save(data);

            msgService.deleteSomeMessageFromChat(chatId, msgId, 2);
            customerMessage.sendBookingResume(search, data);
            shareYourPhone.remove(chatId);
            return;
        }

        if (addComment.contains(chatId)) {
            addComment.remove(chatId);

            ClientData data = userDataService.getClientData(chatId, update);

            Booking lastBooking = Last.getLastBooking(data.getMyBooks());
            Last.removeLastBooking(data.getMyBooks());

            lastBooking.setUserComment(text);
            data.getMyBooks().add(lastBooking);
            userDataService.save(data);

            customerMessage.completeAddComment(search, data, update.getMessage().getMessageId());
            return;
        }

        if (text.contains(Text.CHANGE_CLIENT_DATA_STRING.getText())) {
            ClientData data = extractClientData.updateData(search, text,
                    userDataService.getClientData(chatId, update), msgId);
            if (data == null) return;

            userDataService.save(data);

            msgService.processMessage(TelegramData.getPopupMessage(search.getInlineId(),
                    Text.DATA_UPDATED.getText(), false));

            customerMessage.sendBookingResume(search, data, msgId);
        }
    }

    public void contactHandler(Update update) {
        Long chatId = update.getMessage().getChatId();
        int msgId = update.getMessage().getMessageId();
        UserSearch search = searchController.getUserSearch(update, -1);

        shareYourPhone.remove(chatId);

        ClientData data = userDataService.getClientData(chatId, update);
        String phone = PhoneNumberFormatter.formatNumber(update.getMessage().getContact().getPhoneNumber());

        if (phone == null || phone.equals("null")) {
            msgService.processMessage(TelegramData.getPopupMessage(search.getInlineId(),
                    Text.WRONG_PHONE_FORMAT.getText(), false));
            return;
        }

        if (data.getContactNumbers() == null) {
            data.setContactNumbers(new ArrayList<>());
        }

        data.getContactNumbers().add(phone);
        userDataService.save(data);

        msgService.deleteSomeMessageFromChat(chatId, msgId, 2);
        customerMessage.sendBookingResume(search, data);
    }

}