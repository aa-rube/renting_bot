package app.booking.user.service;

import app.booking.user.ClientData;
import app.booking.user.repository.UserDataRepository;
import app.booking.util.CustomerName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Service
public class UserDataService {
    @Autowired
    private UserDataRepository repository;

    public void save(ClientData data) {
        repository.save(data);
    }

    public boolean clientDataExist(Long userIdLong) {
        return repository.existsByUserIdLong(userIdLong);
    }

    public Optional<ClientData> findByUserIdLong(Long userIdLong) {
        return repository.findByUserIdLong(userIdLong);
    }

    public ClientData createClient(Update update) {
        ClientData data = new ClientData();
        data.setUserIdLong(update.getMessage().getChatId());

        data.setUserName(CustomerName.getUserName(update));
        data.setAccountTelegramName(CustomerName.getAccountTelegramName(update));


        save(data);
        return data;
    }

    public ClientData getClientData(Long chatId, Update update) {
        Optional<ClientData> dataOptional = findByUserIdLong(chatId);
        return dataOptional.orElseGet(() -> createClient(update));
    }

    public Optional<ClientData> findClientByLongUserId(Long userId) {
        return repository.findByUserIdLong(userId);
    }
}
