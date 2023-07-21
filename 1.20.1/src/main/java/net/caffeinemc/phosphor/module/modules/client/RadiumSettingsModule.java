package net.caffeinemc.phosphor.module.modules.client;

import net.caffeinemc.phosphor.api.font.JColor;
import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.gui.RadiumMenu;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.ButtonSetting;
import net.caffeinemc.phosphor.module.setting.settings.ColorSetting;
import org.lwjgl.glfw.GLFW;

public class RadiumSettingsModule extends Module {
    public final ColorSetting color = new ColorSetting("Color", this, new JColor(0.90f, 0.27f, 0.33f), false);

    public final ButtonSetting destruct = new ButtonSetting("Destruct", this, () -> {
        RadiumMenu.stopClient();
    }, true);
    public final ButtonSetting hide = new ButtonSetting("Hide", this, () -> {
        RadiumMenu.toggleVisibility();
    });
    public final ButtonSetting saveSettings = new ButtonSetting("Save Config", this, () -> {
        Phosphor.configManager().saveConfig();
    });

    public RadiumSettingsModule() {
        super("Radium", "Radium's settings", GLFW.GLFW_KEY_F8, Category.CLIENT);
    }

    @Override
    public void onEnable() {
        RadiumMenu.toggleVisibility();
    }

    @Override
    public void onDisable() {
        RadiumMenu.toggleVisibility();
    }
}
