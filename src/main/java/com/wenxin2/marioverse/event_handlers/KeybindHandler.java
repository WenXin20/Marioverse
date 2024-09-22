package com.wenxin2.marioverse.event_handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Marioverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeybindHandler {
    public static KeyMapping FIREBALL_SHOOT_KEY;

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        FIREBALL_SHOOT_KEY = new KeyMapping("key.marioverse.bouncing_fireball",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.marioverse.category");
        event.register(FIREBALL_SHOOT_KEY);
    }

    @EventBusSubscriber(modid = Marioverse.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Player player = Minecraft.getInstance().player;
            if (player != null && (FIREBALL_SHOOT_KEY.isDown() || player.isSprinting())) {
                PacketHandler.sendToServer(new FireballShootPayload(player.blockPosition()));
            }
        }
    }
}
