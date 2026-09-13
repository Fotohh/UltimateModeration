package me.xaxis.ultimatemoderation.lang;

import java.util.Map;

public class LangManager {

    private final Map<LangKey, String> messages;

    public LangManager(LangYml langYml) {
        this.messages = langYml.loadMessages();
    }

    public String getMessage(LangKey key) {
        return messages.get(key);
    }

    public String replacePlaceholders(String message, Map<String, String> placeholders) {
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

}