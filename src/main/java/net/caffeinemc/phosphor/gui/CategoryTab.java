package net.caffeinemc.phosphor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.module.Module;

public class CategoryTab implements Renderable {
    public Module.Category category;

    public CategoryTab(Module.Category category) {
        this.category = category;
    }

    @Override
    public String getName() {
        return category.name;
    }

    @Override
    public void render() {
        int imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.AlwaysAutoResize;
        ImGui.begin(getName(), imGuiWindowFlags);

        for (Module module : Phosphor.moduleManager().getModulesByCategory(category)) {
            ImGui.pushID(module.getName());

            if (module.isEnabled()) {
                ImGui.pushStyleColor(ImGuiCol.Button, 0.90f, 0.27f, 0.33f, 0.75f);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.90f, 0.27f, 0.33f, 0.90f);
            } else {
                ImGui.pushStyleColor(ImGuiCol.Button, 0.90f, 0.27f, 0.33f, 0.50f);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.90f, 0.27f, 0.33f, 0.65f);
            }

            boolean isToggled = ImGui.button(module.getName(), 180f, 25f);

            ImGui.popStyleColor(2);

            if (isToggled) {
                module.toggle();
            }

            if (ImGui.isItemHovered()) {
                ImGui.setTooltip(module.getDescription());

                if (ImGui.isMouseClicked(1)) {
                    module.toggleShowOptions();
                }
            }

            if (module.showOptions()) {
                ImGui.indent();

                module.renderSettings();

                ImGui.unindent();
            }

            ImGui.popID();
        }

        ImGui.end();
    }

    @Override
    public Theme getTheme() {
        return RadiumMenu.getInstance().getTheme();
    }
}
