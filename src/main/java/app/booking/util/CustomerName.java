package app.booking.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class CustomerName {

    public static String getAccountTelegramName(Update update) {
        String result = "";

        try {

            try {
                String firstName = update.getMessage().getFrom().getFirstName();
                if (!firstName.equals("null")) {
                    result = firstName + " ";
                }
            } catch (Exception ignored) {
            }

            try {
                String secondName = update.getMessage().getFrom().getLastName();
                if (!secondName.equals("null")) {
                    result = result + secondName;
                }
            } catch (Exception ignored) {
            }

            if (result.isBlank() || result.isEmpty() || resultIsNull(result)) {
                try {
                    String userName = update.getMessage().getFrom().getUserName();
                    if (!userName.equals("null")) {
                        result = userName;
                    }
                } catch (Exception ignored) {
                }
            }


            if (result.equals("null") || result.isEmpty() || result.isBlank() || resultIsNull(result)) {
                result = "Гость, имя аккаунта не известно";
            }


        } catch (Exception e) {
            result = "Гость, имя аккаунта не известно";
        }

        return result;
    }

    private static boolean resultIsNull(String result) {
        try {
            result.toLowerCase();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static String getUserName(Update update) {
        if (update.hasCallbackQuery()) {
            return "@" + update.getCallbackQuery().getFrom().getUserName();
        }

        return "@" + update.getMessage().getFrom().getUserName();
    }
}
