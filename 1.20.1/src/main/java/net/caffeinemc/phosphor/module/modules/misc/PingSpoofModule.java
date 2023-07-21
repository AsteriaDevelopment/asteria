package net.caffeinemc.phosphor.module.modules.misc;

import net.caffeinemc.phosphor.api.event.events.PacketEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.event.orbit.EventPriority;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.NumberSetting;
import net.minecraft.client.network.PlayerListEntry;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PingSpoofModule extends Module {
    public final NumberSetting ping = new NumberSetting("Ping", this, 200, 0, 200, 1);

    public PingSpoofModule() {
        super("PingSpoof", "Spoofing player's ping", Category.MISCELLANEOUS);
    }

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1000);

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPacketSend(PacketEvent.Send event) {
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());

        int origPing = playerListEntry != null ? playerListEntry.getLatency() : 0;
        if (origPing >= ping.getIValue())
            return;

        scheduler.schedule(() -> mc.getNetworkHandler().getConnection().send(event.packet), ping.getIValue() - origPing, TimeUnit.MILLISECONDS);
        event.cancel();
    }
}
