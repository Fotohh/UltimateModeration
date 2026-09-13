package me.xaxis.ultimatemoderation.lang;

import me.xaxis.ultimatemoderation.utils.Utils;

import java.util.Map;

public class LangManager {

    private final Map<LangKey, String> messages;

    public LangManager(Map<LangKey, String> messages) {
        this.messages = messages;
    }

    public String getMessage(LangKey key) {
        return Utils.chat(messages.get(key));
    }

    public String replacePlaceholders(String message, Map<String, String> placeholders) {
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

}