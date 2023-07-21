package net.caffeinemc.phosphor.module.modules.combat;

import net.caffeinemc.phosphor.api.event.events.TickEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.util.MathUtils;
import net.caffeinemc.phosphor.api.util.PlayerUtils;
import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.BooleanSetting;
import net.caffeinemc.phosphor.module.setting.settings.NumberSetting;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class TriggerBotModule extends Module {
    public final BooleanSetting clickSimulation = new BooleanSetting("Click Simulation", this, true);
    public final NumberSetting minRange = new NumberSetting("Min Range", this, 2.5d, 2d, 4d, 0.1d);
    public final NumberSetting maxRange = new NumberSetting("Max Range", this, 3d, 2d, 4d, 0.1d);
    public final BooleanSetting permTrigger = new BooleanSetting("Permament Trigger", this, true);
    public final BooleanSetting weaponOnly = new BooleanSetting("Weapon Only", this, true);
    public final BooleanSetting focusMode = new BooleanSetting("Focus Mode", this, false);
    public final NumberSetting focusRange = new NumberSetting("Focus Range", this, 10d, 5d, 10d, 1d);

    public TriggerBotModule() {
        super("TriggerBot", "Automatically hits entity on crosshair", Category.COMBAT);
    }

    private boolean isHoldingWeapon() {
        ItemStack heldItem = mc.player.getMainHandStack();

        return heldItem.getItem() instanceof SwordItem || heldItem.getItem() instanceof AxeItem;
    }

    private double currentRange;
    private LivingEntity focusedTarget;

    @Override
    public void onEnable() {
        currentRange = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null)
            return;

        if (clickSimulation.isEnabled()) {
            if (focusedTarget != null) {
                if (!focusedTarget.isAlive() ||
                        focusedTarget.isDead() ||
                        focusedTarget.isRemoved() ||
                        focusedTarget.distanceTo(mc.player) > focusRange.getValue())
                    focusedTarget = null;
            }
        }

        Entity target = mc.crosshairTarget instanceof EntityHitResult result ? result.getEntity() : null;

        if (target == null || mc.interactionManager == null || mc.currentScreen instanceof HandledScreen)
            return;

        if (target.getName().equals(mc.player.getName()))
            return;

        if (target instanceof PlayerEntity player && PlayerUtils.isFriend(player))
            return;

        if (!permTrigger.isEnabled() && !mc.options.attackKey.isPressed())
            return;

        if (weaponOnly.isEnabled() && !isHoldingWeapon())
            return;

        if (mc.player.isBlocking() || mc.player.isUsingItem() || !(target instanceof LivingEntity livingTarget) || ((LivingEntity) target).getHealth() <= 0.0f)
            return;

        if ((mc.player.isOnGround() && mc.player.getAttackCooldownProgress(0.5f) < 0.92f) || (!mc.player.isOnGround() && mc.player.getAttackCooldownProgress(0.5f) < 0.95f))
            return;

        if (livingTarget.isDead() || !livingTarget.isAlive() || livingTarget.isInvisible())
            return;

        if (currentRange == 0) {
            if (minRange.getValue() >= maxRange.getValue()) {
                currentRange = minRange.getValue();
            } else {
                currentRange = MathUtils.getRandomDouble(minRange.getValue(), maxRange.getValue());
            }
        }

        if (livingTarget.distanceTo(mc.player) > currentRange)
            return;

        if (clickSimulation.isEnabled()) {
            if (focusedTarget == null) focusedTarget = livingTarget;
            if (focusedTarget != livingTarget) return;
        }

        if (clickSimulation.isEnabled()) Phosphor.mouseSimulation().mouseClick(GLFW.GLFW_MOUSE_BUTTON_LEFT);

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        currentRange = 0;
    }
}
