package com.wenxin2.marioverse;

import com.mojang.logging.LogUtils;
import com.wenxin2.marioverse.event_handlers.MarioverseEventHandlers;
import com.wenxin2.marioverse.init.ClientSetupHandler;
import com.wenxin2.marioverse.init.Config;
import com.wenxin2.marioverse.init.ModCreativeTabs;
import com.wenxin2.marioverse.init.ModRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.items.data_components.LinkerDataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Marioverse.MODID)
public class Marioverse
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "marioverse";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold blocks/items which will all be registered under the "marioverse" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Marioverse.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Marioverse.MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Marioverse.MODID);

    // Bus for Forge Events
    public static final IEventBus FORGE_BUS = NeoForge.EVENT_BUS;

    public Marioverse(IEventBus bus, Dist dist, ModContainer container)
    {
        // Register the Deferred Register to the mod event bus so blocks/items get registered
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENUS.register(bus);
        SOUNDS.register(bus);
        LinkerDataComponents.COMPONENTS.register(bus);
        ModCreativeTabs.TABS.register(bus);

        ModRegistry.init();
        SoundRegistry.init();
        Config.register(container);

        if (dist.isClient()) {
            bus.addListener(ClientSetupHandler::registerBlockEntityRenderers);
            Config.registerClient(container);
        }

//        WarpEventHandlers.register();
        // PipeBubblesSoundHandler.init();

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onJoinWorld);
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onEntityDamaged);
        NeoForge.EVENT_BUS.addListener(MarioverseEventHandlers::onPlayerRightClick);
    }
}
