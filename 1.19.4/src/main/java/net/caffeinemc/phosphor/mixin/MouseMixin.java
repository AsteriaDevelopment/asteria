package net.caffeinemc.phosphor.mixin;

import net.caffeinemc.phosphor.common.Phosphor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.caffeinemc.phosphor.api.event.events.MouseMoveEvent;
import net.caffeinemc.phosphor.api.event.events.MouseUpdateEvent;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void onMouseMove(long window, double mouseX, double mouseY, CallbackInfo ci) {
        if (window == this.client.getWindow().getHandle())
            Phosphor.EVENTBUS.post(MouseMoveEvent.get(mouseX, mouseY));
    }

    @Inject(method = "updateMouse", at = @At("HEAD"))
    private void onMouseUpdate(CallbackInfo ci) {
        Phosphor.EVENTBUS.post(MouseUpdateEvent.get());
    }
}