package net.caffeinemc.phosphor.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static net.caffeinemc.phosphor.common.Phosphor.mc;

public class RotationUtils {
    public record Rotation(double yaw, double pitch) {}

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
}
