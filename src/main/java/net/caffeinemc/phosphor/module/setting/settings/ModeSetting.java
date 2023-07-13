package net.caffeinemc.phosphor.module.setting.settings;

import java.util.Arrays;

import imgui.ImGui;
import imgui.type.ImInt;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.RenderableSetting;
import net.caffeinemc.phosphor.module.setting.Setting;

public class ModeSetting extends Setting implements RenderableSetting {
	public int index;
	  
	public String[] modes;
	  
	public ModeSetting(String name, Module parent, String defaultMode, String... modes) {
	    this.name = name;
	    this.parent = parent;
	    this.modes = modes;
	    this.index = Arrays.stream(this.modes).toList().indexOf(defaultMode);
		parent.addSettings(this);
	}
	  
	public String getMode() {
	    return this.modes[this.index];
	}
	  
	public void setMode(String mode) {
		this.index = Arrays.stream(this.modes).toList().indexOf(mode);
	}
	  
	public boolean is(String mode) {
	    return (this.index == Arrays.stream(this.modes).toList().indexOf(mode));
	}
	  
	public void cycle() {
	    if (this.index < this.modes.length - 1) {
	      	this.index++;
	    } else {
	      	this.index = 0;
	    }
	}

	@Override
	public void render() {
		ImInt currentItem = new ImInt(this.index);

		ImGui.listBox(this.name, currentItem, modes);

		this.index = currentItem.get();
	}
}