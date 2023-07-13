package net.caffeinemc.phosphor.api.event.events;

import net.caffeinemc.phosphor.api.event.Cancellable;

@SuppressWarnings("all")
public class ItemUseEvent extends Cancellable {
    private static final ItemUseEvent INSTANCE = new ItemUseEvent();

    public static ItemUseEvent get() {
        INSTANCE.setCancelled(false);
        return INSTANCE;
    }
}
