package net.caffeinemc.phosphor.module.modules.client;

import net.caffeinemc.phosphor.api.font.JColor;
import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.gui.AsteriaMenu;
import net.caffeinemc.phosphor.gui.AsteriaNewMenu;
import net.caffeinemc.phosphor.gui.ImguiLoader;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.ButtonSetting;
import net.caffeinemc.phosphor.module.setting.settings.ColorSetting;
import net.caffeinemc.phosphor.module.setting.settings.ModeSetting;
import org.lwjgl.glfw.GLFW;

public class AsteriaSettingsModule extends Module {
    public final ColorSetting color = new ColorSetting("Color", this, new JColor(0.90f, 0.27f, 0.33f), false);

    public final ButtonSetting destruct = new ButtonSetting("Destruct", this, () -> {
        AsteriaMenu.stopClient();
    }, true);
    public final ButtonSetting hide = new ButtonSetting("Hide", this, () -> {
        AsteriaMenu.toggleVisibility();
    });
    public final ButtonSetting saveSettings = new ButtonSetting("Save Config", this, () -> {
        Phosphor.configManager().saveConfig();
    });

    public final ModeSetting mode = new ModeSetting("Mode", this, "ClickGUI", "ClickGUI", "Menu");
    public AsteriaSettingsModule() {
        super("Asteria", "Asteria's settings", GLFW.GLFW_KEY_F8, Category.CLIENT);
    }

    @Override
    public void onEnable() {
        if (mode.getMode().equals("ClickGUI")) {
            if (!ImguiLoader.isRendered(AsteriaMenu.getInstance())) {
                AsteriaMenu.toggleVisibility();
            }
            if (ImguiLoader.isRendered(AsteriaNewMenu.getInstance())) {
                AsteriaNewMenu.toggleVisibility();
            }
        } else {
            if (ImguiLoader.isRendered(AsteriaMenu.getInstance())) {
                AsteriaMenu.toggleVisibility();
            }
            if (!ImguiLoader.isRendered(AsteriaNewMenu.getInstance())) {
                AsteriaNewMenu.toggleVisibility();
            }
        }
        mc.mouse.unlockCursor();
    }

    public void updateMode() {
        if (this.isEnabled()) {
            if (mode.getMode().equals("ClickGUI")) {
                if (!ImguiLoader.isRendered(AsteriaMenu.getInstance())) {
                    AsteriaMenu.toggleVisibility();
                }
                if (ImguiLoader.isRendered(AsteriaNewMenu.getInstance())) {
                    AsteriaNewMenu.toggleVisibility();
                }
            } else {
                if (ImguiLoader.isRendered(AsteriaMenu.getInstance())) {
                    AsteriaMenu.toggleVisibility();
                }
                if (!ImguiLoader.isRendered(AsteriaNewMenu.getInstance())) {
                    AsteriaNewMenu.toggleVisibility();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (ImguiLoader.isRendered(AsteriaMenu.getInstance())) {
            AsteriaMenu.toggleVisibility();
        }
        if (ImguiLoader.isRendered(AsteriaNewMenu.getInstance())) {
            AsteriaNewMenu.toggleVisibility();
        }
        if (mc.currentScreen == null) mc.mouse.lockCursor();
    }
}
