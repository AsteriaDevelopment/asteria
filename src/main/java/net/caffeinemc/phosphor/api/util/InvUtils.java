package net.caffeinemc.phosphor.api.util;

import net.caffeinemc.phosphor.mixin.ClientPlayerInteractionManagerAccessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

import static net.caffeinemc.phosphor.common.Phosphor.mc;

public class InvUtils {
    public static boolean selectItemFromHotbar(Predicate<Item> item) {
        PlayerInventory inv = mc.player.getInventory();
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = inv.getStack(i);
            if (!item.test(itemStack.getItem())) continue;
            inv.selectedSlot = i;
            ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).callSyncSelectedSlot();
            return true;
        }
        return false;
    }

    public static boolean selectItemFromHotbar(Item item) {
        return selectItemFromHotbar((Item i) -> i == item);
    }
}
