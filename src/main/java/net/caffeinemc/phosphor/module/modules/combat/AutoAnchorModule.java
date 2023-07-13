package net.caffeinemc.phosphor.module.modules.combat;

import net.caffeinemc.phosphor.api.event.events.TickEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.util.BlockUtils;
import net.caffeinemc.phosphor.api.util.InvUtils;
import net.caffeinemc.phosphor.mixin.ClientPlayerInteractionManagerAccessor;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.BooleanSetting;
import net.caffeinemc.phosphor.module.setting.settings.NumberSetting;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class AutoAnchorModule extends Module {
    public final NumberSetting cooldown = new NumberSetting("Cooldown", this, 0, 0, 20, 1);
    public final NumberSetting itemSwap = new NumberSetting("Item Swap", this, 0, 0, 8, 1);
    public final BooleanSetting chargeOnly = new BooleanSetting("Charge Only", this, false);

    public AutoAnchorModule() {
        super("AutoAnchor", "Automatically achors", Category.COMBAT);
    }

    private static boolean hasAnchored;
    private static boolean hasCharged;
    private static int clock;

    @Override
    public void onEnable() {
        hasAnchored = false;
        hasCharged = false;
        clock = cooldown.getIValue();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.world == null || mc.player == null) return;

        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 1) != 1)
            return;

        if (mc.player.isUsingItem())
            return;

        if (hasAnchored) {
            if (clock != 0) {
                --clock;
                return;
            }
            clock = cooldown.getIValue();
            hasAnchored = false;
        }

        final HitResult cr = mc.crosshairTarget;

        if (cr instanceof BlockHitResult) {
            final BlockHitResult hit = (BlockHitResult) cr;
            final BlockPos pos = hit.getBlockPos();
            if (BlockUtils.isAnchorUncharged(pos)) {
                if (mc.player.isHolding(Items.GLOWSTONE)) {
                    final ActionResult actionResult = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                    if (actionResult.isAccepted() && actionResult.shouldSwingHand()) {
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    return;
                }
                InvUtils.selectItemFromHotbar(Items.GLOWSTONE);
                final ActionResult actionResult = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                if (actionResult.isAccepted() && actionResult.shouldSwingHand()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            } else if (BlockUtils.isAnchorCharged(pos) && !chargeOnly.isEnabled()) {
                final PlayerInventory inv = mc.player.getInventory();
                inv.selectedSlot = itemSwap.getIValue();
                ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).callSyncSelectedSlot();
                final ActionResult actionResult2 = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                if (actionResult2.isAccepted() && actionResult2.shouldSwingHand()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
                hasAnchored = true;
            }
        }
    }


}
