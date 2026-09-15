package me.xaxis.ultimatemoderationplus.lang;

import me.xaxis.ultimatemoderationplus.utils.Utils;

import java.util.Map;
import java.util.Objects;

public class LangManager {

    private final Map<LangKey, String> messages;

    public LangManager(Map<LangKey, String> messages) {

        this.messages = Map.copyOf(
                Objects.requireNonNull(
                        messages,
                        "Messages cannot be null"
                )
        );

    }

    public String getMessage(LangKey key) {
        Objects.requireNonNull(
                key,
                "Language key cannot be null"
        );

        String message = messages.get(key);

        if(message == null) {
            throw new IllegalStateException(
                    "No language message is loaded for key "
                            + key
            );
        }

        return Utils.chat(message);
    }

    public String replacePlaceholders(String message, Map<Placeholders, String> placeholders) {
        String result = message;
        for (Map.Entry<Placeholders, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey().getPlaceholder() + "}", entry.getValue());
        }
        return result;
    }

}