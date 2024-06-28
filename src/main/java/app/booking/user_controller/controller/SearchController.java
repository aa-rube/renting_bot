package app.booking.user_controller.controller;

import app.booking.user_controller.data.keyboard.SearchKeyboard;
import app.booking.user_controller.data.message.CustomerMessage;
import app.booking.util.CorrectForm;
import app.booking.user_controller.model.UserSearch;
import app.booking.util.Sleep;
import app.bot.messaging.data.Text;
import app.bot.messaging.TelegramData;
import app.bot.messaging.MessagingService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class SearchController {
    @Lazy
    @Autowired
    private MessagingService msgService;

    @Autowired
    private SearchKeyboard searchKeyboard;

    @Autowired
    private CustomerMessage customerMessage;

    @Getter
    private final ConcurrentHashMap<Long, UserSearch> searchMap = new ConcurrentHashMap<>();

    @Getter
    private final CopyOnWriteArraySet<Long> clearFilters = new CopyOnWriteArraySet<>();

    public UserSearch getUserSearch(Update update, int msgId) {
        String inlineId;
        Long userId;

        if (update.hasCallbackQuery()) {
            inlineId = update.getCallbackQuery().getId();
            userId = update.getCallbackQuery().getFrom().getId();
        } else {
            userId = update.getMessage().getFrom().getId();

            if (searchMap.get(userId) != null) {
                inlineId = searchMap.get(userId).getInlineId();
            } else {
                inlineId = "";
            }
        }

        try {
            getSearchMap().get(userId).setInlineId(inlineId);
            return searchMap.get(userId);
        } catch (Exception e) {
            searchMap.put(userId, newSearch(userId, msgId));
            getSearchMap().get(userId).setInlineId(inlineId);
            return searchMap.get(userId);
        }
    }

    public void startSearch(Long chatId, int msgId) {
        if (searchMap.containsKey(chatId) && !clearFilters.contains(chatId)) {
            clearFilters.add(chatId);
            msgService.processMessage(TelegramData.getSendMessage(chatId,
                    "Все фильтры поиска будут сброшены.\nПродолжить?", searchKeyboard.clearFilters()));
            return;
        }

        clearFilters.remove(chatId);
        newSearch(chatId, msgId);
    }

    private UserSearch newSearch(Long chatId, int msgId) {
        UserSearch search = new UserSearch(
                chatId,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                1,
                new ArrayList<>(),
                false
        );

        searchMap.put(chatId, search);
        if (msgId == 1) {
            msgService.processMessage(TelegramData.getSendMessage(chatId, Text.START_SEARCH.getText(),
                    searchKeyboard.getPersonsKeyboard(search)));
        } else {
            msgService.processMessage(TelegramData.getEditMessage(chatId, Text.START_SEARCH.getText(),
                    searchKeyboard.getPersonsKeyboard(search), msgId));
        }

        return search;
    }

    public void callBackDataHandle(Update update, Long chatId, String data, int msgId) {
        String inlineId = update.getCallbackQuery().getId();
        UserSearch search = searchMap.get(chatId);
        search.setInlineId(inlineId);
        searchMap.put(chatId, search);

        if (data.contains("_PERSON")) {
            updatePersons(search, data);
            msgService.processMessage(TelegramData.getEditMessageReplyMarkup(chatId,
                    searchKeyboard.getPersonsKeyboard(search), msgId));
        } else if (data.contains("_CHECK")) {
            dataAdjust(search, data, chatId, msgId);
        } else if (data.contains("_NEXT_")) {
            changePage(search, chatId, data, msgId);
        } else if (data.contains("USER_SRCH_START")) {
            search.setSearchFilled(true);
            searchMap.put(chatId, search);
            customerMessage.buildRoomPages(inlineId, search);
            msgService.processMessage(TelegramData.getTimerDeleteMessage(chatId, msgId, 5000L));
        } else if (data.contains("USER_SRCH_CLEAR")) {
            restartSearch(chatId, msgId);
        }
    }

    public void restartSearch(Long chatId, int msgId) {
        clearFilters.remove(chatId);
        newSearch(chatId, msgId);
    }

    private void updatePersons(UserSearch search, String data) {
        int p = search.getPersons();
        if (data.contains("+")) {
            p++;
        } else {
            p--;
            if (p < 1) p = 1;
        }
        search.setPersons(p);
    }

    private void changePage(UserSearch search, Long chatId, String data, int msgId) {
        if (data.contains("0")) {
            msgService.processMessage(TelegramData.getEditMessage(chatId, "•Шаг 1\n\nУкажите количество человек",
                    searchKeyboard.getPersonsKeyboard(search), msgId));
        } else if (data.contains("1")) {
            msgService.processMessage(TelegramData.getEditMessage(chatId, getTextForStep1(search),
                    searchKeyboard.getCheckInKeyboard(search), msgId));
        } else if (data.contains("2")) {
            msgService.processMessage(TelegramData.getEditMessage(chatId, getTextForStep2(search),
                    searchKeyboard.getCheckOutKeyboard(search), msgId));
        }
    }

    private void dataAdjust(UserSearch search, String data, Long chatId, int msgId) {
        if (data.contains("_CHECKIN_DAY") || data.contains("_CHECKIN_WEEK") || data.contains("_CHECKIN_MONTH")) {
            adjustCheckInDate(search, data);
            msgService.processMessage(TelegramData.getEditMessage(chatId, getTextForStep1(search),
                    searchKeyboard.getCheckInKeyboard(search), msgId));
        } else if (data.contains("_CHECKOUT_DAY") || data.contains("_CHECKOUT_WEEK") || data.contains("_CHECKOUT_MONTH")) {
            adjustCheckOutDate(search, data);
            msgService.processMessage(TelegramData.getEditMessage(chatId, getTextForStep2(search),
                    searchKeyboard.getCheckOutKeyboard(search), msgId));
        }
    }

    private void adjustCheckInDate(UserSearch search, String data) {
        LocalDate checkIn = search.getCheckIn();
        LocalDate checkOut = search.getCheckOut();
        if (data.contains("+")) {
            if (data.contains("_DAY")) {
                checkIn = checkIn.plusDays(1);
                checkOut = checkOut.plusDays(1);
            } else if (data.contains("_WEEK")) {
                checkIn = checkIn.plusWeeks(1);
                checkOut = checkOut.plusWeeks(1);
            } else if (data.contains("_MONTH")) {
                checkIn = checkIn.plusMonths(1);
                checkOut = checkOut.plusMonths(1);
            }
        } else {
            if (data.contains("_DAY")) {
                checkIn = checkIn.minusDays(1);
                checkOut = checkOut.minusDays(1);
            } else if (data.contains("_WEEK")) {
                checkIn = checkIn.minusWeeks(1);
                checkOut = checkOut.minusWeeks(1);
            } else if (data.contains("_MONTH")) {
                checkIn = checkIn.minusMonths(1);
                checkOut = checkOut.minusMonths(1);
            }
            if (checkIn.isBefore(LocalDate.now())) {
                checkIn = LocalDate.now();
                checkOut = LocalDate.now().plusDays(1);
            }
        }
        search.setCheckIn(checkIn);
        search.setCheckOut(checkOut);
    }

    private void adjustCheckOutDate(UserSearch search, String data) {
        LocalDate checkOut = search.getCheckOut();
        if (data.contains("+")) {
            if (data.contains("_DAY")) {
                checkOut = checkOut.plusDays(1);
            } else if (data.contains("_WEEK")) {
                checkOut = checkOut.plusWeeks(1);
            } else if (data.contains("_MONTH")) {
                checkOut = checkOut.plusMonths(1);
            }
        } else {
            if (data.contains("_DAY")) {
                checkOut = checkOut.minusDays(1);
            } else if (data.contains("_WEEK")) {
                checkOut = checkOut.minusWeeks(1);
            } else if (data.contains("_MONTH")) {
                checkOut = checkOut.minusMonths(1);
            }
            if (checkOut.isBefore(search.getCheckIn())) {
                checkOut = search.getCheckOut();
            }
        }
        search.setCheckOut(checkOut);
    }

    private String getTextForStep1(UserSearch search) {
        return "Номер на " + search.getPersons() + CorrectForm.getPersonWord(search.getPersons()) + " \n\nШаг 2\n•Укажите дату заезда:";
    }

    private String getTextForStep2(UserSearch search) {
        return "Номер на " + search.getPersons() + CorrectForm.getPersonWord(search.getPersons()) + "\n\nДата заезда: " + search.getCheckIn()
                + "\n\nШаг 3\n•Укажите дату выезда:";
    }
}