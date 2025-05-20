package com.wenxin2.marioverse;

import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.client.particles.GlowingSuspendedTownParticle;
import com.wenxin2.marioverse.client.particles.GravityParticle;
import com.wenxin2.marioverse.client.particles.LargeRewardParticle;
import com.wenxin2.marioverse.client.particles.MediumRewardParticle;
import com.wenxin2.marioverse.client.particles.NoMovementParticle;
import com.wenxin2.marioverse.client.particles.RewardParticle;
import com.wenxin2.marioverse.client.renderers.accesories.ArmorRenderingExtension;
import com.wenxin2.marioverse.client.renderers.accesories.OneUpRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.CheckpointFlagBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.CoinBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.GoalPoleBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.PottedPiranhaPlantBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.StarCoinBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.WarpPipeBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.entities.FireGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.GoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.HeftyGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.IceCubeRenderer;
import com.wenxin2.marioverse.client.renderers.entities.KoopaShellRenderer;
import com.wenxin2.marioverse.client.renderers.entities.KoopaTroopaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.MegaGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.MiniGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.PiranhaPlantRenderer;
import com.wenxin2.marioverse.client.renderers.entities.layers.SuperStarLayer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.FireFlowerRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.IceFlowerRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.MushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.OneUpMushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.SuperStarRenderer;
import com.wenxin2.marioverse.client.renderers.entities.projectile.BouncingFireballRenderer;
import com.wenxin2.marioverse.client.renderers.entities.projectile.BouncingIceBallRenderer;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.MenuRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Marioverse.MOD_ID, value = Dist.CLIENT)
public class MarioverseClient {

    public static void clientSetup(final FMLClientSetupEvent event) {
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.ONE_UP_MUSHROOM.get(), OneUpRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.GOLDEN_KOOPA_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.GREEN_KOOPA_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.RED_KOOPA_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_HAT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_SHIRT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_PANTS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_FIRE_HAT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_FIRE_SHIRT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_FIRE_PANTS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_FIRE_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_ICE_HAT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_ICE_SHIRT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_ICE_PANTS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.MARIO_ICE_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_HAT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_SHIRT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_PANTS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_FIRE_HAT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_FIRE_SHIRT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_FIRE_PANTS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_FIRE_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_ICE_HAT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_ICE_SHIRT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_ICE_PANTS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.LUIGI_ICE_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_CROWN.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_BODICE.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_DRESS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_FIRE_BODICE.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_FIRE_DRESS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_FIRE_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_ICE_BODICE.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_ICE_DRESS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.PEACH_ICE_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
    }

