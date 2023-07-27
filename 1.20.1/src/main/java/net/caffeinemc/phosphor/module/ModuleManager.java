package net.caffeinemc.phosphor.module;

import java.util.ArrayList;
import java.util.List;

import net.caffeinemc.phosphor.module.modules.misc.PingSpoofModule;
import net.caffeinemc.phosphor.module.modules.misc.FakePlayerModule;
import net.minecraft.client.gui.screen.ChatScreen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import net.caffeinemc.phosphor.api.event.events.KeyPressEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.gui.AsteriaMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import net.caffeinemc.phosphor.module.modules.client.*;
import net.caffeinemc.phosphor.module.modules.combat.*;
import net.caffeinemc.phosphor.module.modules.player.*;

@SuppressWarnings("unchecked")
public class ModuleManager {
	
	public ArrayList<Module> modules;
	
	public ModuleManager() {
		modules = new ArrayList<>();
		addModules();
	}

	public boolean isModuleEnabled(Class<? extends Module> moduleClass) {
		Module m = getModule(moduleClass);
		return m != null ? m.isEnabled() : false;
	}

	public boolean isModuleEnabled(String name) {
		Module m = modules.stream().filter(mm->mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		return m != null ? m.isEnabled() : false;
	}

	@Nullable
	public <T extends Module> T getModule(Class<T> moduleClass) {
		for (Module module : modules) {
			if (moduleClass.isAssignableFrom(module.getClass())) {
				return (T) module;
			}
		}
		return null;
	}

	@Nullable
	public Module getModule(String name) {
		for (Module m : modules) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}

		return null;
	}

	public ArrayList<Module> getModules() {
		return new ArrayList<>(modules);
	}

	public List<Module> getModulesByCategory(Module.Category c) {
		List<Module> modules = new ArrayList<>();

		for (Module m : this.modules) {
			if (m.getCategory().equals(c))
				modules.add(m);
		}

		return modules;
	}

	public void addModule(Module module) {
		modules.add(module);
	}

	public void addModules() {
		// COMBAT
		addModule(new AimAssistModule());
		addModule(new AutoAnchorModule());
		addModule(new AutoCrystalModule());
		addModule(new AutoDoubleHandModule());
		addModule(new AutoHitCrystalModule());
		addModule(new AutoJumpResetModule());
		addModule(new AutoPotModule());
		addModule(new AutoTotemModule());
		addModule(new AxeSpamModule());
		addModule(new BlinkModule());
		addModule(new ChestStealerModule());
		addModule(new HitboxesModule());
		addModule(new InventoryTotemModule());
		addModule(new PotRefillModule());
		addModule(new ReachModule());
		addModule(new SilentAimModule());
		addModule(new SnowballThrowModule());
		addModule(new TriggerBotModule());
		addModule(new WebMacroModule());

		// RENDER


		// PLAYER
		addModule(new BridgeAssistModule());
		addModule(new CrystalOptimizerModule());
		addModule(new FastPlaceModule());
		addModule(new VelocityModule());

		// CLIENT
		addModule(new AsteriaSettingsModule());
		addModule(new MiddleClickFriendModule());
		addModule(new ArrayListModule());

		// MISC
		addModule(new PingSpoofModule());
		addModule(new FakePlayerModule());
	}

	@EventHandler
	private void onKeyPress(KeyPressEvent event) {
		if (!AsteriaMenu.isClientEnabled())
			return;

		if (event.action == GLFW.GLFW_RELEASE)
			return;

		if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen)
			return;

		if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3))
			return;

		modules.stream().filter(m -> m.getKey() == event.key).forEach(Module::toggle);
	}
}
