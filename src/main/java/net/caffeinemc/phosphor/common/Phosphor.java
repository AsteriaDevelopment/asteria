package net.caffeinemc.phosphor.common;

import net.caffeinemc.phosphor.api.config.ConfigManager;
import net.caffeinemc.phosphor.api.rotation.RotationManager;
import net.caffeinemc.phosphor.api.util.CrystalUtils;
import net.caffeinemc.phosphor.module.ModuleManager;
import net.caffeinemc.phosphor.module.modules.client.RadiumSettingsModule;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.caffeinemc.phosphor.api.event.events.WorldRenderEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventBus;
import net.caffeinemc.phosphor.api.event.orbit.IEventBus;
import net.caffeinemc.phosphor.gui.RadiumMenu;

import java.lang.invoke.MethodHandles;

public final class Phosphor {
	public static String name = "Radium";
	public static String packagePrefix = "net.caffeinemc.phosphor";

	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static IEventBus EVENTBUS = new EventBus();
	public static Phosphor INSTANCE;

	public static ModuleManager moduleManager() {
		return INSTANCE.moduleManager;
	}

	public static ConfigManager configManager() {
		return INSTANCE.configManager;
	}

	public static RotationManager rotationManager() {
		return INSTANCE.rotationManager;
	}

	public Phosphor() {
		INSTANCE = this;
	}

	public ModuleManager moduleManager;
	public ConfigManager configManager;
	public CrystalUtils crystalUtils;
	public RotationManager rotationManager;

	public void init() {
		this.moduleManager = new ModuleManager();
		this.configManager = new ConfigManager();
		this.crystalUtils = new CrystalUtils();
		this.rotationManager = new RotationManager();

		EVENTBUS.registerLambdaFactory(packagePrefix, (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		EVENTBUS.subscribe(moduleManager);
		EVENTBUS.subscribe(crystalUtils);
		EVENTBUS.subscribe(rotationManager);
		WorldRenderEvents.END.register((context) -> { EVENTBUS.post(WorldRenderEvent.get(context)); });

		this.configManager.loadConfig();
	}
}
