package net.caffeinemc.phosphor.module.setting.settings;

import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import net.caffeinemc.phosphor.api.font.JColor;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.RenderableSetting;
import net.caffeinemc.phosphor.module.setting.Setting;

public class ColorSetting extends Setting implements RenderableSetting {
    private JColor color;

    public ColorSetting(String name, Module parent, JColor color) {
        this.name = name;
        this.parent = parent;
        this.color = color;
    }

    public JColor getColor() {
        return color;
    }

    public void setColor(JColor color) {
        this.color = color;
    }

    @Override
    public void render() {
        ImGui.pushID(parent.getName()+"/"+this.getName());

        float[] color = {
                getColor().getRed(),
                getColor().getBlue(),
                getColor().getGreen(),
                getColor().getAlpha()
        };

        ImGui.text(this.getName());
        boolean changedColor = ImGui.colorEdit4("", color, ImGuiColorEditFlags.NoInputs);

        if (changedColor)
            this.setColor(new JColor((int) color[0], (int) color[1], (int) color[2], (int) color[3]));

        ImGui.popID();
    }
}
