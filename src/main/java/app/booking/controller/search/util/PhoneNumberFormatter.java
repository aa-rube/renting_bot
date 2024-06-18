package app.booking.controller.search.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneNumberFormatter {
    public static String formatNumber(String phoneNumberString) {
        if (!phoneNumberString.contains("+")) {
            phoneNumberString = "+" + phoneNumberString;
        }

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber phoneNumber = phoneNumberUtil.parse(phoneNumberString, "RU");
            if (phoneNumberUtil.isValidNumber(phoneNumber)) {
                return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } else {
                return  null;
            }
        } catch (NumberParseException e) {
            return null;
        }
    }
}
