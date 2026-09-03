package com.wenxin2.marioverse;

import com.google.common.collect.ImmutableList;
import com.wenxin2.marioverse.blocks.client.BlockSpawnerScreen;
import com.wenxin2.marioverse.blocks.client.QuestionBlockScreen;
import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.client.ArrowAtlas;
import com.wenxin2.marioverse.client.particles.GlowingSuspendedTownParticle;
import com.wenxin2.marioverse.client.particles.GravityParticle;
import com.wenxin2.marioverse.client.particles.LargeRewardParticle;
import com.wenxin2.marioverse.client.particles.MediumRewardParticle;
import com.wenxin2.marioverse.client.particles.NoMovementItemParticle;
import com.wenxin2.marioverse.client.particles.PollenParticle;
import com.wenxin2.marioverse.client.particles.RewardParticle;
import com.wenxin2.marioverse.client.renderers.blocks.ArrowSignBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.curios.OneUpRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.BlockSpawnerBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.CheckpointFlagBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.CoinBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.GoalPoleBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.PottedPiranhaPlantBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.StarCoinBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.blocks.WarpPipeBlockEntityRenderer;
import com.wenxin2.marioverse.client.renderers.curios.CostumeCurioRenderer;
import com.wenxin2.marioverse.client.renderers.entities.BooRenderer;
import com.wenxin2.marioverse.client.renderers.entities.CheepCheepRenderer;
import com.wenxin2.marioverse.client.renderers.entities.DeepCheepRenderer;
import com.wenxin2.marioverse.client.renderers.entities.DryBonesPartRenderer;
import com.wenxin2.marioverse.client.renderers.entities.DryBonesRenderer;
import com.wenxin2.marioverse.client.renderers.entities.EepCheepRenderer;
import com.wenxin2.marioverse.client.renderers.entities.FireGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.GoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.HeftyGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.IceCubeRenderer;
import com.wenxin2.marioverse.client.renderers.entities.KoopaShellRenderer;
import com.wenxin2.marioverse.client.renderers.entities.KoopaTroopaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.MegaGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.MiniGoombaRenderer;
import com.wenxin2.marioverse.client.renderers.entities.PiranhaPlantRenderer;
import com.wenxin2.marioverse.client.renderers.entities.PokeyBodyRenderer;
import com.wenxin2.marioverse.client.renderers.entities.PokeyRenderer;
import com.wenxin2.marioverse.client.renderers.entities.PorcupufferRenderer;
import com.wenxin2.marioverse.client.renderers.entities.SpinyCheepCheepRenderer;
import com.wenxin2.marioverse.client.renderers.entities.SplunkinRenderer;
import com.wenxin2.marioverse.client.renderers.entities.WoodTypeBoatRenderer;
import com.wenxin2.marioverse.client.renderers.entities.layers.SuperStarGeoLayer;
import com.wenxin2.marioverse.client.renderers.entities.layers.SuperStarLayer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.DashMushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.FireFlowerRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.IceFlowerRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.MegaMushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.MiniMushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.SuperMushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.OneUpMushroomRenderer;
import com.wenxin2.marioverse.client.renderers.entities.power_ups.SuperStarRenderer;
import com.wenxin2.marioverse.client.renderers.entities.projectile.BouncingFireballRenderer;
import com.wenxin2.marioverse.client.renderers.entities.projectile.BouncingIceBallRenderer;
import com.wenxin2.marioverse.client.renderers.entities.projectile.LargeSnowballRenderer;
import com.wenxin2.marioverse.dynamic_pack.DynamicClientResources;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.MenuRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Marioverse.MOD_ID, value = Dist.CLIENT)
public class MarioverseClient {

