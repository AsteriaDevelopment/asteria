package net.caffeinemc.phosphor.mixin;

import com.mojang.authlib.GameProfile;
import net.caffeinemc.phosphor.api.event.events.SendMovementPacketEvent;
import net.caffeinemc.phosphor.module.modules.player.BridgeAssistModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.api.event.events.PlayerTickEvent;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow public abstract boolean isSneaking();

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPlayerTick(CallbackInfo ci) {
        Phosphor.EVENTBUS.post(PlayerTickEvent.get());
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo info) {
        Phosphor.EVENTBUS.post(SendMovementPacketEvent.Pre.get());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    private void onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
        Phosphor.EVENTBUS.post(SendMovementPacketEvent.Pre.get());
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPacketsTail(CallbackInfo info) {
        Phosphor.EVENTBUS.post(SendMovementPacketEvent.Post.get());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void onTickHasVehicleAfterSendPackets(CallbackInfo info) {
        Phosphor.EVENTBUS.post(SendMovementPacketEvent.Post.get());
    }

    @Override
    protected boolean clipAtLedge() {
        BridgeAssistModule bridgeAssist = Phosphor.moduleManager().getModule(BridgeAssistModule.class);
        if (bridgeAssist.isEnabled() && bridgeAssist.checkHands() && !isOnGround() &&
                MathHelper.wrapDegrees(getPitch()) >= bridgeAssist.minAngle.getFValue()) {
            if (!isSneaking()) setSneaking(true);
        }

        return super.clipAtLedge();
    }
}
