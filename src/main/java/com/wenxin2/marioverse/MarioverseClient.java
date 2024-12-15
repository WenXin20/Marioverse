package com.wenxin2.marioverse;

import com.google.common.collect.ImmutableList;
import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.client.particles.FirePoweredUpParticle;
import com.wenxin2.marioverse.client.particles.LargeRewardParticle;
import com.wenxin2.marioverse.client.particles.MediumRewardParticle;
import com.wenxin2.marioverse.client.particles.RewardParticle;
import com.wenxin2.marioverse.client.renderers.blocks.CoinBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.GoalPoleBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.WarpPipeBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.entities.FireGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.GoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.HeftyGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.MegaGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.MiniGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.layers.SuperStarLayer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.FireFlowerRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.MushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.OneUpMushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.SuperStarRenderer;
import com.wenxin2.marioverse.client.renderers.entities.projectile.BouncingFireballRenderer;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.MenuRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

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
        event.registerBlockEntityRenderer(BlockEntityRegistry.GOAL_POLE_BLOCK_ENTITY.get(), GoalPoleBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.WARP_PIPE_BLOCK_ENTITY.get(), WarpPipeBlockEntityRenderer::new);
    }

    @SubscribeEvent
    private static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.BOUNCING_FIREBALL.get(), BouncingFireballRenderer::new);
        
        event.registerEntityRenderer(EntityRegistry.FIRE_FLOWER.get(), FireFlowerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MUSHROOM.get(), MushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ONE_UP_MUSHROOM.get(), OneUpMushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SUPER_STAR.get(), SuperStarRenderer::new);

        event.registerEntityRenderer(EntityRegistry.FIRE_GOOMBA.get(), FireGoombaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GOOMBA.get(), GoombaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.HEFTY_GOOMBA.get(), HeftyGoombaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MEGA_GOOMBA.get(), MegaGoombaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MINI_GOOMBA.get(), MiniGoombaRenderer::new);
    }

    @SubscribeEvent
    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.COIN_GLINT.get(), SuspendedTownParticle.HappyVillagerProvider::new);
        event.registerSpriteSet(ParticleRegistry.EXCELLENT.get(), LargeRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.FANTASTIC.get(), LargeRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.FIRE_POWERED_UP.get(), FirePoweredUpParticle::new);
        event.registerSpriteSet(ParticleRegistry.GOOD.get(), RewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.GREAT.get(), MediumRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.INCREDIBLE.get(), LargeRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.POWERED_UP.get(), SuspendedTownParticle.HappyVillagerProvider::new);
        event.registerSpriteSet(ParticleRegistry.ONE_UP.get(), RewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.SUPER.get(), MediumRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.WONDERFUL.get(), LargeRewardParticle::new);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
//        List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(
//                BuiltInRegistries.ENTITY_TYPE.stream()
//                        .filter(DefaultAttributes::hasSupplier)
//                        .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
//                        .collect(Collectors.toList()));
//        entityTypes.forEach((entityType -> {
//            addLayerIfApplicable(entityType, event);
//        }));
        for (PlayerSkin.Model skinType : event.getSkins()){
            var skinRenderer = event.getSkin(skinType);
            if (skinRenderer instanceof PlayerRenderer livingEntityRenderer) {
                livingEntityRenderer.addLayer(new SuperStarLayer<>(skinRenderer));
            }
        }
    }

//    private static void addLayerIfApplicable(EntityType<? extends LivingEntity> entityType, EntityRenderersEvent.AddLayers event) {
//        LivingEntityRenderer renderer = null;
//        if (entityType != EntityType.ENDER_DRAGON) {
//            try {
//                renderer = event.getRenderer(entityType);
//            } catch (Exception e) {
//                Marioverse.LOGGER.warn("Could not apply rainbow color layer to " + BuiltInRegistries.ENTITY_TYPE.getKey(entityType) + ", has custom renderer that is not LivingEntityRenderer.");
//            }
//            if (renderer != null) {
//                renderer.addLayer(new SuperStarLayer<>(renderer));
//            }
//        }
//    }
}
