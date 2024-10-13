package com.wenxin2.marioverse;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.client.renderers.ArmorRenderingExtension;
import com.wenxin2.marioverse.client.renderers.accesories.OneUpRenderer;
import com.wenxin2.marioverse.event_handlers.MarioverseEventHandlers;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.MenuRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.items.data_components.LinkerDataComponents;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Marioverse.MOD_ID)
public class Marioverse
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "marioverse";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold blocks/items which will all be registered under the "marioverse" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Marioverse.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Marioverse.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Marioverse.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Marioverse.MOD_ID);

    // Bus for Forge Events
    public static final IEventBus FORGE_BUS = NeoForge.EVENT_BUS;

    public Marioverse(IEventBus bus, Dist dist, ModContainer container)
    {
        // Register the Deferred Register to the mod event bus so blocks/items get registered
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        PARTICLES.register(bus);
        MENUS.register(bus);
        SOUNDS.register(bus);
        LOOT_CODECS.register(bus);
        LinkerDataComponents.COMPONENTS.register(bus);
        MarioverseCreativeTabs.TABS.register(bus);

        BlockRegistry.init();
        BlockEntityRegistry.init();
        ItemRegistry.init();
        EntityRegistry.init();
        MenuRegistry.init();
        ParticleRegistry.init();
        SoundRegistry.init();
        ConfigRegistry.register(container);

        if (dist.isClient()) {
            ConfigRegistry.registerClient(container);
        }

        // PipeBubblesSoundHandler.init();

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onJoinWorld);
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onPlayerRightClick);
        bus.addListener(FMLClientSetupEvent.class, (evt) -> this.clientSetup(evt, bus));
    }

    private void clientSetup(final FMLClientSetupEvent event, final IEventBus eventBus) {
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.ONE_UP_MUSHROOM.get(), OneUpRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.FIRE_HAT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.FIRE_SHIRT.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.FIRE_OVERALLS.get(), () -> ArmorRenderingExtension.RENDERER);
        AccessoriesRendererRegistry.registerRenderer(ItemRegistry.FIRE_SHOES.get(), () -> ArmorRenderingExtension.RENDERER);
    }
}
