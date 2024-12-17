package com.wenxin2.marioverse;

import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Marioverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MarioverseCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Marioverse.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MARIOVERSE_TAB = TABS.register("marioverse_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.marioverse"))
            .icon(() -> new ItemStack(ItemRegistry.MUSHROOM.get())).build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == MARIOVERSE_TAB.getKey()) {
            add(event, ItemRegistry.MUSHROOM.get());
            add(event, ItemRegistry.ONE_UP_MUSHROOM.get());
            add(event, ItemRegistry.FIRE_FLOWER.get());
            add(event, ItemRegistry.SUPER_STAR.get());

            add(event, ItemRegistry.FIRE_HAT.get());
            add(event, ItemRegistry.FIRE_SHIRT.get());
            add(event, ItemRegistry.FIRE_OVERALLS.get());
            add(event, ItemRegistry.FIRE_SHOES.get());

            add(event, BlockRegistry.COIN.get());
            add(event, ItemRegistry.WARP_DISRUPTOR.get());
            add(event, ItemRegistry.PIPE_WRENCH.get());
            add(event, BlockRegistry.CLASSIC_GOAL_POLE);
            addDyedBlocks(event, BlockRegistry.CLASSIC_GOAL_POLE, BlockRegistry.GOAL_POLES, true, true);

            add(event, ItemRegistry.BOWSER_BANNER_PATTERN.get());
            add(event, ItemRegistry.BOWSER_POTTERY_SHERD.get());

            add(event, BlockRegistry.FUNGAL_QUESTION_BLOCK.get());
            add(event, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.get());
            add(event, BlockRegistry.STORAGE_FUNGAL_BRICKS.get());
            add(event, BlockRegistry.FUNGAL_BRICKS.get());
            add(event, BlockRegistry.FUNGAL_BRICK_STAIRS.get());
            add(event, BlockRegistry.FUNGAL_BRICK_SLAB.get());
            add(event, BlockRegistry.FUNGAL_BRICK_PEDESTAL.get());

            add(event, BlockRegistry.QUESTION_BRICKS.get());
            add(event, BlockRegistry.INVISIBLE_QUESTION_BRICKS.get());
            add(event, BlockRegistry.STORAGE_BRICKS.get());
            add(event, BlockRegistry.SMASHABLE_BRICKS.get());
            add(event, BlockRegistry.BRICK_PEDESTAL.get());

            add(event, BlockRegistry.NETHER_QUESTION_BRICKS.get());
            add(event, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS.get());
            add(event, BlockRegistry.STORAGE_NETHER_BRICKS.get());
            add(event, BlockRegistry.SMASHABLE_NETHER_BRICKS.get());
            add(event, BlockRegistry.NETHER_BRICK_PEDESTAL.get());

            add(event, BlockRegistry.RED_NETHER_QUESTION_BRICKS.get());
            add(event, BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS.get());
            add(event, BlockRegistry.STORAGE_RED_NETHER_BRICKS.get());
            add(event, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS.get());
            add(event, BlockRegistry.RED_NETHER_BRICK_PEDESTAL.get());

            add(event, BlockRegistry.END_STONE_QUESTION_BRICKS.get());
            add(event, BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS.get());
            add(event, BlockRegistry.STORAGE_END_STONE_BRICKS.get());
            add(event, BlockRegistry.SMASHABLE_END_STONE_BRICKS.get());
            add(event, BlockRegistry.END_STONE_BRICK_PEDESTAL.get());

            add(event, BlockRegistry.PURPUR_QUESTION_BLOCK.get());
            add(event, BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK.get());
            add(event, BlockRegistry.STORAGE_PURPUR_BLOCK.get());
            add(event, BlockRegistry.SMASHABLE_PURPUR_BLOCK.get());
            add(event, BlockRegistry.PURPUR_BLOCK_PEDESTAL.get());

            add(event, BlockRegistry.CLEAR_WARP_PIPE.get());
            addDyedBlocks(event, BlockRegistry.CLEAR_WARP_PIPE.get(), BlockRegistry.WARP_PIPES, true, true);

            add(event, ItemRegistry.MINI_GOOMBA_SPAWN_EGG.get());
            add(event, ItemRegistry.GOOMBA_SPAWN_EGG.get());
            add(event, ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG.get());
            add(event, ItemRegistry.MEGA_GOOMBA_SPAWN_EGG.get());
            add(event, ItemRegistry.FIRE_GOOMBA_SPAWN_EGG.get());
        }

        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            addAfter(event, Blocks.PINK_SHULKER_BOX, BlockRegistry.CLEAR_WARP_PIPE.get());
            addDyedBlocks(event, BlockRegistry.CLEAR_WARP_PIPE.get(), BlockRegistry.WARP_PIPES, true, true);

            addAfter(event, Blocks.PINK_BANNER, BlockRegistry.CLASSIC_GOAL_POLE.get());
            addDyedBlocks(event, BlockRegistry.CLASSIC_GOAL_POLE.get(), BlockRegistry.GOAL_POLES, true, true);
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            addBefore(event, Items.SHIELD, ItemRegistry.PIPE_WRENCH.get());
        }

        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            addAfter(event, Items.DECORATED_POT, BlockRegistry.FUNGAL_QUESTION_BLOCK.get());
            addAfter(event, BlockRegistry.FUNGAL_QUESTION_BLOCK.get(), BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.get());
            addAfter(event, Items.REDSTONE_LAMP, BlockRegistry.CLEAR_WARP_PIPE.get());
            addAfter(event, BlockRegistry.CLEAR_WARP_PIPE.get(), BlockRegistry.WARP_PIPES.get(DyeColor.GREEN).get());
            addAfter(event, BlockRegistry.WARP_PIPES.get(DyeColor.GREEN).get(), BlockRegistry.GOAL_POLES.get(DyeColor.RED).get());
            addAfter(event, BlockRegistry.GOAL_POLES.get(DyeColor.RED).get(), BlockRegistry.BRICK_PEDESTAL.get());
        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            addAfter(event, Items.FISHING_ROD, ItemRegistry.PIPE_WRENCH.get());
            addBefore(event, ItemRegistry.PIPE_WRENCH.get(), ItemRegistry.WARP_DISRUPTOR.get());
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            addAfter(event, Items.GUSTER_BANNER_PATTERN, ItemRegistry.BOWSER_BANNER_PATTERN.get());
            addAfter(event, Items.SNORT_POTTERY_SHERD, ItemRegistry.BOWSER_POTTERY_SHERD.get());
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            addAfter(event, Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, ItemRegistry.MINI_GOOMBA_SPAWN_EGG.get());
            addAfter(event, ItemRegistry.MINI_GOOMBA_SPAWN_EGG.get(), ItemRegistry.GOOMBA_SPAWN_EGG.get());
            addAfter(event, ItemRegistry.GOOMBA_SPAWN_EGG.get(), ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG.get());
            addAfter(event, ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG.get(), ItemRegistry.MEGA_GOOMBA_SPAWN_EGG.get());
            addAfter(event, ItemRegistry.MEGA_GOOMBA_SPAWN_EGG.get(), ItemRegistry.FIRE_GOOMBA_SPAWN_EGG.get());
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            addAfter(event, Blocks.BRICK_WALL, BlockRegistry.BRICK_PEDESTAL.get());
            addAfter(event, BlockRegistry.BRICK_PEDESTAL.get(), BlockRegistry.SMASHABLE_BRICKS.get());

            addAfter(event, BlockRegistry.SMASHABLE_BRICKS.get(), BlockRegistry.FUNGAL_BRICKS.get());
            addAfter(event, BlockRegistry.FUNGAL_BRICKS.get(), BlockRegistry.FUNGAL_BRICK_STAIRS.get());
            addAfter(event, BlockRegistry.FUNGAL_BRICK_STAIRS.get(), BlockRegistry.FUNGAL_BRICK_SLAB.get());
            addAfter(event, BlockRegistry.FUNGAL_BRICK_SLAB.get(), BlockRegistry.FUNGAL_BRICK_PEDESTAL.get());

            addAfter(event, Blocks.NETHER_BRICK_FENCE, BlockRegistry.NETHER_BRICK_PEDESTAL.get());
            addAfter(event, BlockRegistry.NETHER_BRICK_PEDESTAL.get(), BlockRegistry.SMASHABLE_NETHER_BRICKS.get());

            addAfter(event, Blocks.RED_NETHER_BRICK_WALL, BlockRegistry.RED_NETHER_BRICK_PEDESTAL.get());
            addAfter(event, BlockRegistry.RED_NETHER_BRICK_PEDESTAL.get(), BlockRegistry.SMASHABLE_RED_NETHER_BRICKS.get());

            addAfter(event, Blocks.END_STONE_BRICK_WALL, BlockRegistry.END_STONE_BRICK_PEDESTAL.get());
            addAfter(event, BlockRegistry.END_STONE_BRICK_PEDESTAL.get(), BlockRegistry.SMASHABLE_END_STONE_BRICKS.get());

            addAfter(event, Blocks.PURPUR_SLAB, BlockRegistry.PURPUR_BLOCK_PEDESTAL.get());
            addAfter(event, BlockRegistry.PURPUR_BLOCK_PEDESTAL.get(), BlockRegistry.SMASHABLE_PURPUR_BLOCK.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            addBefore(event, Items.LIGHTNING_ROD, BlockRegistry.FUNGAL_QUESTION_BLOCK.get());
            addAfter(event, BlockRegistry.FUNGAL_QUESTION_BLOCK.get(), BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.get());
            addAfter(event, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.get(), BlockRegistry.STORAGE_FUNGAL_BRICKS.get());
            addAfter(event, BlockRegistry.STORAGE_FUNGAL_BRICKS.get(), BlockRegistry.FUNGAL_BRICKS.get());
            addAfter(event, BlockRegistry.FUNGAL_BRICKS.get(), BlockRegistry.FUNGAL_BRICK_PEDESTAL.get());

            addAfter(event, BlockRegistry.FUNGAL_BRICKS.get(), BlockRegistry.QUESTION_BRICKS.get());
            addAfter(event, BlockRegistry.QUESTION_BRICKS.get(), BlockRegistry.INVISIBLE_QUESTION_BRICKS.get());
            addAfter(event, BlockRegistry.INVISIBLE_QUESTION_BRICKS.get(), BlockRegistry.STORAGE_BRICKS.get());
            addAfter(event, BlockRegistry.STORAGE_BRICKS.get(), BlockRegistry.SMASHABLE_BRICKS.get());
            addAfter(event, BlockRegistry.SMASHABLE_BRICKS.get(), BlockRegistry.BRICK_PEDESTAL.get());

            addAfter(event, BlockRegistry.SMASHABLE_BRICKS.get(), BlockRegistry.NETHER_QUESTION_BRICKS.get());
            addAfter(event, BlockRegistry.NETHER_QUESTION_BRICKS.get(), BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS.get());
            addAfter(event, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS.get(), BlockRegistry.STORAGE_NETHER_BRICKS.get());
            addAfter(event, BlockRegistry.STORAGE_NETHER_BRICKS.get(), BlockRegistry.SMASHABLE_NETHER_BRICKS.get());
            addAfter(event, BlockRegistry.SMASHABLE_NETHER_BRICKS.get(), BlockRegistry.NETHER_BRICK_PEDESTAL.get());

            addAfter(event, BlockRegistry.SMASHABLE_NETHER_BRICKS.get(), BlockRegistry.RED_NETHER_QUESTION_BRICKS.get());
            addAfter(event, BlockRegistry.RED_NETHER_QUESTION_BRICKS.get(), BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS.get());
            addAfter(event, BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS.get(), BlockRegistry.STORAGE_RED_NETHER_BRICKS.get());
            addAfter(event, BlockRegistry.STORAGE_RED_NETHER_BRICKS.get(), BlockRegistry.SMASHABLE_RED_NETHER_BRICKS.get());
            addAfter(event, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS.get(), BlockRegistry.RED_NETHER_BRICK_PEDESTAL.get());

            addAfter(event, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS.get(), BlockRegistry.END_STONE_QUESTION_BRICKS.get());
            addAfter(event, BlockRegistry.END_STONE_QUESTION_BRICKS.get(), BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS.get());
            addAfter(event, BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS.get(), BlockRegistry.STORAGE_END_STONE_BRICKS.get());
            addAfter(event, BlockRegistry.STORAGE_END_STONE_BRICKS.get(), BlockRegistry.SMASHABLE_END_STONE_BRICKS.get());
            addAfter(event, BlockRegistry.SMASHABLE_END_STONE_BRICKS.get(), BlockRegistry.END_STONE_BRICK_PEDESTAL.get());

            addAfter(event, BlockRegistry.SMASHABLE_END_STONE_BRICKS.get(), BlockRegistry.PURPUR_QUESTION_BLOCK.get());
            addAfter(event, BlockRegistry.PURPUR_QUESTION_BLOCK.get(), BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK.get());
            addAfter(event, BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK.get(), BlockRegistry.STORAGE_PURPUR_BLOCK.get());
            addAfter(event, BlockRegistry.STORAGE_PURPUR_BLOCK.get(), BlockRegistry.SMASHABLE_PURPUR_BLOCK.get());
            addAfter(event, BlockRegistry.SMASHABLE_PURPUR_BLOCK.get(), BlockRegistry.PURPUR_BLOCK_PEDESTAL.get());

            addAfter(event, Items.RESPAWN_ANCHOR, BlockRegistry.CLEAR_WARP_PIPE.get());
            addDyedBlocks(event, BlockRegistry.CLEAR_WARP_PIPE.get(), BlockRegistry.WARP_PIPES, true, true);

            addDyedBlocks(event, Blocks.SKELETON_SKULL, BlockRegistry.GOAL_POLES, false, false);
            addBefore(event, BlockRegistry.GOAL_POLES.get(DyeColor.WHITE), BlockRegistry.CLASSIC_GOAL_POLE.get());
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

    private static void addDyedBlocks(BuildCreativeModeTabContentsEvent event, ItemLike existingItem,
                                      EnumMap<DyeColor, DeferredBlock<Block>> dyedBlock, boolean isReversed, boolean addAfter) {
        List<DyeColor> rainbowOrder = Arrays.asList(DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK,
                DyeColor.BROWN, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.GREEN, DyeColor.CYAN,
                DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK);
        List<DeferredHolder<Block, Block>> dyedBlocks = new ArrayList<>();
        Set<DyeColor> processedColors = new HashSet<>();

        if (isReversed)
            Collections.reverse(rainbowOrder);

        for (DyeColor color : rainbowOrder) {
            DeferredBlock<Block> coloredBlock = dyedBlock.get(color);
            if (coloredBlock != null) {
                dyedBlocks.add(coloredBlock);
                processedColors.add(color);
            }
        }

        // Track blocks not in the rainbow order
        Set<Block> additionalBlocks = new HashSet<>();
        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : dyedBlock.entrySet()) {
            DyeColor color = entry.getKey();
            if (!processedColors.contains(color)) {
                additionalBlocks.add(entry.getValue().get());
            }
        }

        Set<Block> listedBlocks = new HashSet<>();

        // Adds all rainbow blocks
        Block lastRainbowBlock = null;
        for (DeferredHolder<Block, Block> block : dyedBlocks) {
            Block coloredBlock = block.get();
            if (!listedBlocks.contains(coloredBlock)) {
                if (addAfter)
                    addAfter(event, existingItem, coloredBlock);
                else addBefore(event, existingItem, coloredBlock);

                listedBlocks.add(coloredBlock);
                lastRainbowBlock = dyedBlock.get(DyeColor.PINK).get();
            }
        }

        // Adds any additional blocks that were not in the rainbow blocks
        for (Block additionalBlock : additionalBlocks) {
            if (!listedBlocks.contains(additionalBlock)) {
                if (lastRainbowBlock != null && addAfter)
                    addAfter(event, lastRainbowBlock, additionalBlock);
                else addBefore(event, existingItem, additionalBlock);
                listedBlocks.add(additionalBlock);
            }
        }
    }
}
