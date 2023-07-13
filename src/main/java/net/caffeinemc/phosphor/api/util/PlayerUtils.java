package net.caffeinemc.phosphor.api.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.module.modules.combat.ReachModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import static net.caffeinemc.phosphor.common.Phosphor.mc;

public class PlayerUtils {
    public static JsonObject friends = new JsonObject();

    public static boolean isFriend(PlayerEntity player) {
        return friends.get(player.getUuidAsString()) != null;
    }

    public static void addFriend(PlayerEntity player) {
        if (!isFriend(player))
            friends.add(player.getUuidAsString(), new JsonPrimitive(player.getName().getString()));
    }

    public static void removeFriend(PlayerEntity player) {
        if (isFriend(player))
            friends.remove(player.getUuidAsString());
    }

    public static PlayerEntity findNearestPlayer(PlayerEntity toPlayer, float range, boolean seeOnly) {
        float minRange = Float.MAX_VALUE;
        PlayerEntity minPlayer = null;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (isFriend(player))
                continue;

            float distance = player.distanceTo(toPlayer);

            if (player.getName().equals(toPlayer.getName()) &&
                    player != toPlayer &&
                    distance <= range &&
                    toPlayer.canSee(player) == seeOnly) {
                if (distance < minRange) {
                    minRange = distance;
                    minPlayer = player;
                }
            }
        }

        return minPlayer;
    }

    public static Entity findNearestEntity(PlayerEntity toPlayer, float range, boolean seeOnly) {
        float minRange = Float.MAX_VALUE;
        Entity minEntity = null;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player && isFriend(player))
                continue;

            float distance = entity.distanceTo(toPlayer);

            if (entity != toPlayer && distance <= range && toPlayer.canSee(entity) == seeOnly) {
                if (distance < minRange) {
                    minRange = distance;
                    minEntity = entity;
                }
            }
        }

        return minEntity;
    }

    @Nullable
    public static ItemStack findShield(PlayerEntity player) {
        ItemStack result = null;

        for (Hand hand : Hand.values()) {
            ItemStack handStack = player.getStackInHand(hand);
            if (handStack.isOf(Items.SHIELD)) {
                result = handStack;
            }
        }

        return result;
    }

    public static float getShieldCooldownProgress(PlayerEntity player) {
        ItemStack shieldStack = findShield(player);
        if (shieldStack == null)
            return 0.0f;

        return player.getItemCooldownManager().getCooldownProgress(shieldStack.getItem(), mc.getTickDelta());
    }

    public static float getItemCooldownProgress(PlayerEntity player, ItemStack itemStack) {
        return player.getItemCooldownManager().getCooldownProgress(itemStack.getItem(), mc.getTickDelta());
    }

    public static float getItemCooldownProgress(PlayerEntity player, Hand hand) {
        return player.getItemCooldownManager().getCooldownProgress(player.getStackInHand(hand).getItem(), mc.getTickDelta());
    }

    public static float getItemCooldownProgress(Hand hand) {
        return mc.player.getItemCooldownManager().getCooldownProgress(mc.player.getStackInHand(hand).getItem(), mc.getTickDelta());
    }

    public static double getReach() {
        ReachModule reach = Phosphor.moduleManager().getModule(ReachModule.class);
        if (reach != null && reach.isEnabled())
            return reach.reach.getValue();

        return mc.interactionManager.getCurrentGameMode().isCreative() ? 4D : 3D;
    }
}
