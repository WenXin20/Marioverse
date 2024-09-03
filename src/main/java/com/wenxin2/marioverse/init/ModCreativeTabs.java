package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Marioverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Marioverse.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WARP_PIPES_TAB = TABS.register("marioverse_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.marioverse"))
            .icon(() -> new ItemStack(BlockRegistry.WARP_PIPES.get(DyeColor.GREEN).get())).build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == WARP_PIPES_TAB.getKey()) {
            add(event, ItemRegistry.PIPE_WRENCH.get());

            add(event, BlockRegistry.CLEAR_WARP_PIPE.get());

            for (DeferredHolder<Block, Block> pipe : BlockRegistry.WARP_PIPES.values()) {
                add(event, pipe.get());
            }
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            addAfter(event, Items.FISHING_ROD, ItemRegistry.PIPE_WRENCH.get());
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            addBefore(event, Items.SHIELD, ItemRegistry.PIPE_WRENCH.get());
        }

        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            addAfter(event, Items.REDSTONE_LAMP, BlockRegistry.CLEAR_WARP_PIPE.get());
            addAfter(event, Items.REDSTONE_LAMP, BlockRegistry.WARP_PIPES.get(DyeColor.GREEN).get());
        }

        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            add(event, BlockRegistry.CLEAR_WARP_PIPE.get());

            for (DeferredHolder<Block, Block> pipe : BlockRegistry.WARP_PIPES.values()) {
                add(event, pipe.get());
            }
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            addAfter(event, Items.RESPAWN_ANCHOR, BlockRegistry.CLEAR_WARP_PIPE.get());

            for (DeferredHolder<Block, Block> pipe : BlockRegistry.WARP_PIPES.values()) {
                addAfter(event, BlockRegistry.CLEAR_WARP_PIPE.get(), pipe.get());
            }

//            addAfter(event, ModRegistry.CLEAR_WARP_PIPE.get(), ModRegistry.WARP_PIPES.get(DyeColor.WHITE).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.WHITE).get(), ModRegistry.WARP_PIPES.get(DyeColor.LIGHT_GRAY).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.LIGHT_GRAY).get(), ModRegistry.WARP_PIPES.get(DyeColor.GRAY).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.GRAY).get(), ModRegistry.WARP_PIPES.get(DyeColor.BLACK).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.BLACK).get(), ModRegistry.WARP_PIPES.get(DyeColor.BROWN).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.BROWN).get(), ModRegistry.WARP_PIPES.get(DyeColor.RED).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.RED).get(), ModRegistry.WARP_PIPES.get(DyeColor.ORANGE).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.ORANGE).get(), ModRegistry.WARP_PIPES.get(DyeColor.YELLOW).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.YELLOW).get(), ModRegistry.WARP_PIPES.get(DyeColor.LIME).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.LIME).get(),ModRegistry.WARP_PIPES.get(DyeColor.GREEN).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.GREEN).get(), ModRegistry.WARP_PIPES.get(DyeColor.CYAN).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.CYAN).get(), ModRegistry.WARP_PIPES.get(DyeColor.LIGHT_BLUE).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.LIGHT_BLUE).get(), ModRegistry.WARP_PIPES.get(DyeColor.BLUE).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.BLUE).get(), ModRegistry.WARP_PIPES.get(DyeColor.PURPLE).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.PURPLE).get(), ModRegistry.WARP_PIPES.get(DyeColor.MAGENTA).get());
//            addAfter(event, ModRegistry.WARP_PIPES.get(DyeColor.MAGENTA).get(), ModRegistry.WARP_PIPES.get(DyeColor.PINK).get());
        }
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemLike item)
    {
        ItemStack stack = new ItemStack(item);
        add(event, stack);
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemStack stack)
    {
        if (stack.isEmpty())
        {
            System.out.println("Warning, attempting to register an empty stack to tab!");
            return;
        }
        event.accept(stack);
    }

    public static void addAfter(BuildCreativeModeTabContentsEvent event, ItemLike afterItem, ItemLike item) {
        event.insertAfter(new ItemStack(afterItem), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public static void addBefore(BuildCreativeModeTabContentsEvent event, ItemLike beforeItem, ItemLike item) {
        event.insertBefore(new ItemStack(beforeItem), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
