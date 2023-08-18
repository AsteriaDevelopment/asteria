package net.caffeinemc.phosphor.api.util;

import net.caffeinemc.phosphor.api.event.events.AttackEvent;
import net.caffeinemc.phosphor.api.event.events.BlockBreakEvent;
import net.caffeinemc.phosphor.api.event.events.ItemUseEvent;
import net.caffeinemc.phosphor.api.event.events.WorldRenderEvent;
import net.caffeinemc.phosphor.api.event.orbit.EventHandler;
import net.caffeinemc.phosphor.api.event.orbit.EventPriority;
import net.caffeinemc.phosphor.mixin.MinecraftClientAccessor;
import net.caffeinemc.phosphor.mixin.MouseAccessor;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

import static net.caffeinemc.phosphor.common.Phosphor.mc;

public class MouseSimulation {
    private final HashMap<Integer, Integer> mouseButtons = new HashMap<>();
    private boolean cancelLeft = false;
    private boolean cancelRight = false;

    public boolean isFakeMousePressed(int keyCode) {
        return mouseButtons.containsKey(keyCode);
    }

    public MouseAccessor getMouse() {
        return (MouseAccessor) ((MinecraftClientAccessor) mc).getMouse();
    }

    public void mouseClick(int keyCode, int frames) {
        if (!isFakeMousePressed(keyCode)) {
            if (!cancelRight) cancelRight = keyCode == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
            if (!cancelLeft) cancelLeft = keyCode == GLFW.GLFW_MOUSE_BUTTON_LEFT;

            mouseButtons.put(keyCode, frames);
            getMouse().callOnMouseButton(mc.getWindow().getHandle(), keyCode, GLFW.GLFW_PRESS, 0);
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
    private void onWorldRender(WorldRenderEvent event) {
        checkMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
        checkMouse(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onItemUse(ItemUseEvent.Pre event) {
        if (cancelRight) {
            event.cancel();
            cancelRight = mc.options.useKey.isPressed() && !KeyUtils.isKeyPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAttack(AttackEvent.Pre event) {
        if (cancelLeft) {
            event.cancel();
            cancelLeft = mc.options.attackKey.isPressed() && !KeyUtils.isKeyPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockBreak(BlockBreakEvent.Pre event) {
        if (cancelLeft) {
            event.cancel();
            cancelLeft = mc.options.attackKey.isPressed() && !KeyUtils.isKeyPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
        }
    }
}