    public static void clientSetup(final FMLClientSetupEvent event) {
        CuriosRendererRegistry.register(ItemRegistry.ONE_UP_MUSHROOM.get(), OneUpRenderer::new);

        CuriosRendererRegistry.register(ItemRegistry.CHRISTMAS_HAT.get(), () -> new CostumeCurioRenderer(EquipmentSlot.HEAD));
        CuriosRendererRegistry.register(ItemRegistry.CROWN.get(), () -> new CostumeCurioRenderer(EquipmentSlot.HEAD));
        CuriosRendererRegistry.register(ItemRegistry.HAT.get(), () -> new CostumeCurioRenderer(EquipmentSlot.HEAD));
        CuriosRendererRegistry.register(ItemRegistry.PLASTIC_BUCKET.get(), () -> new CostumeCurioRenderer(EquipmentSlot.HEAD));

        CuriosRendererRegistry.register(ItemRegistry.BODICE.get(), () -> new CostumeCurioRenderer(EquipmentSlot.CHEST));
        CuriosRendererRegistry.register(ItemRegistry.SHIRT.get(), () -> new CostumeCurioRenderer(EquipmentSlot.CHEST));

        CuriosRendererRegistry.register(ItemRegistry.DRESS.get(), () -> new CostumeCurioRenderer(EquipmentSlot.LEGS));
        CuriosRendererRegistry.register(ItemRegistry.PANTS.get(), () -> new CostumeCurioRenderer(EquipmentSlot.LEGS));

        CuriosRendererRegistry.register(ItemRegistry.GOLDEN_KOOPA_SHOES.get(), () -> new CostumeCurioRenderer(EquipmentSlot.FEET));
        CuriosRendererRegistry.register(ItemRegistry.GREEN_KOOPA_SHOES.get(), () -> new CostumeCurioRenderer(EquipmentSlot.FEET));
        CuriosRendererRegistry.register(ItemRegistry.HEELS.get(), () -> new CostumeCurioRenderer(EquipmentSlot.FEET));
        CuriosRendererRegistry.register(ItemRegistry.RED_KOOPA_SHOES.get(), () -> new CostumeCurioRenderer(EquipmentSlot.FEET));
        CuriosRendererRegistry.register(ItemRegistry.SHOES.get(), () -> new CostumeCurioRenderer(EquipmentSlot.FEET));
        CuriosRendererRegistry.register(ItemRegistry.WHITE_KOOPA_SHOES.get(), () -> new CostumeCurioRenderer(EquipmentSlot.FEET));
    }

