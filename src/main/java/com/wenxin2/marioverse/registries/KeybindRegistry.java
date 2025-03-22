package com.wenxin2.marioverse.registries;

import com.mojang.blaze3d.platform.InputConstants;
import com.wenxin2.marioverse.Marioverse;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Marioverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeybindRegistry {
    public static KeyMapping ACTIVATE_POWER_UP;

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        ACTIVATE_POWER_UP = new KeyMapping("key.marioverse.activate_power_up",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R,
                "gui.marioverse.controls");

        event.register(ACTIVATE_POWER_UP);
    }
}
