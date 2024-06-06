package app.bot.adminpanel.invite;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class InviteRedisRepository {
    private final Jedis jedis;
    private static final String INVITE_KEY_PREFIX = "invite:";

    public InviteRedisRepository() {
        jedis = new Jedis("localhost", 6379);
    }

    public void saveInviteUser(String username) {
        jedis.set(getInviteKey(username), username);
    }

    public String getInviteUser(String username) {
        return jedis.get(getInviteKey(username));
    }

    public void deleteInviteUser(String username) {
        jedis.del(getInviteKey(username));
    }

    public List<String> getAllInviteUsers() {
        Set<String> keys = jedis.keys(getInviteKey("*"));
        return keys.stream()
                .map(jedis::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void clearAllInviteUsers() {
        Set<String> keys = jedis.keys(getInviteKey("*"));
        if (!keys.isEmpty()) {
            String[] keyArray = keys.toArray(new String[0]);
            jedis.del(keyArray);
        }
    }


    private String getInviteKey(String username) {
        return INVITE_KEY_PREFIX + username;
    }
}