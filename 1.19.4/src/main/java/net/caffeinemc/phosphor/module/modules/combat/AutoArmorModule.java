package net.caffeinemc.phosphor.module.modules.combat;

import net.caffeinemc.phosphor.api.event.events.PlayerTickEvent;
import net.caffeinemc.phosphor.api.event.events.SlotCheckEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.util.InvUtils;
import net.caffeinemc.phosphor.api.util.KeyUtils;
import net.caffeinemc.phosphor.mixin.HandledScreenAccessor;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.KeybindSetting;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ArmorItem;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmorModule extends Module {
    public final KeybindSetting activateKey = new KeybindSetting("Activate Key", -1, this);

    public AutoArmorModule() {
        super("AutoArmor", "Automatically equip best armor from inventory", Category.COMBAT);
    }

    public boolean searchArmor() {
        return InvUtils.hasItemInInventory(itemStack -> itemStack.getItem() instanceof ArmorItem) &&
                KeyUtils.isKeyPressed(activateKey.getKeyCode());
    }

    @EventHandler
    private void onPlayerTick(PlayerTickEvent event) {
        if (!searchArmor()) return;

        if (mc.currentScreen instanceof InventoryScreen inventoryScreen) {
            Slot focusedSlot = ((HandledScreenAccessor) inventoryScreen).getFocusedSlot();

            if (focusedSlot == null) return;

            if (focusedSlot.getStack().getItem() instanceof ArmorItem armorItem) {
                int slot;

                switch (armorItem.getType()) {
                    case HELMET -> slot = 39;
                    case CHESTPLATE -> slot = 38;
                    case LEGGINGS -> slot = 37;
                    case BOOTS -> slot = 36;
                    default -> slot = -1;
                }

                if (slot == -1) return;

                mc.interactionManager.clickSlot(
                        inventoryScreen.getScreenHandler().syncId,
                        focusedSlot.getIndex(),
                        slot,
                        SlotActionType.SWAP,
                        mc.player
                );
            }
        }
    }

    @EventHandler
    private void onSlotCheck(SlotCheckEvent event) {
        if (!searchArmor()) return;

        if (0 >= event.slot.getIndex() || event.slot.getIndex() >= 36) return;

        if (mc.currentScreen instanceof InventoryScreen inventoryScreen) {
            Slot focusedSlot = ((HandledScreenAccessor) inventoryScreen).getFocusedSlot();

            if (focusedSlot == null || !(focusedSlot.getStack().getItem() instanceof ArmorItem)) {
                if (event.slot.getStack().getItem() instanceof ArmorItem && InvUtils.isArmorBetter(event.slot.getStack())) {
                    event.setCancelOutput(true);
                }
            } else if (event.slot.getIndex() == focusedSlot.getIndex() && InvUtils.isArmorBetter(focusedSlot.getStack())) {
                event.setCancelOutput(true);
            }
        }
    }
}
