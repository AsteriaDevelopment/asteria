package net.caffeinemc.phosphor.module.modules.player;

import com.google.common.collect.Streams;
import net.caffeinemc.phosphor.api.event.events.TickEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.NumberSetting;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

import java.util.stream.Stream;

public class BridgeAssistModule extends Module {
    private final NumberSetting edgeDistance = new NumberSetting("Edge Distance", this, 0.2, 0.2, 0.3, 0.01);

    public BridgeAssistModule() {
        super("BridgeAssist", "Automatically shifts on edge of block when you hold block in hand.", Category.PLAYER);
    }

    private boolean wasActivated;

    @Override
    public void onEnable() {
        wasActivated = false;
    }

    public boolean checkHands() {
        return mc.player.getMainHandStack().getItem() instanceof BlockItem || mc.player.getOffHandStack().getItem() instanceof BlockItem;
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if (mc.player == null) return;

        if (!checkHands()) return;

        if (!mc.player.isOnGround()) return;

        Box box = mc.player.getBoundingBox();
        Box adjustedBox = box.offset(0, -0.5, 0).expand(-edgeDistance.getValue(), 0, -edgeDistance.getValue());

        Stream<VoxelShape> blockCollisions = Streams.stream(mc.world.getBlockCollisions(mc.player, adjustedBox));

        if (blockCollisions.findAny().isPresent()) {
            if (mc.options.sneakKey.isPressed() && wasActivated) {
                mc.options.sneakKey.setPressed(false);
                wasActivated = false;
            }
        } else {
            if (!mc.options.sneakKey.isPressed()) {
                if (mc.options.sprintKey.isPressed()) mc.options.sprintKey.setPressed(false);
                mc.options.sneakKey.setPressed(true);
                wasActivated = true;
            }
        }
    }
}
