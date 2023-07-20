package net.caffeinemc.phosphor.api.event.events;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

@SuppressWarnings("all")
public class WorldRenderEvent {

	private static final WorldRenderEvent INSTANCE = new WorldRenderEvent();

	public WorldRenderContext context;

	public static WorldRenderEvent get(WorldRenderContext context) {
		INSTANCE.context = context;
		return INSTANCE;
	}
}
