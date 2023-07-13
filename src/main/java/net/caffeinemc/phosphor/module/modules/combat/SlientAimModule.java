package net.caffeinemc.phosphor.module.modules.combat;

import net.caffeinemc.phosphor.api.event.events.AttackEvent;
import net.caffeinemc.phosphor.api.event.events.HudRenderEvent;
import net.caffeinemc.phosphor.api.event.events.MouseUpdateEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.event.orbit.EventPriority;
import net.caffeinemc.phosphor.api.util.PlayerUtils;
import net.caffeinemc.phosphor.api.util.RenderUtils;
import net.caffeinemc.phosphor.api.util.RotationUtils;
import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.BooleanSetting;
import net.caffeinemc.phosphor.module.setting.settings.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class SlientAimModule extends Module {
    public final BooleanSetting lookAtNearest = new BooleanSetting("Look At Nearest Hitbox's Corner", this, false);
    public final NumberSetting yawSpeed = new NumberSetting("Horizontal Speed", this, 4d, 1d, 10d, 0.1d);
    public final NumberSetting pitchSpeed = new NumberSetting("Verical Speed", this, 2d, 0.5d, 10d, 0.1d);
    public final NumberSetting fov = new NumberSetting("FOV", this, 90d, 1d, 180d, 1d);
    public final BooleanSetting fovCircle = new BooleanSetting("FOV Circle", this, true);

    public SlientAimModule() {
        super("SlientAim", "Automatically aims at players and hit them for you.", Category.COMBAT);
    }

    private RotationUtils.Rotation beforeRot = new RotationUtils.Rotation(0, 0);
    private RotateStatus rotateStatus = RotateStatus.IDLE;

    private enum RotateStatus {
        IDLE,
        TO_TARGET,
        RETURN
    }

    @Override
    public void onEnable() {
        beforeRot = new RotationUtils.Rotation(0, 0);
        rotateStatus = RotateStatus.IDLE;
    }

    @EventHandler
    public void onMouseUpdate(MouseUpdateEvent event) {
        if (mc.currentScreen == null) {
            if (rotateStatus != RotateStatus.RETURN) {
//                PlayerEntity targetPlayer = PlayerUtils.findNearestPlayer(mc.player, (float) PlayerUtils.getReach() + 1f, true);
                Entity targetPlayer = PlayerUtils.findNearestEntity(mc.player, (float) PlayerUtils.getReach() + 1f, true);

                if (targetPlayer == null)
                    return;

                if (mc.player.squaredDistanceTo(targetPlayer) >= 9.0f)
                    return;

                Vec3d targetPlayerPos = targetPlayer.getPos();

                if (lookAtNearest.isEnabled()) {
                    double halfHitboxSize = (targetPlayer.getBoundingBox().getXLength() / 2) - 0.01d;
                    double offsetX;

                    if (mc.player.getX() - targetPlayer.getX() > 0) {
                        offsetX = halfHitboxSize;
                    } else {
                        offsetX = -halfHitboxSize;
                    }

                    double offsetZ;

                    if (mc.player.getZ() - targetPlayer.getZ() > 0) {
                        offsetZ = halfHitboxSize;
                    } else {
                        offsetZ = -halfHitboxSize;
                    }

                    targetPlayerPos = targetPlayerPos.add(offsetX, 0, offsetZ);
                }

                RotationUtils.Rotation targetRot = RotationUtils.getDirection(mc.player, targetPlayerPos);

                if (RotationUtils.getAngleToRotation(targetRot) > fov.getValue() / 2)
                    return;

                if (rotateStatus == RotateStatus.IDLE) {
                    beforeRot = new RotationUtils.Rotation(mc.player.getYaw(), mc.player.getPitch());
                    rotateStatus = RotateStatus.TO_TARGET;
                }

                float yawStrength = yawSpeed.getFValue() / 50;
                float pitchStrength = pitchSpeed.getFValue() / 50;

                float yaw = MathHelper.lerpAngleDegrees(yawStrength, mc.player.getYaw(), (float) targetRot.yaw());
                float pitch = MathHelper.lerpAngleDegrees(pitchStrength, mc.player.getPitch(), (float) targetRot.pitch());

                mc.player.setYaw(yaw);
                mc.player.setPitch(pitch);

                if (mc.crosshairTarget instanceof EntityHitResult entityHit) {
                    if (entityHit.getType() != HitResult.Type.MISS) {
                        if (entityHit.getEntity() != targetPlayer)
                            return;

                        mc.interactionManager.attackEntity(mc.player, entityHit.getEntity());
                        mc.player.swingHand(Hand.MAIN_HAND);

                        rotateStatus = RotateStatus.RETURN;
                    }
                }
            }

            if (rotateStatus == RotateStatus.RETURN) {
                float yawStrength = yawSpeed.getFValue() / 50;
                float pitchStrength = pitchSpeed.getFValue() / 50;

                float yaw = MathHelper.lerpAngleDegrees(yawStrength, mc.player.getYaw(), (float) beforeRot.yaw());
                float pitch = MathHelper.lerpAngleDegrees(pitchStrength, mc.player.getPitch(), (float) beforeRot.pitch());

                mc.player.setYaw(yaw);
                mc.player.setPitch(pitch);

                if (MathHelper.wrapDegrees(beforeRot.yaw()) == MathHelper.wrapDegrees(yaw) &&
                        MathHelper.wrapDegrees(beforeRot.pitch()) == MathHelper.wrapDegrees(pitch)) {
                    rotateStatus = RotateStatus.IDLE;
                }
            }
        }
    }

    @EventHandler
    private void onHudRender(HudRenderEvent event) {
        if (fovCircle.isEnabled()) {
            RenderUtils.Render2D.renderCircle(event.matrices, new Color(255, 255, 255, 100), mc.getWindow().getScaledWidth() / 2, mc.getWindow().getScaledHeight() / 2, fov.getFValue() * 2, 60);
        }
    }
}
