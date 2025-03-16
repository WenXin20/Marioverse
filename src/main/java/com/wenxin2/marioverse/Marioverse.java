package com.wenxin2.marioverse;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.event_handlers.MarioverseEventHandlers;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import com.wenxin2.marioverse.init.AttributesRegistry;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.MenuRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.DataComponentRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

@Mod(Marioverse.MOD_ID)
public class Marioverse {
    public static final String MOD_ID = "marioverse";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<GameEvent> GAME_EVENTS = DeferredRegister.create(Registries.GAME_EVENT, Marioverse.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Marioverse.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Marioverse.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Marioverse.MOD_ID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Marioverse.MOD_ID);

    public Marioverse(IEventBus bus, Dist dist, ModContainer container) {
        COMPONENTS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        ATTRIBUTES.register(bus);
        PARTICLES.register(bus);
        MENUS.register(bus);
        SOUNDS.register(bus);
        LOOT_CODECS.register(bus);
        MarioverseCreativeTabs.TABS.register(bus);

        BlockRegistry.init();
        BlockEntityRegistry.init();
        ItemRegistry.init();
        EntityRegistry.init();
        MenuRegistry.init();
        ParticleRegistry.init();
        SoundRegistry.init();
        DataComponentRegistry.init();
        AttributesRegistry.init();
        ConfigRegistry.register(container);

        if (dist.isClient()) {
            ConfigRegistry.registerClient(container);
        }

        // PipeBubblesSoundHandler.init();

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onJoinWorld);
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onPlayerRightClick);
        bus.addListener(RegistryEventHandlers::gatherData);
        bus.addListener(MarioverseClient::clientSetup);
        bus.addListener(MarioverseClient::addPackFinder);
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.tryBuild(MOD_ID, id);
    }
}
