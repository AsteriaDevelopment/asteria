package net.caffeinemc.phosphor.api.util;

import net.caffeinemc.phosphor.api.event.events.*;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.event.orbit.EventPriority;
import net.caffeinemc.phosphor.mixin.MinecraftClientAccessor;
import net.caffeinemc.phosphor.mixin.MouseAccessor;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.caffeinemc.phosphor.common.Phosphor.mc;

public class MouseSimulation {
    private boolean enabled;

    private final HashMap<Integer, Integer> mouseButtons = new HashMap<>();
    private final ExecutorService clickExecutor = Executors.newFixedThreadPool(1000);

    public MouseSimulation() {
        this.enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isFakeMousePressed(int keyCode) {
        return mouseButtons.containsKey(keyCode);
    }

    public MouseAccessor getMouse() {
        return (MouseAccessor) ((MinecraftClientAccessor) mc).getMouse();
    }

    public void mouseClick(int keyCode, int ticks) {
        if (!isFakeMousePressed(keyCode)) {
            mouseButtons.put(keyCode, ticks);
            clickExecutor.submit(() -> getMouse().callOnMouseButton(mc.getWindow().getHandle(), keyCode, GLFW.GLFW_PRESS, 0));
        }
    }

    public void mouseClick(int keyCode) {
        mouseClick(keyCode, 1);
    }

    public void mouseRelease(int keyCode) {
        if (isFakeMousePressed(keyCode)) {
            getMouse().callOnMouseButton(mc.getWindow().getHandle(), keyCode, GLFW.GLFW_RELEASE, 0);
            mouseButtons.remove(keyCode);
        }
    }
    
    private void checkMouse(int keyCode) {
        if (isFakeMousePressed(keyCode)) {
            int ticksLeft = mouseButtons.get(keyCode);

            if (ticksLeft > 0) {
                mouseButtons.replace(keyCode, ticksLeft - 1);
            } else {
                mouseRelease(keyCode);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerTick(PlayerTickEvent event) {
        checkMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
        checkMouse(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }

    @EventHandler
    private void onItemUse(ItemUseEvent.Pre event) {
        if (isFakeMousePressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            event.cancel();
        }
    }

    @EventHandler
    private void onAttack(AttackEvent.Pre event) {
        if (isFakeMousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            event.cancel();
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent.Pre event) {
        if (isFakeMousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            event.cancel();
        }
    }
}
