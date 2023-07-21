package net.caffeinemc.phosphor.api.rotation;

import net.caffeinemc.phosphor.api.event.events.*;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.event.orbit.EventPriority;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.hit.HitResult;

import static net.caffeinemc.phosphor.common.Phosphor.mc;

public class RotationManager {
    private boolean enabled;

    private RotationUtils.Rotation currentRotation;
    private float clientYaw, clientPitch;
    private float serverYaw, serverPitch;

    public RotationManager() {
        enabled = true;

        this.serverYaw = 0;
        this.serverPitch = 0;

        this.clientYaw = 0;
        this.clientPitch = 0;
    }

    public RotationUtils.Rotation getServerRotation() {
        return new RotationUtils.Rotation(serverYaw, serverPitch);
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
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

    @EventHandler(priority = EventPriority.LOWEST)
    void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket packet) {
            serverYaw = packet.getYaw(serverYaw);
            serverPitch = packet.getPitch(serverPitch);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerPositionLookS2CPacket packet) {
            serverYaw = packet.getYaw();
            serverPitch = packet.getPitch();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onSendMovementPacketPre(SendMovementPacketEvent.Pre event) {
        if (currentRotation != null && isEnabled()) {
            setClientRotation(currentRotation);
            setServerRotation(currentRotation);
        } else {
            setServerRotation(new RotationUtils.Rotation(mc.player.getYaw(), mc.player.getPitch()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onSendMovementPacketPost(SendMovementPacketEvent.Post event) {
        if (currentRotation != null && isEnabled()) {
            currentRotation.runCallback();
            resetClientRotation();
        }

        mc.player.setHeadYaw(serverYaw);
    }

    private boolean wasDisabled;

    @EventHandler(priority = EventPriority.LOWEST)
    private void onAttack(AttackEvent.Pre event) {
        if (!event.isCancelled() && isEnabled()) {
            disable();
            wasDisabled = true;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAttack(AttackEvent.Post event) {
        if (!isEnabled() && wasDisabled) {
            enable();
            wasDisabled = false;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemUse(ItemUseEvent.Pre event) {
        if (!event.isCancelled() && isEnabled()) {
            disable();
            wasDisabled = true;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onItemUse(ItemUseEvent.Post event) {
        if (!isEnabled() && wasDisabled) {
            enable();
            wasDisabled = false;
        }
    }
}
