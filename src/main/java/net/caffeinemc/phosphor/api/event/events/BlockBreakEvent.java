package net.caffeinemc.phosphor.api.event.events;

import net.caffeinemc.phosphor.api.event.Cancellable;

@SuppressWarnings("all")
public class BlockBreakEvent extends Cancellable {
    private static final BlockBreakEvent INSTANCE = new BlockBreakEvent();

    public static BlockBreakEvent get() {
        INSTANCE.setCancelled(false);
        return INSTANCE;
    }
}
