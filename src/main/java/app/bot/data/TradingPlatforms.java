package app.bot.data;

import lombok.Getter;

@Getter
public enum TradingPlatforms {
    PLATFORMS(new String[]{"MEXC", "BYBIT", "GATE"});
    private final String[] names;
    TradingPlatforms(String[] names) {
        this.names = names;
    }
}
