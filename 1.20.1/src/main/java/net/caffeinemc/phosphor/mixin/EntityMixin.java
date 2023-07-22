package net.caffeinemc.phosphor.mixin;

import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.gui.AsteriaMenu;
import net.caffeinemc.phosphor.module.modules.combat.HitboxesModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public class EntityMixin {
    @Inject(
            method = "getTargetingMargin",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetTargetingMargin(CallbackInfoReturnable<Float> cir) {
        HitboxesModule hitboxes = Phosphor.moduleManager().getModule(HitboxesModule.class);
        if (AsteriaMenu.isClientEnabled() && hitboxes.isEnabled()) {
            cir.setReturnValue(hitboxes.getHitboxSize((Entity) (Object) this));
        }
    }
}
