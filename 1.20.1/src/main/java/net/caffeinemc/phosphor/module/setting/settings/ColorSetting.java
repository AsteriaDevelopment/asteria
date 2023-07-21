package net.caffeinemc.phosphor.module.setting.settings;

import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImInt;
import net.caffeinemc.phosphor.api.font.JColor;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.RenderableSetting;
import net.caffeinemc.phosphor.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class ColorSetting extends Setting implements RenderableSetting {
    private JColor color;
    private ArrayList<NumberSetting> sliders = new ArrayList<>();
    private boolean alpha;
    private boolean showSliders;

    public ColorSetting(String name, Module parent, JColor color, boolean alpha) {
        this.name = name;
        this.parent = parent;
        this.color = color;
        this.alpha = alpha;
        this.showSliders = false;

        sliders.addAll(List.of(
                new ColorSliderSetting("Red", this.getName()+"/Red", color.getRed()),
                new ColorSliderSetting("Green", this.getName()+"/Green", color.getGreen()),
                new ColorSliderSetting("Blue", this.getName()+"/Blue", color.getBlue())));

        if (alpha) sliders.add(new ColorSliderSetting("Alpha", this.getName()+"/Alpha", color.getAlpha()));

        parent.addSettings(this);
    }

    private class ColorSliderSetting extends NumberSetting {
        private String imGuiID;

        public ColorSliderSetting(String name, String imGuiID, int value) {
            super(name, null, value, 0, 255, 1);
            this.imGuiID = imGuiID;
        }

        @Override
        public void render() {
            ImGui.pushID(imGuiID);

            ImGui.text(this.name);

            ImInt val = new ImInt((int) this.value);

            ImGui.pushItemWidth(160f);
            boolean changed = ImGui.sliderScalar("", ImGuiDataType.S32, val, (int) minimum, (int) maximum);
            ImGui.popItemWidth();

            if (changed) this.value = val.doubleValue();

            ImGui.popID();
        }
    }

    public JColor getColor() {
        return color;
    }

    public void setColor(JColor color) {
        this.color = color;

        sliders.get(0).setValue(color.getRed());
        sliders.get(1).setValue(color.getGreen());
        sliders.get(2).setValue(color.getBlue());
        if (alpha) sliders.get(3).setValue(color.getAlpha());
    }

    @Override
    public void render() {
        ImGui.pushID(parent.getName()+"/"+this.getName());

        float[] color = getColor().getFloatColorWAlpha();

        ImGui.text(this.getName());

        if (ImGui.colorButton(this.getName(), color)) showSliders = !showSliders;

        if (showSliders) {
            ImGui.indent(10f);

            for (NumberSetting numberSetting : sliders) {
                numberSetting.render();
            }

            ImGui.unindent(10f);

            if (alpha) {
                this.setColor(new JColor(sliders.get(0).getIValue(), sliders.get(1).getIValue(), sliders.get(2).getIValue(), sliders.get(3).getIValue()));
            } else {
                this.setColor(new JColor(sliders.get(0).getIValue(), sliders.get(1).getIValue(), sliders.get(2).getIValue()));
            }

            ImGui.spacing();
        }

        ImGui.popID();
    }
}
