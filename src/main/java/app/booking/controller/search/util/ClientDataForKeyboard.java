package app.booking.controller.search.util;

import app.booking.user.ClientData;
import app.bot.messaging.data.Text;

public class ClientDataForKeyboard {
    public static String getStringData(ClientData data) {
        return Text.CHANGE_CLIENT_DATA_STRING.getText()
                + "\n\nФИО: " + LastListElement.getLastElement(data.getFullCustomerNames())
                +"\nКонтактный номер: " + LastListElement.getLastElement(data.getContactNumbers());
    }

}
