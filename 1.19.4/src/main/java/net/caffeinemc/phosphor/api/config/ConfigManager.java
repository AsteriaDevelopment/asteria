package net.caffeinemc.phosphor.api.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.caffeinemc.phosphor.api.font.JColor;
import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.api.util.PlayerUtils;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.Setting;
import net.caffeinemc.phosphor.module.setting.settings.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
    private final Gson GSON = new Gson();
    private final Path pathConfigFolder;
    private final Path pathConfig;
    private JsonObject jsonConfig;

    public ConfigManager() {
        String tempFolderDirectory = System.getProperty("java.io.tmpdir");
        pathConfigFolder = Paths.get(tempFolderDirectory).resolve("radium");
        pathConfig = pathConfigFolder.resolve("radium.json");
    }

    public void loadConfig() {
        try {
            if (!Files.isRegularFile(pathConfig))
                return;

            jsonConfig = GSON.fromJson(Files.readString(pathConfig), JsonObject.class);

            for (Module module : Phosphor.moduleManager().modules) {
                JsonElement moduleJson = jsonConfig.get(module.getName());
                if (moduleJson == null || !moduleJson.isJsonObject())
                    continue;
                JsonObject moduleConfig = moduleJson.getAsJsonObject();

                JsonElement enabledJson = moduleConfig.get("enabled");
                if (enabledJson == null || !enabledJson.isJsonPrimitive())
                    continue;

                if (enabledJson.getAsBoolean())
                    module.enable();

                for (Setting setting : module.settings) {
                    JsonElement settingJson = moduleConfig.get(setting.name);
                    if (settingJson == null || !settingJson.isJsonPrimitive())
                        continue;

                    if (setting instanceof BooleanSetting booleanSetting) {
                        booleanSetting.enabled = settingJson.getAsBoolean();
                    } else if (setting instanceof KeybindSetting keybindSetting) {
                        keybindSetting.setKeyCode(settingJson.getAsInt());
                    } else if (setting instanceof ModeSetting modeSetting) {
                        modeSetting.setMode(settingJson.getAsString());
                    } else if (setting instanceof NumberSetting numberSetting) {
                        numberSetting.setValue(settingJson.getAsDouble());
                    } else if (setting instanceof ColorSetting colorSetting) {
                        colorSetting.setColor(new JColor(settingJson.getAsInt()));
                    }
                }
            }

            JsonElement friendsJson = jsonConfig.get("friends");
            if (friendsJson != null && friendsJson.isJsonObject())
                PlayerUtils.friends = friendsJson.getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig() {
        try {
            Files.createDirectories(pathConfigFolder);
            jsonConfig = new JsonObject();

            for (Module module : Phosphor.moduleManager().modules) {
                JsonObject moduleConfig = new JsonObject();

                moduleConfig.addProperty("enabled", module.isEnabled());
                for (Setting setting : module.settings) {
                    if (setting instanceof BooleanSetting booleanSetting) {
                        moduleConfig.addProperty(setting.getName(), booleanSetting.isEnabled());
                    } else if (setting instanceof KeybindSetting keybindSetting) {
                        moduleConfig.addProperty(setting.getName(), keybindSetting.getKeyCode());
                    } else if (setting instanceof ModeSetting modeSetting) {
                        moduleConfig.addProperty(setting.getName(), modeSetting.getMode());
                    } else if (setting instanceof NumberSetting numberSetting) {
                        moduleConfig.addProperty(setting.getName(), numberSetting.getValue());
                    } else if (setting instanceof ColorSetting colorSetting) {
                        moduleConfig.addProperty(setting.getName(), colorSetting.getColor().getRGB());
                    }
                }

                jsonConfig.add(module.getName(), moduleConfig);
            }

            jsonConfig.add("friends", PlayerUtils.friends);

            Files.writeString(pathConfig, GSON.toJson(jsonConfig));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
