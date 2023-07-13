package net.caffeinemc.phosphor.api.event.events;

import net.caffeinemc.phosphor.api.event.Cancellable;

@SuppressWarnings("all")
public class AttackEvent extends Cancellable {
    private static final AttackEvent INSTANCE = new AttackEvent();

    public static AttackEvent get() {
        INSTANCE.setCancelled(false);
        return INSTANCE;
    }
}
