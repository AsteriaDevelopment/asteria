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
    private final BooleanSetting blocks = new BooleanSetting("Blocks", this, true);
    private final BooleanSetting items = new BooleanSetting("Items", this, true);
    private final NumberSetting delay = new NumberSetting("Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting useChance = new NumberSetting("Use Chance", this, 100d, 0d, 100d, 1d);

    public FastPlaceModule() {
        super("FastPlace", "Spams use action", Category.PLAYER);
    }

    private int placeClock = 0;

    public void reset() {
        placeClock = delay.getIValue();
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

            ItemStack mainHandStack = mc.player.getMainHandStack();

            if (placeClock > 0) {
                placeClock--;
                return;
            }

            int randomNum = MathUtils.getRandomInt(1, 100);

            if (randomNum <= useChance.getIValue()) {
                Item mainHandItem = mainHandStack.getItem();

                if (mainHandItem instanceof BlockItem) {
                    if (!blocks.isEnabled())
                        return;
                } else {
                    if (!items.isEnabled())
                        return;
                }

                if (mainHandItem.getFoodComponent() != null)
                    return;

                if (mainHandStack.isOf(Items.RESPAWN_ANCHOR) || mainHandStack.isOf(Items.GLOWSTONE))
                    return;

                if (mainHandItem instanceof RangedWeaponItem)
                    return;

                ((MinecraftClientAccessor) mc).callDoItemUse();

                placeClock = delay.getIValue();
            }
        }
    }
}
