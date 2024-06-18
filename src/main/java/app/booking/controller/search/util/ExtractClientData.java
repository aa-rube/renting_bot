package app.booking.controller.search.util;

import app.booking.sheets.model.UserSearch;
import app.booking.user.ClientData;
import app.bot.messaging.MessagingService;
import app.bot.messaging.TelegramData;
import app.bot.messaging.data.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class ExtractClientData {
    @Lazy
    @Autowired
    private MessagingService msgService;

    public ClientData updateData(UserSearch search, String formatedString, ClientData clientData, int msgId) {
        String[] info = extractInfo(formatedString);

        if (!Last.getLast(clientData.getContactNumbers()).equals(info[1])) {
            String phoneNumber = PhoneNumberFormatter.formatNumber(info[1]);

            if (phoneNumber == null || phoneNumber.contains("null")) {
                msgService.processMessage(TelegramData.getDeleteMessage(search.getUserId(), msgId));
                msgService.sendPopupMessage(search.getInlineId(), Text.WRONG_PHONE_FORMAT.getText(), false);
                return null;
            } else {
                String lastPhone = Last.getLast(clientData.getContactNumbers()) + " canceled";
                Last.removeLast(clientData.getContactNumbers());
                clientData.getContactNumbers().add(lastPhone);
                clientData.getContactNumbers().add(phoneNumber);
            }
        }

        if (!Last.getLast(clientData.getFullCustomerNames()).equals(info[0])) {
            String newCustomerName = info[0];

            if (newCustomerName.split(" ").length < 1
                    || newCustomerName.contains("null")
                    || newCustomerName.contains("canceled")) {

                msgService.processMessage(TelegramData.getDeleteMessage(search.getUserId(), msgId));
                msgService.sendPopupMessage(search.getInlineId(), Text.WRONG_NAME_FORMAT.getText(), false);
                return null;
            } else {
                String lastName = Last.getLast(clientData.getFullCustomerNames()) + " canceled";
                Last.removeLast(clientData.getFullCustomerNames());

                clientData.getFullCustomerNames().add(lastName);
                clientData.getFullCustomerNames().add(newCustomerName);
            }
        }


        return clientData;
    }

    public static String[] extractInfo(String text) {
        String fioPattern = "ФИО:\\s*(.*)";
        String phonePattern = "Контактный номер:\\s*(.+)";

        Pattern patternFio = Pattern.compile(fioPattern);
        Pattern patternPhone = Pattern.compile(phonePattern);

        Matcher matcherFio = patternFio.matcher(text);
        Matcher matcherPhone = patternPhone.matcher(text);

        String fio = null;
        String phone = null;

        if (matcherFio.find()) {
            fio = matcherFio.group(1);
        }

        if (matcherPhone.find()) {
            phone = matcherPhone.group(1).split("\\r?\\n")[0].trim();
        }

        return new String[]{fio, phone};
    }
}
