package net.caffeinemc.phosphor.module.modules.combat;

import net.caffeinemc.phosphor.api.event.events.ItemUseEvent;
import net.caffeinemc.phosphor.api.event.events.PlayerTickEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.event.orbit.EventPriority;
import net.caffeinemc.phosphor.api.util.BlockUtils;
import net.caffeinemc.phosphor.api.util.KeyUtils;
import net.caffeinemc.phosphor.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.lwjgl.glfw.GLFW;

public class AirAnchorModule extends Module {
    public AirAnchorModule() {
        super("AirAnchor", "Makes MINDBLOWING Minecraft Crystal PVP Method easier!", Category.COMBAT);
    }

    private BlockHitResult anchorHitResult;
    private int count;

    @Override
    public void onEnable() {
        anchorHitResult = null;
        count = 0;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerTick(PlayerTickEvent event) {
        int airPlaceLimit = 1;

        if (KeyUtils.isKeyPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            if (mc.player.getMainHandStack().isOf(Items.RESPAWN_ANCHOR)) {
                if (mc.crosshairTarget instanceof BlockHitResult blockHitResult &&
                        BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, blockHitResult.getBlockPos()) &&
                        BlockUtils.isAnchorUncharged(blockHitResult.getBlockPos())) {
                    mc.options.useKey.setPressed(false);
                    return;
                }

                if (anchorHitResult != null) {
                    if (count >= airPlaceLimit) return;

                    ActionResult interactBlock = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, anchorHitResult);
                    if (interactBlock.shouldSwingHand()) {
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }

                    count++;
                    anchorHitResult = null;
                }
            }

            if (!mc.options.useKey.isPressed()) {
                mc.options.useKey.setPressed(true);
            }
        } else {
            count = 0;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onItemUse(ItemUseEvent.Pre event) {
        if (mc.player.getMainHandStack().isOf(Items.RESPAWN_ANCHOR)) {
            if (mc.crosshairTarget instanceof BlockHitResult blockHitResult &&
                    BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, blockHitResult.getBlockPos()) &&
                    BlockUtils.isAnchorCharged(blockHitResult.getBlockPos())) {
                anchorHitResult = blockHitResult;
            }
        }
    }
}
