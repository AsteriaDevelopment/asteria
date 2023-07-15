package net.caffeinemc.phosphor.api.rotation;

import net.caffeinemc.phosphor.api.event.events.SendMovementPacketEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;

import static net.caffeinemc.phosphor.common.Phosphor.mc;

public class RotationManager {
    private RotationUtils.Rotation currentRotation;
    private float clientYaw, clientPitch;
    private float serverYaw, serverPitch;

    public RotationManager() {
        this.serverYaw = 0;
        this.serverPitch = 0;

        this.clientYaw = 0;
        this.clientPitch = 0;
    }

    public RotationUtils.Rotation getServerRotation() {
        return new RotationUtils.Rotation(serverYaw, serverPitch);
    }

    public void setRotation(RotationUtils.Rotation rotation) {
        currentRotation = rotation;
        if (currentRotation != null) {
            setServerRotation(currentRotation);
        }
    }

    public void setRotation(double yaw, double pitch) {
        setRotation(new RotationUtils.Rotation(yaw, pitch));
    }

    private void resetClientRotation() {
        mc.player.setYaw(clientYaw);
        mc.player.setPitch(clientPitch);
    }

    private void setClientRotation(RotationUtils.Rotation rotation) {
        this.clientYaw = mc.player.getYaw();
        this.clientPitch = mc.player.getPitch();

        mc.player.setYaw((float) rotation.yaw());
        mc.player.setPitch((float) rotation.pitch());
    }

    private void setServerRotation(RotationUtils.Rotation rotation) {
        this.serverYaw = (float) rotation.yaw();
        this.serverPitch = (float) rotation.pitch();
    }

    @EventHandler
    private void onSendMovementPacketPre(SendMovementPacketEvent.Pre event) {
        if (currentRotation != null) {
            setClientRotation(currentRotation);
            setServerRotation(currentRotation);
        } else {
            setServerRotation(new RotationUtils.Rotation(mc.player.getYaw(), mc.player.getPitch()));
        }
    }

    @EventHandler
    private void onSendMovementPacketPost(SendMovementPacketEvent.Post event) {
        if (currentRotation != null) {
            currentRotation.runCallback();
            resetClientRotation();
        }
    }
}
