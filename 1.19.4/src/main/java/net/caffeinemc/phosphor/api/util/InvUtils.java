package net.caffeinemc.phosphor.api.util;

import net.caffeinemc.phosphor.mixin.ClientPlayerInteractionManagerAccessor;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.potion.PotionUtil;

import java.util.function.Predicate;

import static net.caffeinemc.phosphor.common.Phosphor.mc;

public class InvUtils {
    public static void setInvSlot(int slot) {
        mc.player.getInventory().selectedSlot = slot;
        ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).callSyncSelectedSlot();
    }

    public static int getItemSlot(Predicate<Item> item) {
        PlayerInventory inv = mc.player.getInventory();
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = inv.getStack(i);
            if (!item.test(itemStack.getItem()))
                continue;
            return i;
        }

        return -1;
    }

    public static int getItemSlot(Item item) {
        return getItemSlot((Item i) -> i == item);
    }

    public static boolean selectItemFromHotbar(Predicate<Item> item) {
        PlayerInventory inv = mc.player.getInventory();
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = inv.getStack(i);
            if (!item.test(itemStack.getItem()))
                continue;

            setInvSlot(i);
            return true;
        }
        return false;
    }

    public static boolean selectItemFromHotbar(Item item) {
        return selectItemFromHotbar((Item i) -> i == item);
    }

    /**
     * Returns integer of a slot with splash potion of your specific potion effect
     *
     * @param  rawId  		You can get id of your specific effect from <a href="https://minecraft.fandom.com/wiki/Effect">MC Wiki</a>
     * @param  duration		Duration of potion effect
     * @param  amplifier	Multiplier of potion effect
     *
     * @return integer of slot with splash potion of your specific potion effect
     *
     * @author pycat
     */
    public static int findSplash(int rawId, int duration, int amplifier) {
        PlayerInventory inv = mc.player.getInventory();
        StatusEffectInstance potion = new StatusEffectInstance(StatusEffect.byRawId(rawId), duration, amplifier);

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = inv.getStack(i);

            if (!(itemStack.getItem() instanceof SplashPotionItem))
                continue;

            String s = PotionUtil.getPotion(itemStack).getEffects().toString();

            if (s.contains(potion.toString())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns true if itemStack has the specific splash potion
     *
     * @param  rawId            You can get id of your specific effect from <a href="https://minecraft.fandom.com/wiki/Effect">MC Wiki</a>
     * @param  duration         Duration of potion effect
     * @param  amplifier        Multiplier of potion effect
     * @param  itemStack        ItemStack to check
     *
     * @return boolean
     *
     * @author pycat
     */
    public static boolean isThatSplash(int rawId, int duration, int amplifier, ItemStack itemStack) {
        StatusEffectInstance potion = new StatusEffectInstance(StatusEffect.byRawId(rawId), duration, amplifier);

        return itemStack.getItem() instanceof SplashPotionItem &&
                PotionUtil.getPotion(itemStack).getEffects().toString().contains(potion.toString());
    }

    public static int scrollToSlot(int toSlot) {
        int currentSlot = mc.player.getInventory().selectedSlot;

        if (currentSlot < toSlot)
            return currentSlot + 1;
        else
            return currentSlot - 1;
    }

}
