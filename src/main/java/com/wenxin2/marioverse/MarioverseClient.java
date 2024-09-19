package com.wenxin2.marioverse;

import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.client.particles.OneUpParticle;
import com.wenxin2.marioverse.client.renderers.blocks.CoinBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.WarpPipeBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.curios.OneUpRenderer;
import com.wenxin2.marioverse.client.renderers.entities.FireFlowerRenderer;
import com.wenxin2.marioverse.client.renderers.entities.MushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.OneUpMushroomRenderer;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.MenuRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Marioverse.MOD_ID, value = Dist.CLIENT)
public class MarioverseClient {
    @SubscribeEvent
    private static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> {
            return world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) | 0xFF0000cc
                    : 0xFFFFFFFF;
        }, BlockRegistry.WATER_SPOUT.get());
    }

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.WARP_PIPE_MENU.get(), WarpPipeScreen::new);
    }

    @SubscribeEvent
    private static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.COIN_BLOCK_ENTITY.get(), CoinBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.WARP_PIPE_BLOCK_ENTITY.get(), WarpPipeBlockEntityRenderer::new);
    }

    @SubscribeEvent
    private static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.FIRE_FLOWER.get(), FireFlowerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MUSHROOM.get(), MushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ONE_UP_MUSHROOM.get(), OneUpMushroomRenderer::new);
    }

    @SubscribeEvent
    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.COIN_GLINT.get(), SuspendedTownParticle.HappyVillagerProvider::new);
        event.registerSpriteSet(ParticleRegistry.MUSHROOM_TRANSFORM.get(), SuspendedTownParticle.HappyVillagerProvider::new);
        event.registerSpriteSet(ParticleRegistry.ONE_UP.get(), OneUpParticle::new);
    }
}
