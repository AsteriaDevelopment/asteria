package net.caffeinemc.phosphor.module.modules.misc;

import net.caffeinemc.phosphor.api.event.events.PacketEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.event.orbit.EventPriority;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.NumberSetting;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.PacketCallbacks;

public class FakePingModule extends Module {
    public final NumberSetting ping = new NumberSetting("Ping", this, 200, 0, 200, 1);

    public FakePingModule() {
        super("FakePing", "Fakes player's ping", Category.MISCELLANEOUS);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPacketSend(PacketEvent.Send event) {
        if (mc.player == null)
            return;

        int origPing;
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());

        if (playerListEntry != null)
            origPing = playerListEntry.getLatency();
        else
            origPing = 0;

        if (origPing >= ping.getIValue())
            return;

        new Thread(() -> {
            try {
                Thread.sleep(ping.getIValue() - origPing);
                mc.getNetworkHandler().getConnection().send(event.packet, null);
            } catch (InterruptedException ignored) {}
        }).start();

        event.cancel();
    }
}