    public static void addPackFinder(final AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {

            ResourceLocation packLocation = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "resourcepacks/marioverse/truly_invisible");
            Component packNameDisplay = Component.translatable("resource_pack.marioverse.truly_invisible");

            event.addPackFinders(packLocation, PackType.CLIENT_RESOURCES, packNameDisplay,
                    PackSource.BUILT_IN, false, Pack.Position.TOP);
        }
    }

    @SubscribeEvent
    private static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> {
            return world != null && pos != null
                    ? BiomeColors.getAverageWaterColor(world, pos) | 0xFF0000cc : 0xFFFFFFFF;
        }, BlockRegistry.WATER_SPOUT.get());
    }

    @SubscribeEvent
    private static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
    }

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.WARP_PIPE_MENU.get(), WarpPipeScreen::new);
    }

    @SubscribeEvent
    private static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.CHECKPOINT_FLAG_BLOCK_ENTITY.get(), CheckpointFlagBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.COIN_BLOCK_ENTITY.get(), CoinBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.GOAL_POLE_BLOCK_ENTITY.get(), GoalPoleBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.POTTED_PIRANHA_PLANT_BLOCK_ENTITY.get(), PottedPiranhaPlantBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.STAR_COIN_BLOCK_ENTITY.get(), StarCoinBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.WARP_PIPE_BLOCK_ENTITY.get(), WarpPipeBlockEntityRenderer::new);
    }

    @SubscribeEvent
    private static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.BOUNCING_FIREBALL.get(), BouncingFireballRenderer::new);
        event.registerEntityRenderer(EntityRegistry.BOUNCING_ICE_BALL.get(), BouncingIceBallRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ICE_CUBE.get(), IceCubeRenderer::new);

        event.registerEntityRenderer(EntityRegistry.FIRE_FLOWER.get(), FireFlowerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ICE_FLOWER.get(), IceFlowerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MUSHROOM.get(), MushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ONE_UP_MUSHROOM.get(), OneUpMushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SUPER_STAR.get(), SuperStarRenderer::new);

        event.registerEntityRenderer(EntityRegistry.FIRE_GOOMBA.get(), FireGoombaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GOOMBA.get(), GoombaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GOLD_KOOPA_SHELL.get(), KoopaShellRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GOLD_KOOPA_TROOPA.get(), KoopaTroopaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GREEN_KOOPA_SHELL.get(), KoopaShellRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GREEN_KOOPA_TROOPA.get(), KoopaTroopaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.HEFTY_GOOMBA.get(), HeftyGoombaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MEGA_GOOMBA.get(), MegaGoombaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MINI_GOOMBA.get(), MiniGoombaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.PIRANHA_PLANT.get(), PiranhaPlantRenderer::new);
        event.registerEntityRenderer(EntityRegistry.RED_KOOPA_SHELL.get(), KoopaShellRenderer::new);
        event.registerEntityRenderer(EntityRegistry.RED_KOOPA_TROOPA.get(), KoopaTroopaRenderer::new);
    }

    @SubscribeEvent
    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.COIN_GLINT.get(), GlowingSuspendedTownParticle.CoinGlintProvider::new);
        event.registerSpriteSet(ParticleRegistry.EXCELLENT.get(), LargeRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.FANTASTIC.get(), LargeRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.FIRE_POWERED_UP.get(), GlowingSuspendedTownParticle.FireProvider::new);
        event.registerSpriteSet(ParticleRegistry.GLOWING_STAR.get(), GlowingSuspendedTownParticle.GlowingProvider::new);
        event.registerSpriteSet(ParticleRegistry.GOOD.get(), RewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.GREAT.get(), MediumRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.GOLD_KOOPA_SHELL_SHATTER.get(), GravityParticle.BreakEntityProvider::new);
        event.registerSpriteSet(ParticleRegistry.GREEN_KOOPA_SHELL_SHATTER.get(), GravityParticle.BreakEntityProvider::new);
        event.registerSpriteSet(ParticleRegistry.ICE_CUBE_SHATTER.get(), GravityParticle.BreakEntityProvider::new);
        event.registerSpriteSet(ParticleRegistry.ICE_POWERED_UP.get(), GlowingSuspendedTownParticle.FireProvider::new);
        event.registerSpriteSet(ParticleRegistry.ICE_STAR.get(), GlowingSuspendedTownParticle.GlowingProvider::new);
        event.registerSpriteSet(ParticleRegistry.INCREDIBLE.get(), LargeRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_AMETHYST_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_BLACKSTONE_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_COPPER_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_DEEPSLATE_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_DEEPSLATE_TILE_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_END_STONE_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_FUNGAL_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_MOSSY_STONE_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_MUD_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_NETHER_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_PRISMARINE_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_PURPUR_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_QUARTZ_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_RED_NETHER_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_RED_SANDSTONE_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_SANDSTONE_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_STONE_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_TUFF_BRICK_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION.get(), NoMovementParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.POWERED_UP.get(), GlowingSuspendedTownParticle.FireProvider::new);
        event.registerSpriteSet(ParticleRegistry.ONE_UP.get(), RewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.RED_KOOPA_SHELL_SHATTER.get(), GravityParticle.BreakEntityProvider::new);
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
