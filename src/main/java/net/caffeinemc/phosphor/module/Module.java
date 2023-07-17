package net.caffeinemc.phosphor.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import net.caffeinemc.phosphor.gui.*;
import net.caffeinemc.phosphor.module.setting.RenderableSetting;
import net.caffeinemc.phosphor.module.setting.Setting;
import net.caffeinemc.phosphor.module.setting.settings.KeybindSetting;
import net.caffeinemc.phosphor.common.Phosphor;
import net.minecraft.client.MinecraftClient;

public abstract class Module implements Renderable {
	protected MinecraftClient mc = MinecraftClient.getInstance();

	public String name, description;
	public List<Setting> settings = new ArrayList<>();
	public KeybindSetting keybind = new KeybindSetting("Keybind", 0, this);
	private Category category;
	private boolean enabled;
	private boolean showOptions;

	public Module(String name, String description, int key, Category category) {
		this.name = name;
		this.description = description;
		this.category = category;
		keybind.setKeyCode(key);

		enabled = false;
		showOptions = false;
	}

	public Module(String name, String description, Category category) {
		this.name = name;
		this.description = description;
		this.category = category;

		enabled = false;
		showOptions = false;
	}

	public enum Category {
		COMBAT("Combat"), RENDER("Render"), PLAYER("Player"), MISCELLANEOUS("Miscellaneous"), CLIENT("Client");
		public String name;

		Category(String name) {
			this.name = name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public static void clearStrings() {
			COMBAT.setName(null);
			RENDER.setName(null);
			PLAYER.setName(null);
			MISCELLANEOUS.setName(null);
			CLIENT.setName(null);
		}
	}

	public void addSettings(Setting... settings) {
		this.settings.addAll(Arrays.asList(settings));
		this.settings.sort(Comparator.comparingInt(s -> s == keybind ? 1 : 0));
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return this.category;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getKey() {
		return keybind.code;
	}
	
	public void setKey(int key) {
		this.keybind.code = key;
	} 
	
	public void toggle() {
		if (isEnabled()) {
			disable();
		} else {
			enable();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (enabled) {
			Phosphor.EVENTBUS.subscribe(this);
		} else {
			Phosphor.EVENTBUS.unsubscribe(this);
		}
	}

	public boolean showOptions() {
		return showOptions;
	}

	public void toggleShowOptions() {
		this.showOptions = !this.showOptions;
	}
	
	public void enable() {
		onEnable();
		setEnabled(true);
	}

	public void disable() {
		onDisable();
		setEnabled(false);
	}
	
	public void onEnable() {}
	
	public void onDisable() {}

	public boolean nullCheck() {
		return mc.player != null && mc.world != null;
	}

	public void cleanStrings() {
		this.setName(null);
		this.setDescription(null);
		Category.clearStrings();

		for (Setting setting : settings) {
			setting.name = null;
		}
	}

	//public void render() {
	//	if (ImGui.checkbox(this.getName(), this.enabled))
	//		toggle();
	//}


	public void toggleVisibility() {
		if (ImguiLoader.isRendered(this)) {
			ImguiLoader.queueRemove(this);
		} else {
			ImguiLoader.addRenderable(this);
		}
	}


	public void renderSettings() {
		for (Setting setting : this.settings) {
			if (setting instanceof RenderableSetting renderableSetting) {
				renderableSetting.render();
			}
		}
	}

	@Override
	public void render() {
		int imGuiWindowFlags = 0;
		imGuiWindowFlags |= ImGuiWindowFlags.AlwaysAutoResize;
		imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;
		ImGui.begin(getName(), imGuiWindowFlags);
		this.renderSettings();
		ImGui.end();
	}

	@Override
	public Theme getTheme() {
		return RadiumMenu.getInstance().getTheme();
	}
}
