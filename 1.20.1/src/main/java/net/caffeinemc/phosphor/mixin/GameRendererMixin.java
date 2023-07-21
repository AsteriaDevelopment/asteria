package net.caffeinemc.phosphor.mixin;

import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.module.modules.combat.ReachModule;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 3))
    private double updateTargetedEntityModifySurvivalReach(double d) {
        ReachModule reachModule = Phosphor.moduleManager().getModule(ReachModule.class);
        if (reachModule.isEnabled()) return reachModule.entityReach.getValue();

        return d;
    }

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9))
    private double updateTargetedEntityModifySquaredMaxReach(double d) {
        ReachModule reachModule = Phosphor.moduleManager().getModule(ReachModule.class);
        if (reachModule.isEnabled()) return reachModule.entityReach.getValue();

        return d;
    }
}
