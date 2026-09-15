package me.xaxis.ultimatemoderationplus.validation;

import me.xaxis.ultimatemoderationplus.constants.ConfigConstants;
import me.xaxis.ultimatemoderationplus.lang.LangKey;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.Path;
import java.util.List;

public class LangValidator extends YamlValidator {

    public LangValidator(Path path, YamlConfiguration configuration) {
        super(path, configuration, ConfigConstants.LANG.currentVersion());
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

    @Override
    protected void validateFile(List<String> errors) {

        validateMessages(errors);
    }
}
