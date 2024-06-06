package app.bot.data;

import lombok.Getter;

@Getter
public enum Text {
    GREETING("\uD83E\uDD16 Добро пожаловать в TRADING BOT | CRYPTO CURRENCY!\n\n\uD83C\uDFAE Здесь ты можешь найти много разных  валют для торговли!"),
    ADMIN_GREETING("Hello!"),
    INVITE("Введите имя пользователя, которого хотите пригласить:");
    private final String text;
    Text(String text) {
        this.text = text;
    }

}
