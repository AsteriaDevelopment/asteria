package net.caffeinemc.phosphor.module.modules.combat;

import net.caffeinemc.phosphor.api.event.events.WorldTickEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.module.Module;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class AutoJumpResetModule extends Module {
    public AutoJumpResetModule() {
        super("AutoJumpReset", "Automatically doing jump reset", Category.COMBAT);
    }

    @EventHandler
    private void onWorldTick(WorldTickEvent event) {
        if (mc.player == null) {
            return;
        }
        if (mc.player.isBlocking()) {
            return;
        }
        if (mc.player.isUsingItem()) {
            return;
        }
        if (mc.currentScreen instanceof HandledScreen) {
            return;
        }
        if (!mc.player.isOnGround()) {
            return;
        }
        if (mc.player.maxHurtTime == 0) {
            return;
        }
        if (mc.player.hurtTime == 0) {
            return;
        }
        if (mc.player.hurtTime == mc.player.maxHurtTime - 1) {
            mc.player.jump();
        }
    }
}
