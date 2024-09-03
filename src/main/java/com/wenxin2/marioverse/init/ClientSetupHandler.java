package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.blocks.client.renderers.CoinBlockEntityRenderer;
import com.wenxin2.marioverse.blocks.client.renderers.WarpPipeBlockEntityRenderer;
import net.minecraft.client.renderer.BiomeColors;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Marioverse.MOD_ID, value = Dist.CLIENT)
public class ClientSetupHandler {
    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> {
            return world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) | 0xFF0000cc
                    : 0xFFFFFFFF;
        }, BlockRegistry.WATER_SPOUT.get());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.WARP_PIPE_MENU.get(), WarpPipeScreen::new);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.COIN_BLOCK_ENTITY.get(), CoinBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.WARP_PIPE_BLOCK_ENTITY.get(), WarpPipeBlockEntityRenderer::new);
    }
}
