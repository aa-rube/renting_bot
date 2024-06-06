package app.bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.processing.Generated;

@Component
@ConfigurationProperties(prefix = "spring.redis")
@Getter
@Setter
public class RedisProperties {
    private String host;
    private int port;
}

