package com.wenxin2.marioverse;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.event_handlers.MarioverseEventHandlers;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import com.wenxin2.marioverse.integration.StoneZoneCompat;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.GameEventRegistry;
import com.wenxin2.marioverse.registries.MenuRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

@Mod(Marioverse.MOD_ID)
public class Marioverse {
    public static final String MOD_ID = "marioverse";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<GameEvent> GAME_EVENTS = DeferredRegister.create(Registries.GAME_EVENT, Marioverse.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Marioverse.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Marioverse.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Marioverse.MOD_ID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Marioverse.MOD_ID);

    public Marioverse(IEventBus bus, Dist dist, ModContainer container) {
        COMPONENTS.register(bus);
        GAME_EVENTS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        ATTRIBUTES.register(bus);
        PARTICLES.register(bus);
        MENUS.register(bus);
        SOUNDS.register(bus);
        GLOBAL_LOOT_MODIFIERS.register(bus);
        MarioverseCreativeTabs.TABS.register(bus);

        BlockRegistry.init();
        BlockEntityRegistry.init();
        ItemRegistry.init();
        EntityRegistry.init();
        GameEventRegistry.init();
        MenuRegistry.init();
        ParticleRegistry.init();
        SoundRegistry.init();
        DataComponentRegistry.init();
        AttributesRegistry.init();
        ConfigRegistry.register(container);

        Marioverse.stoneZoneModule();

        if (dist.isClient()) {
            ConfigRegistry.registerClient(container);
            bus.addListener(MarioverseClient::clientSetup);
            bus.addListener(MarioverseClient::addPackFinder);
        }

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onJoinWorld);
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onPlayerRightClick);
        bus.addListener(this::commonSetup);
        bus.addListener(RegistryEventHandlers::gatherData);
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

    public static GameRules.Key<GameRules.BooleanValue> ALL_MOBS_CAN_STOMP;
    public static GameRules.Key<GameRules.BooleanValue> DAMAGE_SHRINKS_ALL_MOBS;
    public static GameRules.Key<GameRules.BooleanValue> DAMAGE_SHRINKS_PLAYERS;
    public static GameRules.Key<GameRules.BooleanValue> STOMP_ALL_MOBS;

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ALL_MOBS_CAN_STOMP = GameRules.register("marioverse:all_mobs_can_stomp",
                    GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
            DAMAGE_SHRINKS_ALL_MOBS = GameRules.register("marioverse:damage_shrinks_all_mobs",
                    GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
            DAMAGE_SHRINKS_PLAYERS = GameRules.register("marioverse:damage_shrinks_players",
                    GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
            STOMP_ALL_MOBS = GameRules.register("marioverse:stomp_all_mobs",
                    GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
        });
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.tryBuild(MOD_ID, id);
    }
}
