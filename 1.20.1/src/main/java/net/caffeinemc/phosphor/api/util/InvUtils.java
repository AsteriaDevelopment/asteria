package net.caffeinemc.phosphor.api.util;

import net.caffeinemc.phosphor.mixin.ClientPlayerInteractionManagerAccessor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
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

    public static boolean hasItemInHotbar(Item item) {
        PlayerInventory inventory = mc.player.getInventory();
        for (int i = 0; i < 9; ++i) {
            if (inventory.getStack(i).isOf(item)) return true;
        }
        return false;
    }

    public static boolean hasItemInInventory(Item item) {
        PlayerInventory inventory = mc.player.getInventory();
        for (int i = 9; i < 36; ++i) {
            if (inventory.getStack(i).isOf(item)) return true;
        }
        return false;
    }

    public static boolean hasItemInInventory(Predicate<ItemStack> itemSupplier) {
        PlayerInventory inventory = mc.player.getInventory();
        for (int i = 9; i < 36; ++i) {
            if (itemSupplier.test(inventory.getStack(i))) return true;
        }
        return false;
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

    public static Map<ArmorItem.Type, Integer> slotNumByType = new HashMap<>();
    static {
        slotNumByType.put(ArmorItem.Type.HELMET, 3);
        slotNumByType.put(ArmorItem.Type.CHESTPLATE, 2);
        slotNumByType.put(ArmorItem.Type.LEGGINGS, 1);
        slotNumByType.put(ArmorItem.Type.BOOTS, 0);
    }

    public static boolean isArmorBetter(ItemStack armorStack) {
        ItemStack currentArmor = mc.player.getInventory().getArmorStack(slotNumByType.get(((ArmorItem) armorStack.getItem()).getType()));

        if (currentArmor == null || !(currentArmor.getItem() instanceof ArmorItem)) return true;

        return isArmorBetter(currentArmor, armorStack);
    }

    public static boolean isArmorBetter(ItemStack fromArmorStack, ItemStack thenArmorStack) {
        ArmorItem fromArmorItem = (ArmorItem) fromArmorStack.getItem();
        float fromPoints = fromArmorItem.getProtection() + fromArmorItem.getToughness();

        ArmorItem thenArmorItem = (ArmorItem) thenArmorStack.getItem();
        float thenPoints = thenArmorItem.getProtection() + thenArmorItem.getToughness();

        if (fromPoints < thenPoints) {
            return true;
        }

        NbtList fromArmorEnchantments = fromArmorStack.getEnchantments();
        if (fromArmorEnchantments == null) {
            return thenArmorStack.getEnchantments() != null;
        }

        NbtList thenArmorEnchantments = thenArmorStack.getEnchantments();
        if (thenArmorEnchantments == null) {
            return false;
        }

        fromPoints += getEnchantmentLevel(fromArmorStack, Enchantments.PROTECTION);
        fromPoints += getEnchantmentLevel(fromArmorStack, Enchantments.UNBREAKING);
        fromPoints += getEnchantmentLevel(fromArmorStack, Enchantments.MENDING) * 2;

        thenPoints += getEnchantmentLevel(thenArmorStack, Enchantments.PROTECTION);
        thenPoints += getEnchantmentLevel(thenArmorStack, Enchantments.UNBREAKING);
        thenPoints += getEnchantmentLevel(thenArmorStack, Enchantments.MENDING) * 2;

        if (fromPoints == thenPoints) {
            switch (fromArmorItem.getType()) {
                case HELMET -> {
                    fromPoints += getEnchantmentLevel(fromArmorStack, Enchantments.AQUA_AFFINITY);
                    fromPoints += getEnchantmentLevel(fromArmorStack, Enchantments.RESPIRATION);

                    thenPoints += getEnchantmentLevel(thenArmorStack, Enchantments.AQUA_AFFINITY);
                    thenPoints += getEnchantmentLevel(thenArmorStack, Enchantments.RESPIRATION);
                }
                case BOOTS -> {
                    fromPoints += getEnchantmentLevel(fromArmorStack, Enchantments.DEPTH_STRIDER);
                    fromPoints += getEnchantmentLevel(fromArmorStack, Enchantments.SWIFT_SNEAK);
                    fromPoints += getEnchantmentLevel(fromArmorStack, Enchantments.SOUL_SPEED);
                    fromPoints += getEnchantmentLevel(fromArmorStack, Enchantments.FEATHER_FALLING);

                    thenPoints += getEnchantmentLevel(thenArmorStack, Enchantments.DEPTH_STRIDER);
                    thenPoints += getEnchantmentLevel(thenArmorStack, Enchantments.SWIFT_SNEAK);
                    thenPoints += getEnchantmentLevel(thenArmorStack, Enchantments.SOUL_SPEED);
                    thenPoints += getEnchantmentLevel(thenArmorStack, Enchantments.FEATHER_FALLING);
                }
            }
        }

        return fromPoints < thenPoints;
    }

    public static int getEnchantmentLevel(ItemStack armorStack, Enchantment enchantment) {
        if (EnchantmentHelper.get(armorStack).containsKey(enchantment)) {
            return EnchantmentHelper.getLevel(enchantment, armorStack);
        }

        return 0;
    }

    public static boolean hasEmptyArmor() {
        AtomicBoolean hasEmptyArmor = new AtomicBoolean(false);

        for (ItemStack armorStack : mc.player.getArmorItems()) {
            if (armorStack == null || !(armorStack.getItem() instanceof ArmorItem)) {
                hasEmptyArmor.set(true);
                break;
            }
        }

        return hasEmptyArmor.get();
    }

    public static boolean isArmorSlotEmpty(ArmorItem armorItem) {
        ItemStack armorStack = mc.player.getInventory().getArmorStack(slotNumByType.get(armorItem.getType()));
        return armorStack == null || !(armorStack.getItem() instanceof ArmorItem);
    }
}
