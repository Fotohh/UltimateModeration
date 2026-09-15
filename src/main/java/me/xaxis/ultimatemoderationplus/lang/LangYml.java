package me.xaxis.ultimatemoderationplus.lang;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class LangYml {

    private final YamlConfiguration configuration;

    public LangYml(YamlConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    public Map<LangKey, String> loadMessages() {
        Map<LangKey, String> messages = new EnumMap<>(LangKey.class);

        for(LangKey key : LangKey.values()) {
            messages.put(
                    key,
                    configuration.getString(key.getPath())
            );
        }

        return Map.copyOf(messages);
    }
}
