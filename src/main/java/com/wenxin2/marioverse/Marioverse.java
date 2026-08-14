package com.wenxin2.marioverse;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.event_handlers.MarioverseEventHandlers;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import com.wenxin2.marioverse.integration.SableCompat;
import com.wenxin2.marioverse.integration.StoneZoneCompat;
import com.wenxin2.marioverse.integration.WoodGoodCompat;
import com.wenxin2.marioverse.loot.AddItemsModifier;
import com.wenxin2.marioverse.power_up.PowerUpType;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.FeatureRegistry;
import com.wenxin2.marioverse.registries.GameEventRegistry;
import com.wenxin2.marioverse.registries.MenuRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.PowerUpTypeRegistry;
import com.wenxin2.marioverse.registries.RecipeSerializerRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.TreeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.slf4j.Logger;

@Mod(Marioverse.MOD_ID)
public class Marioverse {
    public static final String MOD_ID = "marioverse";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Marioverse.MOD_ID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Marioverse.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Marioverse.MOD_ID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registries.CONFIGURED_FEATURE, Marioverse.MOD_ID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registries.PLACED_FEATURE, Marioverse.MOD_ID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegister.create(BuiltInRegistries.FOLIAGE_PLACER_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(BuiltInRegistries.TRUNK_PLACER_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<GameEvent> GAME_EVENTS = DeferredRegister.create(Registries.GAME_EVENT, Marioverse.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Marioverse.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Marioverse.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Marioverse.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Marioverse.MOD_ID);

    public static final ResourceKey<Registry<PowerUpType>> POWER_UP_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "power_up_type"));

    public static final Registry<PowerUpType> POWER_UP_REGISTRY = new RegistryBuilder<>(POWER_UP_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<PowerUpType> POWER_UP_TYPES = DeferredRegister.create(POWER_UP_REGISTRY_KEY, Marioverse.MOD_ID);

    public Marioverse(IEventBus bus, Dist dist, ModContainer container) {
        BlockRegistry.registerAliases();
        ItemRegistry.registerAliases();
        ATTACHMENT_TYPES.register(bus);
        COMPONENTS.register(bus);
        GAME_EVENTS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        CONFIGURED_FEATURES.register(bus);
        FEATURES.register(bus);
        PLACED_FEATURES.register(bus);
        TRUNK_PLACERS.register(bus);
        FOLIAGE_PLACERS.register(bus);
        ATTRIBUTES.register(bus);
        PARTICLES.register(bus);
        MENUS.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
        SOUNDS.register(bus);
        GLOBAL_LOOT_MODIFIERS.register(bus);
        POWER_UP_TYPES.register(bus);
        MarioverseCreativeTabs.TABS.register(bus);

        AttributesRegistry.init();
        BlockEntityRegistry.init();
        BlockRegistry.init();
        ConfigRegistry.register(container);
        DataAttachmentRegistry.init();
        DataComponentRegistry.init();
        EntityRegistry.init();
        FeatureRegistry.init();
        TreeRegistry.init();
        AddItemsModifier.init();
        GameEventRegistry.init();
        ItemRegistry.init();
        MenuRegistry.init();
        ParticleRegistry.init();
        RecipeSerializerRegistry.init();
        SoundRegistry.init();

        Marioverse.everyCompatModule();
        Marioverse.stoneZoneModule();
        Marioverse.sableModule();

        if (dist.isClient()) {
            ConfigRegistry.registerClient(container);
            bus.addListener(MarioverseClient::clientSetup);
            bus.addListener(MarioverseClient::addPackFinder);
        }

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onJoinWorld);
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onRightClickBlock);
        bus.addListener(this::commonSetup);
        bus.addListener(RegistryEventHandlers::gatherData);
        bus.addListener(RegistryEventHandlers::addPackFinder);
        bus.addListener(PowerUpTypeRegistry::registerRegistry);
//        bus.addListener(TreesDataGen::gatherData);
    }

    private static void everyCompatModule() {
        try {
            if (ModList.get().isLoaded("everycomp"))
                WoodGoodCompat.init();
            else LOGGER.info("Every Compat module is not loaded");
        } catch (Exception e) {
            LOGGER.error("Failed to start Wood Good module", e);
        }
    }

    private static void stoneZoneModule() {
        try {
            if (ModList.get().isLoaded("stonezone"))
                StoneZoneCompat.init();
            else LOGGER.info("Stone Zone module is not loaded");
        } catch (Exception e) {
            LOGGER.error("Failed to start Stone Zone module", e);
        }
    }

    private static void sableModule() {
        try {
            if (ModList.get().isLoaded("sable"))
                SableCompat.init();
            else LOGGER.info("Sable compat not loaded");
        } catch (Throwable e) {
            LOGGER.error("Failed to load Sable compat", e);
        }
    }

    public static GameRules.Key<GameRules.BooleanValue> ALL_MOBS_CAN_STOMP;
    public static GameRules.Key<GameRules.BooleanValue> DAMAGE_SHRINKS_ALL_MOBS;
    public static GameRules.Key<GameRules.BooleanValue> DAMAGE_SHRINKS_PLAYERS;
    public static GameRules.Key<GameRules.BooleanValue> STOMP_ALL_MOBS;

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(BlockRegistry::registerFlowerPots);
        event.enqueueWork(() -> {
            ALL_MOBS_CAN_STOMP = GameRules.register("marioverse:all_mobs_can_stomp",
                    GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
            DAMAGE_SHRINKS_ALL_MOBS = GameRules.register("marioverse:damage_shrinks_all_mobs",
                    GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
            DAMAGE_SHRINKS_PLAYERS = GameRules.register("marioverse:damage_shrinks_players",
                    GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
            STOMP_ALL_MOBS = GameRules.register("marioverse:stomp_all_mobs",
                    GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
        });
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.tryBuild(MOD_ID, id);
    }
}