    @SubscribeEvent
    private static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> {
            return world != null && pos != null
                    ? BiomeColors.getAverageWaterColor(world, pos) | 0x0000cc : 0xFFFFFFFF;
        }, BlockRegistry.WATER_SPOUT.get());
    }

    @SubscribeEvent
    private static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
                    if (tintIndex == 1)
                        return 0x3F76E4;
                    return -1;
                }, ItemRegistry.PLASTIC_WATER_BUCKET.get()
        );
        event.register(
                (stack, tintIndex) -> {
                    if (tintIndex == 0)
                        return DyedItemColor.getOrDefault(stack, 0xED0011);
                    return -1;
                },
                ItemRegistry.CHRISTMAS_HAT.get()
        );
        event.register(
                (stack, tintIndex) -> {
                    if (tintIndex == 0)
                        return DyedItemColor.getOrDefault(stack, 0xF6343A);
                    return -1;
                },
                ItemRegistry.HAT.get(),
                ItemRegistry.SHIRT.get()
        );
        event.register(
                (stack, tintIndex) -> {
                    if (tintIndex == 0)
                        return DyedItemColor.getOrDefault(stack, 0x325EFF);
                    return -1;
                },
                ItemRegistry.PANTS.get()
        );
        event.register(
                (stack, tintIndex) -> {
                    if (tintIndex == 0)
                        return DyedItemColor.getOrDefault(stack, 0xA94535);
                    return -1;
                },
                ItemRegistry.SHOES.get()
        );
        event.register(
                (stack, tintIndex) -> {
                    if (tintIndex == 0)
                        return DyedItemColor.getOrDefault(stack, 0xFF647D);
                    return -1;
                },
                ItemRegistry.CROWN.get()
        );
        event.register(
                (stack, tintIndex) -> {
                    if (tintIndex == 0)
                        return DyedItemColor.getOrDefault(stack, 0xFFC1D7);
                    return -1;
                },
                ItemRegistry.BODICE.get(),
                ItemRegistry.DRESS.get(),
                ItemRegistry.HEELS.get()
        );
        event.register(
                (stack, tintIndex) -> tintIndex == 0 ? GrassColor.getDefaultColor() : -1,
                BlockRegistry.SHROOMGRASS_BLOCK.get()
        );
    }

    @SubscribeEvent
    private static void registerReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ArrowAtlas.get());
    }

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.BLOCK_SPAWNER_MENU.get(), BlockSpawnerScreen::new);
        event.register(MenuRegistry.QUESTION_BLOCK_MENU.get(), QuestionBlockScreen::new);
        event.register(MenuRegistry.WARP_PIPE_MENU.get(), WarpPipeScreen::new);
    }

    @SubscribeEvent
    private static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.ARROW_SIGN.get(), ArrowSignBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.BLOCK_SPAWNER_BLOCK_ENTITY.get(), BlockSpawnerBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.CHECKPOINT_FLAG_BLOCK_ENTITY.get(), CheckpointFlagBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.COIN_BLOCK_ENTITY.get(), CoinBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.GOAL_POLE_BLOCK_ENTITY.get(), GoalPoleBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.POTTED_PIRANHA_PLANT_BLOCK_ENTITY.get(), PottedPiranhaPlantBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.STAR_COIN_BLOCK_ENTITY.get(), StarCoinBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.WARP_PIPE_BLOCK_ENTITY.get(), WarpPipeBlockEntityRenderer::new);
    }

    @SubscribeEvent
    private static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WoodTypeBoatRenderer.MUSHROOT_BOAT_LAYER, BoatModel::createBodyModel);
        event.registerLayerDefinition(WoodTypeBoatRenderer.MUSHROOT_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel);
    }

    @SubscribeEvent
    private static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.MUSHROOT_BOAT.get(),
                context -> new WoodTypeBoatRenderer(context, false,
                        WoodTypeBoatRenderer.MUSHROOT_BOAT_LAYER, Marioverse.id("textures/entity/boat/mushroot.png")));
        event.registerEntityRenderer(EntityRegistry.MUSHROOT_CHEST_BOAT.get(),
                context -> new WoodTypeBoatRenderer(context, true,
                        WoodTypeBoatRenderer.MUSHROOT_CHEST_BOAT_LAYER, Marioverse.id("textures/entity/chest_boat/mushroot.png")));

        event.registerEntityRenderer(EntityRegistry.BOUNCING_FIREBALL.get(), BouncingFireballRenderer::new);
        event.registerEntityRenderer(EntityRegistry.BOUNCING_ICE_BALL.get(), BouncingIceBallRenderer::new);
        event.registerEntityRenderer(EntityRegistry.LARGE_SNOWBALL.get(), LargeSnowballRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ICE_CUBE.get(), IceCubeRenderer::new);

        event.registerEntityRenderer(EntityRegistry.DASH_MUSHROOM.get(), DashMushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.FIRE_FLOWER.get(), FireFlowerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ICE_FLOWER.get(), IceFlowerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MEGA_MUSHROOM.get(), MegaMushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MINI_MUSHROOM.get(), MiniMushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ONE_UP_MUSHROOM.get(), OneUpMushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SUPER_MUSHROOM.get(), SuperMushroomRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SUPER_STAR.get(), SuperStarRenderer::new);

        event.registerEntityRenderer(EntityRegistry.BOO.get(), BooRenderer::new);
        event.registerEntityRenderer(EntityRegistry.CHEEP_CHEEP.get(), CheepCheepRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DEEP_CHEEP.get(), DeepCheepRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DRY_BONES.get(), DryBonesRenderer::new);
        event.registerEntityRenderer(EntityRegistry.EEP_CHEEP.get(), EepCheepRenderer::new);
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
        event.registerEntityRenderer(EntityRegistry.POKEY.get(), PokeyRenderer::new);
        event.registerEntityRenderer(EntityRegistry.POKEY_BODY.get(), PokeyBodyRenderer::new);
        event.registerEntityRenderer(EntityRegistry.PORCUPUFFER.get(), PorcupufferRenderer::new);
        event.registerEntityRenderer(EntityRegistry.RED_KOOPA_SHELL.get(), KoopaShellRenderer::new);
        event.registerEntityRenderer(EntityRegistry.RED_KOOPA_TROOPA.get(), KoopaTroopaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SNOW_POKEY.get(), PokeyRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SNOW_POKEY_BODY.get(), PokeyBodyRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SPINY_CHEEP_CHEEP.get(), SpinyCheepCheepRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SPLUNKIN.get(), SplunkinRenderer::new);

        event.registerEntityRenderer(EntityRegistry.DRY_BONES_HEAD.get(), DryBonesPartRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DRY_BONES_LEFT_ARM.get(), DryBonesPartRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DRY_BONES_LEFT_LEG.get(), DryBonesPartRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DRY_BONES_RIGHT_ARM.get(), DryBonesPartRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DRY_BONES_RIGHT_LEG.get(), DryBonesPartRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DRY_BONES_SHELL.get(), DryBonesPartRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DRY_BONES_TAIL.get(), DryBonesPartRenderer::new);
    }

    @SubscribeEvent
    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ParticleRegistry.NO_MOVEMENT_ITEM.get(), new NoMovementItemParticle.Provider());
        event.registerSpriteSet(ParticleRegistry.BLUE_STAR.get(), GlowingSuspendedTownParticle.CoinGlintProvider::new);
        event.registerSpriteSet(ParticleRegistry.COIN_GLINT.get(), GlowingSuspendedTownParticle.CoinGlintProvider::new);
        event.registerSpriteSet(ParticleRegistry.EXCELLENT.get(), LargeRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.FANTASTIC.get(), LargeRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.FIRE_POWERED_UP.get(), GlowingSuspendedTownParticle.FireProvider::new);
        event.registerSpriteSet(ParticleRegistry.GLOWING_STAR.get(), GlowingSuspendedTownParticle.GlowingProvider::new);
        event.registerSpriteSet(ParticleRegistry.GOLD_KOOPA_SHELL_SHATTER.get(), GravityParticle.BreakEntityProvider::new);
        event.registerSpriteSet(ParticleRegistry.GOLD_POLLEN.get(), PollenParticle.PollenProvider::new);
        event.registerSpriteSet(ParticleRegistry.GOOD.get(), RewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.GREAT.get(), MediumRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.GREEN_KOOPA_SHELL_SHATTER.get(), GravityParticle.BreakEntityProvider::new);
        event.registerSpriteSet(ParticleRegistry.ICE_CUBE_SHATTER.get(), GravityParticle.BreakEntityProvider::new);
        event.registerSpriteSet(ParticleRegistry.ICE_POWERED_UP.get(), GlowingSuspendedTownParticle.FireProvider::new);
        event.registerSpriteSet(ParticleRegistry.ICE_STAR.get(), GlowingSuspendedTownParticle.GlowingProvider::new);
        event.registerSpriteSet(ParticleRegistry.INCREDIBLE.get(), LargeRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.ONE_UP.get(), RewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.POWERED_UP.get(), GlowingSuspendedTownParticle.FireProvider::new);
        event.registerSpriteSet(ParticleRegistry.RAINBOW_GLINT.get(), GlowingSuspendedTownParticle.CoinGlintProvider::new);
        event.registerSpriteSet(ParticleRegistry.RED_KOOPA_SHELL_SHATTER.get(), GravityParticle.BreakEntityProvider::new);
        event.registerSpriteSet(ParticleRegistry.RED_STAR.get(), GlowingSuspendedTownParticle.CoinGlintProvider::new);
        event.registerSpriteSet(ParticleRegistry.SUPER.get(), MediumRewardParticle::new);
        event.registerSpriteSet(ParticleRegistry.SUSPENDED_FIRE.get(), GlowingSuspendedTownParticle.FireProvider::new);
        event.registerSpriteSet(ParticleRegistry.WONDERFUL.get(), LargeRewardParticle::new);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(
                BuiltInRegistries.ENTITY_TYPE.stream()
                        .filter(DefaultAttributes::hasSupplier)
                        .map(entityType -> (EntityType<LivingEntity>) entityType)
                        .collect(Collectors.toList()));
        entityTypes.forEach((entityType -> {
//            addLayerIfApplicable(entityType, event);
            EntityRenderer<?> renderer = event.getRenderer(entityType);
            if (renderer instanceof LivingEntityRenderer<?, ?> entityRenderer) {
                entityRenderer.addLayer(new SuperStarLayer(entityRenderer));
            } else if (renderer instanceof GeoEntityRenderer<?> geoRendererRaw) {
                addGeoSuperStarLayer(geoRendererRaw);
            } else if (entityType != EntityType.PLAYER) {
                Marioverse.LOGGER.warn("Could not apply super star layer to {}, " +
                                "has custom renderer that is not LivingEntityRenderer nor GeoEntityRenderer.",
                        BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
            }
        }));

        for (PlayerSkin.Model skinType : event.getSkins()){
            var skinRenderer = event.getSkin(skinType);
            if (skinRenderer instanceof LivingEntityRenderer<?, ?> entityRenderer)
                entityRenderer.addLayer(new SuperStarLayer(entityRenderer));
        }
    }

    private static void addLayerIfApplicable(EntityType<? extends LivingEntity> entityType, EntityRenderersEvent.AddLayers event) {
        EntityRenderer<?> renderer = event.getRenderer(entityType);
        if (renderer instanceof LivingEntityRenderer<?, ?> entityRenderer) {
            entityRenderer.addLayer(new SuperStarLayer(entityRenderer));
        } else if (renderer instanceof GeoEntityRenderer<?> geoRendererRaw) {
            addGeoSuperStarLayer(geoRendererRaw);
        } else if (entityType != EntityType.PLAYER) {
            Marioverse.LOGGER.warn("Could not apply super star layer to {}, " +
                            "has custom renderer that is not LivingEntityRenderer nor GeoEntityRenderer.",
                    BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity & GeoAnimatable> void addGeoSuperStarLayer(GeoEntityRenderer<?> geoRendererRaw) {
        GeoEntityRenderer<T> geoRenderer = (GeoEntityRenderer<T>) geoRendererRaw;
        geoRenderer.addRenderLayer(new SuperStarGeoLayer<>(geoRenderer));
    }

    public static void addPackFinder(final AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            ResourceLocation packLocation = ResourceLocation
                    .fromNamespaceAndPath(Marioverse.MOD_ID, "resourcepacks/marioverse/truly_invisible");
            Component packNameDisplay = Component.translatable("resource_pack.marioverse.truly_invisible");

            event.addPackFinders(packLocation, PackType.CLIENT_RESOURCES, packNameDisplay,
                    PackSource.BUILT_IN, false, Pack.Position.TOP);

            packLocation = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "resourcepacks/marioverse/classic_coins");
            packNameDisplay = Component.translatable("resource_pack.marioverse.classic_coins");
            event.addPackFinders(packLocation, PackType.CLIENT_RESOURCES, packNameDisplay,
                    PackSource.BUILT_IN, false, Pack.Position.TOP);

            packLocation = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "resourcepacks/marioverse/classic_pipes");
            packNameDisplay = Component.translatable("resource_pack.marioverse.classic_pipes");
            event.addPackFinders(packLocation, PackType.CLIENT_RESOURCES, packNameDisplay,
                    PackSource.BUILT_IN, false, Pack.Position.TOP);

            if (ModList.get().isLoaded("fusion")) {
                packLocation = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "resourcepacks/marioverse/classic_pipes_fusion");
                packNameDisplay = Component.translatable("resource_pack.marioverse.classic_pipes_fusion");
                event.addPackFinders(packLocation, PackType.CLIENT_RESOURCES, packNameDisplay,
                        PackSource.BUILT_IN, false, Pack.Position.TOP);
            }

            ResourceLocation dynamicPackLocation = ResourceLocation
                    .fromNamespaceAndPath(Marioverse.MOD_ID, "resourcepacks/marioverse/dynamic_client_resources");
            Component  dynamicPackNameDisplay = Component.translatable("resource_pack.marioverse.dynamic_client_resources");
            PackLocationInfo packLocationInfo = new PackLocationInfo(Marioverse.MOD_ID + ":dynamic_client_resources",
                    dynamicPackNameDisplay, PackSource.BUILT_IN, Optional.of(new KnownPack(Marioverse.MOD_ID, "mod/" + dynamicPackLocation, "22")));

            event.addRepositorySource(consumer -> consumer.accept(Pack.readMetaAndCreate(packLocationInfo,
                            new Pack.ResourcesSupplier() {
                                @Override
                                public PackResources openPrimary(PackLocationInfo info) {
                                    return new DynamicClientResources(info);
                                }

                                @Override
                                public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                                    return new DynamicClientResources(info);
                                }
                            },
                            PackType.CLIENT_RESOURCES,
                            new PackSelectionConfig(true, Pack.Position.TOP, false)
                    )
            ));
        }
    }
}
