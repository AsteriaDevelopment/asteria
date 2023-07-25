package net.caffeinemc.phosphor.api.rotation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static net.caffeinemc.phosphor.common.Phosphor.mc;

public class RotationUtils {
    public static class Rotation {
        private double yaw, pitch;
        private Runnable callback;

        public Rotation(double yaw, double pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public Rotation(double yaw, double pitch, Runnable callback) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.callback = callback;
        }

        public double yaw() {
            return yaw;
        }

        public double pitch() {
            return pitch;
        }

        public void sendPacket() {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) yaw(), (float) pitch, mc.player.isOnGround()));
        }

        public void runCallback() {
            if (callback != null)
                callback.run();
        }
    }

    public static Rotation getDirection(Entity entity, Vec3d vec) {
        double dx = vec.x - entity.getX(),
                dy = vec.y - entity.getY(),
                dz = vec.z - entity.getZ(),
                dist = MathHelper.sqrt((float) (dx * dx + dz * dz));

        return new Rotation(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90.0), -MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dy, dist))));
    }

    public static double getAngleToRotation(Rotation rotation) {
        double currentYaw = MathHelper.wrapDegrees(mc.player.getYaw());
        double currentPitch = MathHelper.wrapDegrees(mc.player.getPitch());

        double diffYaw = MathHelper.wrapDegrees(currentYaw - rotation.yaw());
        double diffPitch = MathHelper.wrapDegrees(currentPitch - rotation.pitch());

        return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
    }

    public static Vec3d getPlayerLookVec(float yaw, float pitch) {
        float f = 0.017453292F;
        float pi = (float)Math.PI;

        float f1 = MathHelper.cos(-yaw * f - pi);
        float f2 = MathHelper.sin(-yaw * f - pi);
        float f3 = -MathHelper.cos(-pitch * f);
        float f4 = MathHelper.sin(-pitch * f);

        return new Vec3d(f2 * f3, f4, f1 * f3).normalize();
    }

    public static Vec3d getPlayerLookVec(PlayerEntity player) {
        return getPlayerLookVec(player.getYaw(), player.getPitch());
    }
}
