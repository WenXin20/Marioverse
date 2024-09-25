package com.wenxin2.marioverse.init;

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
    public static KeyMapping FIREBALL_SHOOT_KEY;

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        FIREBALL_SHOOT_KEY = new KeyMapping("key.marioverse.bouncing_fireball",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R,
                "gui.marioverse.controls");
        event.register(FIREBALL_SHOOT_KEY);
    }
}
