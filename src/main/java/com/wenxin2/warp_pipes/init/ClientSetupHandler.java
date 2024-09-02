package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.client.WarpPipeScreen;
import com.wenxin2.warp_pipes.blocks.client.renderers.WarpPipeBlockEntityRenderer;
import net.minecraft.client.renderer.BiomeColors;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = WarpPipes.MODID, value = Dist.CLIENT)
public class ClientSetupHandler {
    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> {
            return world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) | 0xFF0000cc
                    : 0xFFFFFFFF;
        }, ModRegistry.WATER_SPOUT.get());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModRegistry.WARP_PIPE_MENU.get(), WarpPipeScreen::new);
    }

    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModRegistry.WARP_PIPE_BLOCK_ENTITY.get(), WarpPipeBlockEntityRenderer::new);
    }
}
