package me.xaxis.ultimatemoderation.lang;

import me.xaxis.ultimatemoderation.validation.YamlValidator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.Path;
import java.util.List;

public class LangValidator extends YamlValidator {

    private static final int CONFIG_VERSION = 1;

    public LangValidator(Path path, YamlConfiguration configuration) {
        super(path, configuration, CONFIG_VERSION);

        addValidation(this::validateMessages);
    }

    private void validateMessages(List<String> errors){

        if(!configuration.isSet("messages")){
            errors.add("Missing 'messages' section in " + path.getFileName());
            return;
        }

        if (!configuration.isConfigurationSection("messages")) {
            errors.add("'messages' must be a section in " + path.getFileName());
            return;
        }

        for (LangKey key : LangKey.values()) {
            if (!configuration.isSet(key.getPath())) {
                errors.add("Missing message for key: " + key.name());
                continue;
            }

            if(!configuration.isString(key.getPath())) {
                errors.add("Message for key: " + key.name() + " is not a string.");
            }
        }


    }
}
