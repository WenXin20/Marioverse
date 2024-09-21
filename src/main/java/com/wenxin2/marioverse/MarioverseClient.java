package com.wenxin2.marioverse;

import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.client.particles.OneUpParticle;
import com.wenxin2.marioverse.client.renderers.CostumeLayer;
import com.wenxin2.marioverse.client.renderers.blocks.CoinBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.WarpPipeBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.entities.FireFlowerRenderer;
import com.wenxin2.marioverse.client.renderers.entities.MushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.OneUpMushroomRenderer;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.MenuRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Marioverse.MOD_ID, value = Dist.CLIENT)
public class MarioverseClient {

    public static void setup(IEventBus eventBus) {
        eventBus.addListener(MarioverseClient::addLayers);
    }

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

    private static void addLayers(final EntityRenderersEvent.AddLayers evt) {
        addEntityLayer(evt, EntityType.ARMOR_STAND);

        for (PlayerSkin.Model skin : evt.getSkins()) {
            addPlayerLayer(evt, skin);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, PlayerSkin.Model skin) {
        EntityRenderer<? extends Player> renderer = evt.getSkin(skin);
        boolean slim = skin == PlayerSkin.Model.SLIM;

        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new CostumeLayer(livingRenderer, evt.getEntityModels()));
        }
    }

    private static <T extends LivingEntity, M extends HumanoidModel<T>, R extends LivingEntityRenderer<T, M>> void addEntityLayer(
            EntityRenderersEvent.AddLayers evt, EntityType<? extends T> entityType) {
        R renderer = evt.getRenderer(entityType);

        if (renderer != null) {
            renderer.addLayer(new CostumeLayer<>(renderer, evt.getEntityModels()));
        }
    }
}
