package net.caffeinemc.phosphor.mixin;

import net.caffeinemc.phosphor.api.util.PlayerUtils;
import net.caffeinemc.phosphor.api.util.RenderUtils;
import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.gui.RadiumMenu;
import net.caffeinemc.phosphor.module.modules.render.ShieldStatusModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(method = "renderLabelIfPresent",
            at = @At(value = "INVOKE",
                     shift = At.Shift.BEFORE,
                     target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                     opcode = Opcodes.INVOKEVIRTUAL))
    private void onRenderLabel(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (RadiumMenu.isClientEnabled()) {
            ShieldStatusModule shieldStatus = Phosphor.moduleManager().getModule(ShieldStatusModule.class);
            if (shieldStatus.isEnabled()) {
                if (entity instanceof PlayerEntity player) {
                    int width = MinecraftClient.getInstance().textRenderer.getWidth(text);
                    float shieldProgress = PlayerUtils.getShieldCooldownProgress(player);

                    double x = -(width / 2) - 1;
                    double y = (text.getString().equals("deadmau5") ? -10 : 0);
                    double y2 = y - 1;

                    // x - 1.0F, y + 9.0F, x + 1.0F, y - 1.0F
//                    Vec3d start = new Vec3d(x, y2 + (shieldProgress == 0.0f ? 10 : 10 * (1 - shieldProgress)), 0);
                    Vec3d start = new Vec3d(x, y2 + (10 * shieldProgress), 0);
                    Vec3d end = new Vec3d(x, y2, 0);

                    RenderUtils.Render3D.renderLine(matrices, Color.WHITE, start, end);
                }
            }
        }
    }
}
