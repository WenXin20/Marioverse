package com.wenxin2.marioverse.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeybindRegistry {
    public static KeyMapping FIREBALL_SHOOT_KEY =
            new KeyMapping("key.marioverse.bouncing_fireball",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R,
                    "gui.marioverse.controls");

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(FIREBALL_SHOOT_KEY);
    }
}
