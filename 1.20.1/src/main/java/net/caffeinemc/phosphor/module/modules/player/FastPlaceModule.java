package net.caffeinemc.phosphor.module.modules.player;

import net.caffeinemc.phosphor.api.event.events.PlayerTickEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.util.KeyUtils;
import net.caffeinemc.phosphor.api.util.MathUtils;
import net.caffeinemc.phosphor.mixin.MinecraftClientAccessor;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.BooleanSetting;
import net.caffeinemc.phosphor.module.setting.settings.NumberSetting;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.*;
import org.lwjgl.glfw.GLFW;

public class FastPlaceModule extends Module {
    private final BooleanSetting doPlaceInGui = new BooleanSetting("Place in GUI", this, false);
    private final BooleanSetting xpOnly = new BooleanSetting("Only XP", this, false);
    private final BooleanSetting blocks = new BooleanSetting("Blocks", this, true);
    private final BooleanSetting items = new BooleanSetting("Items", this, true);
    private final NumberSetting delay = new NumberSetting("Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting useChance = new NumberSetting("Use Chance", this, 100d, 0d, 100d, 1d);

    public FastPlaceModule() {
        super("FastPlace", "Spams use action", Category.PLAYER);
    }

    private int placeClock = 0;

    public void reset() {
        placeClock = 0;
    }

    @Override
    public void onEnable() {
        reset();
    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {
        if (KeyUtils.isKeyPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            if (mc.currentScreen instanceof HandledScreen && !this.doPlaceInGui.isEnabled())
                return;

            if (placeClock <= delay.getIValue()) {
                placeClock++;
                return;
            }

            if (MathUtils.getRandomInt(1, 100) <= useChance.getIValue()) {
                ItemStack mainHandStack = mc.player.getMainHandStack();
                ItemStack offHandStack = mc.player.getOffHandStack();
                Item mainHandItem = mainHandStack.getItem();
                Item offHandItem = mc.player.getOffHandStack().getItem();

                if (!(mainHandStack.isOf(Items.EXPERIENCE_BOTTLE) || offHandStack.isOf(Items.EXPERIENCE_BOTTLE)) && xpOnly.isEnabled())
                    return;

                if (mainHandItem instanceof BlockItem || offHandItem instanceof BlockItem) {
                    if (!blocks.isEnabled())
                        return;
                } else {
                    if (!items.isEnabled())
                        return;
                }

                if (mainHandItem.getFoodComponent() != null)
                    return;

                if (offHandItem.getFoodComponent() != null)
                    return;

                if ((mainHandStack.isOf(Items.RESPAWN_ANCHOR) || mainHandStack.isOf(Items.GLOWSTONE)) ||
                        (offHandStack.isOf(Items.RESPAWN_ANCHOR) || offHandStack.isOf(Items.GLOWSTONE)))
                    return;

                if (mainHandItem instanceof RangedWeaponItem || offHandItem instanceof RangedWeaponItem)
                    return;

                ((MinecraftClientAccessor) mc).callDoItemUse();

                reset();
            }
        }
    }
}
