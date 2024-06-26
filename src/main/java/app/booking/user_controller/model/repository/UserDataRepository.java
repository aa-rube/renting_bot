package app.booking.user_controller.model.repository;

import app.booking.user_controller.model.ClientData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDataRepository extends MongoRepository<ClientData, String> {
    boolean existsByUserIdLong(Long userIdLong);

    Optional<ClientData> findByUserIdLong(Long userIdLong);
}
