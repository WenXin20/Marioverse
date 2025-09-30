package com.wenxin2.marioverse;

import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
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

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class MarioverseCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Marioverse.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MARIOVERSE_BLOCKS_TAB = TABS.register("marioverse_blocks_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("creative_tab.marioverse_blocks"))
            .icon(() -> new ItemStack(BlockRegistry.FUNGAL_QUESTION_BLOCK.get())).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MARIOVERSE_ITEMS_TAB = TABS.register("marioverse_items_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("creative_tab.marioverse_items"))
            .icon(() -> new ItemStack(ItemRegistry.SUPER_MUSHROOM.get())).build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == MARIOVERSE_ITEMS_TAB.getKey() && !ConfigRegistry.DISABLE_MARIOVERSE_TABS.get()) {
            add(event, ItemRegistry.WRENCH);
            add(event, ItemRegistry.WARP_DISRUPTOR);

            add(event, ItemRegistry.SUPER_MUSHROOM);
            add(event, ItemRegistry.DASH_MUSHROOM);
            add(event, ItemRegistry.ONE_UP_MUSHROOM);
            add(event, ItemRegistry.FIRE_FLOWER);
            add(event, ItemRegistry.ICE_FLOWER);
            add(event, ItemRegistry.SUPER_STAR);

            add(event, ItemRegistry.GREEN_KOOPA_SHELL);
            add(event, ItemRegistry.RED_KOOPA_SHELL);
            add(event, ItemRegistry.GOLD_KOOPA_SHELL);

            add(event, ItemRegistry.PIRANHA_PLANT_POD);

            add(event, ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE);
            add(event, ItemRegistry.LUIGI_COSTUME_SMITHING_TEMPLATE);
            add(event, ItemRegistry.PEACH_COSTUME_SMITHING_TEMPLATE);
            add(event, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE);
            add(event, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE);

            add(event, ItemRegistry.MARIO_HAT);
            add(event, ItemRegistry.MARIO_SHIRT);
            add(event, ItemRegistry.MARIO_PANTS);
            add(event, ItemRegistry.MARIO_SHOES);
            add(event, ItemRegistry.MARIO_FIRE_HAT);
            add(event, ItemRegistry.MARIO_FIRE_SHIRT);
            add(event, ItemRegistry.MARIO_FIRE_PANTS);
            add(event, ItemRegistry.MARIO_FIRE_SHOES);
            add(event, ItemRegistry.MARIO_ICE_HAT);
            add(event, ItemRegistry.MARIO_ICE_SHIRT);
            add(event, ItemRegistry.MARIO_ICE_PANTS);
            add(event, ItemRegistry.MARIO_ICE_SHOES);

            add(event, ItemRegistry.LUIGI_HAT);
            add(event, ItemRegistry.LUIGI_SHIRT);
            add(event, ItemRegistry.LUIGI_PANTS);
            add(event, ItemRegistry.LUIGI_SHOES);
            add(event, ItemRegistry.LUIGI_FIRE_HAT);
            add(event, ItemRegistry.LUIGI_FIRE_SHIRT);
            add(event, ItemRegistry.LUIGI_FIRE_PANTS);
            add(event, ItemRegistry.LUIGI_FIRE_SHOES);
            add(event, ItemRegistry.LUIGI_ICE_HAT);
            add(event, ItemRegistry.LUIGI_ICE_SHIRT);
            add(event, ItemRegistry.LUIGI_ICE_PANTS);
            add(event, ItemRegistry.LUIGI_ICE_SHOES);

            add(event, ItemRegistry.PEACH_CROWN);
            add(event, ItemRegistry.PEACH_BODICE);
            add(event, ItemRegistry.PEACH_DRESS);
            add(event, ItemRegistry.PEACH_SHOES);
            add(event, ItemRegistry.PEACH_FIRE_BODICE);
            add(event, ItemRegistry.PEACH_FIRE_DRESS);
            add(event, ItemRegistry.PEACH_FIRE_SHOES);
            add(event, ItemRegistry.PEACH_ICE_BODICE);
            add(event, ItemRegistry.PEACH_ICE_DRESS);
            add(event, ItemRegistry.PEACH_ICE_SHOES);

            add(event, ItemRegistry.GREEN_KOOPA_SHOES);
            add(event, ItemRegistry.RED_KOOPA_SHOES);
            add(event, ItemRegistry.GOLDEN_KOOPA_SHOES);

            add(event, ItemRegistry.BOWSER_BANNER_PATTERN);
            add(event, ItemRegistry.PLUMBER_BANNER_PATTERN);
            add(event, ItemRegistry.BOWSER_POTTERY_SHERD);
            add(event, ItemRegistry.PLUMBER_POTTERY_SHERD);

            add(event, ItemRegistry.MINI_GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.MEGA_GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.FIRE_GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.GREEN_KOOPA_TROOPA_SPAWN_EGG);
            add(event, ItemRegistry.RED_KOOPA_TROOPA_SPAWN_EGG);
            add(event, ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG);
            add(event, ItemRegistry.PIRANHA_PLANT_SPAWN_EGG);
        }

        if (event.getTabKey() == MARIOVERSE_BLOCKS_TAB.getKey() && !ConfigRegistry.DISABLE_MARIOVERSE_TABS.get()) {
            add(event, BlockRegistry.STAR_COIN);
            add(event, BlockRegistry.COIN);
            add(event, BlockRegistry.IRON_SPIKE);
            add(event, BlockRegistry.CLASSIC_CHECKPOINT_FLAG);
            addDyedBlocks(event, BlockRegistry.CLASSIC_CHECKPOINT_FLAG, BlockRegistry.CHECKPOINT_FLAGS, true, true);
            add(event, BlockRegistry.CLASSIC_GOAL_POLE);
            addDyedBlocks(event, BlockRegistry.CLASSIC_GOAL_POLE, BlockRegistry.GOAL_POLES, true, true);

            add(event, BlockRegistry.GLOW_BLOCK);
            add(event, BlockRegistry.CLEAR_WARP_PIPE);
            addDyedBlocks(event, BlockRegistry.CLEAR_WARP_PIPE, BlockRegistry.WARP_PIPES, true, true);

            add(event, BlockRegistry.FUNGAL_QUESTION_PANEL);

            add(event, BlockRegistry.FUNGAL_QUESTION_BLOCK);
            add(event, BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK);
            add(event, BlockRegistry.AMETHYST_QUESTION_BLOCK);
            add(event, BlockRegistry.CALCITE_QUESTION_BLOCK);
            add(event, BlockRegistry.STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.MOSSY_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.DEEPSLATE_QUESTION_BRICKS);
            add(event, BlockRegistry.DEEPSLATE_QUESTION_TILES);
            add(event, BlockRegistry.TUFF_QUESTION_BRICKS);
            add(event, BlockRegistry.QUESTION_BRICKS);
            add(event, BlockRegistry.MUD_QUESTION_BRICKS);
            add(event, BlockRegistry.SANDSTONE_QUESTION_BLOCK);
            add(event, BlockRegistry.RED_SANDSTONE_QUESTION_BLOCK);
            add(event, BlockRegistry.PRISMARINE_QUESTION_BRICKS);
            add(event, BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK);
            add(event, BlockRegistry.NETHER_QUESTION_BRICKS);
            add(event, BlockRegistry.RED_NETHER_QUESTION_BRICKS);
            add(event, BlockRegistry.BLACKSTONE_QUESTION_BRICKS);
            add(event, BlockRegistry.QUARTZ_QUESTION_BRICKS);
            add(event, BlockRegistry.END_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.PURPUR_QUESTION_BLOCK);
            add(event, BlockRegistry.COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK);

            add(event, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_CALCITE_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES);
            add(event, BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_SANDSTONE_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK);

            add(event, BlockRegistry.STORAGE_FUNGAL_BRICKS);
            add(event, BlockRegistry.STORAGE_POLISHED_FUNGAL_BRICKS);
            add(event, BlockRegistry.STORAGE_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.STORAGE_POLISHED_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.STORAGE_AMETHYST_BRICKS);
            add(event, BlockRegistry.STORAGE_STONE_BRICKS);
            add(event, BlockRegistry.STORAGE_MOSSY_STONE_BRICKS);
            add(event, BlockRegistry.STORAGE_DEEPSLATE_BRICKS);
            add(event, BlockRegistry.STORAGE_DEEPSLATE_TILES);
            add(event, BlockRegistry.STORAGE_TUFF_BRICKS);
            add(event, BlockRegistry.STORAGE_BRICKS);
            add(event, BlockRegistry.STORAGE_MUD_BRICKS);
            add(event, BlockRegistry.STORAGE_SANDSTONE_BRICKS);
            add(event, BlockRegistry.STORAGE_RED_SANDSTONE_BRICKS);
            add(event, BlockRegistry.STORAGE_PRISMARINE_BRICKS);
            add(event, BlockRegistry.STORAGE_DARK_PRISMARINE);
            add(event, BlockRegistry.STORAGE_NETHER_BRICKS);
            add(event, BlockRegistry.STORAGE_RED_NETHER_BRICKS);
            add(event, BlockRegistry.STORAGE_BLACKSTONE_BRICKS);
            add(event, BlockRegistry.STORAGE_QUARTZ_BRICKS);
            add(event, BlockRegistry.STORAGE_END_STONE_BRICKS);
            add(event, BlockRegistry.STORAGE_PURPUR_BLOCK);
            add(event, BlockRegistry.STORAGE_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WAXED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER);

            add(event, BlockRegistry.SMASHABLE_STONE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_DEEPSLATE_TILES);
            add(event, BlockRegistry.SMASHABLE_TUFF_BRICKS);
            add(event, BlockRegistry.SMASHABLE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_MUD_BRICKS);
            add(event, BlockRegistry.SMASHABLE_PRISMARINE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_DARK_PRISMARINE);
            add(event, BlockRegistry.SMASHABLE_NETHER_BRICKS);
            add(event, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS);
            add(event, BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_QUARTZ_BRICKS);
            add(event, BlockRegistry.SMASHABLE_END_STONE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_PURPUR_BLOCK);
            add(event, BlockRegistry.SMASHABLE_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER);

            add(event, BlockRegistry.FUNGAL_BRICK_PEDESTAL);
            add(event, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL);
            add(event, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL);
            add(event, BlockRegistry.AMETHYST_BRICK_PEDESTAL);
            add(event, BlockRegistry.STONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL);
            add(event, BlockRegistry.DEEPSLATE_TILE_PEDESTAL);
            add(event, BlockRegistry.TUFF_BRICK_PEDESTAL);
            add(event, BlockRegistry.BRICK_PEDESTAL);
            add(event, BlockRegistry.MUD_BRICK_PEDESTAL);
            add(event, BlockRegistry.SANDSTONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.RED_SANDSTONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.PRISMARINE_BRICK_PEDESTAL);
            add(event, BlockRegistry.DARK_PRISMARINE_PEDESTAL);
            add(event, BlockRegistry.NETHER_BRICK_PEDESTAL);
            add(event, BlockRegistry.RED_NETHER_BRICK_PEDESTAL);
            add(event, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.QUARTZ_BRICK_PEDESTAL);
            add(event, BlockRegistry.END_STONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.PURPUR_BLOCK_PEDESTAL);
            add(event, BlockRegistry.CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL);

            add(event, BlockRegistry.FUNGAL_STONE);
            add(event, BlockRegistry.FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.FUNGAL_STONE_WALL);
            add(event, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE);
            add(event, BlockRegistry.FUNGAL_STONE_BUTTON);

            add(event, BlockRegistry.FUNGAL_BRICKS);
            add(event, BlockRegistry.CRACKED_FUNGAL_BRICKS);
            add(event, BlockRegistry.FUNGAL_BRICK_STAIRS);
            add(event, BlockRegistry.FUNGAL_BRICK_SLAB);
            add(event, BlockRegistry.FUNGAL_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_FUNGAL_BRICKS);

            add(event, BlockRegistry.POLISHED_FUNGAL_STONE);
            add(event, BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.POLISHED_FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.POLISHED_FUNGAL_STONE_WALL);

            add(event, BlockRegistry.POLISHED_FUNGAL_BRICKS);
            add(event, BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS);
            add(event, BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS);
            add(event, BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB);
            add(event, BlockRegistry.POLISHED_FUNGAL_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_POLISHED_FUNGAL_BRICKS);

            add(event, BlockRegistry.DEEP_FUNGAL_STONE);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_WALL);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON);

            add(event, BlockRegistry.DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.CRACKED_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS);
            add(event, BlockRegistry.DEEP_FUNGAL_BRICK_SLAB);
            add(event, BlockRegistry.DEEP_FUNGAL_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS);

            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL);

            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_POLISHED_DEEP_FUNGAL_BRICKS);

            add(event, Blocks.AMETHYST_BLOCK);
            add(event, BlockRegistry.AMETHYST_STAIRS);
            add(event, BlockRegistry.AMETHYST_SLAB);
            add(event, BlockRegistry.AMETHYST_WALL);
            add(event, BlockRegistry.AMETHYST_PRESSURE_PLATE);
            add(event, BlockRegistry.AMETHYST_BUTTON);
            add(event, BlockRegistry.POLISHED_AMETHYST);
            add(event, BlockRegistry.POLISHED_AMETHYST_STAIRS);
            add(event, BlockRegistry.POLISHED_AMETHYST_SLAB);
            add(event, BlockRegistry.POLISHED_AMETHYST_WALL);
            add(event, BlockRegistry.AMETHYST_BRICKS);
            add(event, BlockRegistry.CRACKED_AMETHYST_BRICKS);
            add(event, BlockRegistry.AMETHYST_BRICK_STAIRS);
            add(event, BlockRegistry.AMETHYST_BRICK_SLAB);
            add(event, BlockRegistry.AMETHYST_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_AMETHYST_BRICKS);

            add(event, Blocks.SANDSTONE);
            add(event, Blocks.CUT_SANDSTONE);
            add(event, Blocks.CHISELED_SANDSTONE);
            add(event, BlockRegistry.SANDSTONE_BRICKS);
            add(event, BlockRegistry.CRACKED_SANDSTONE_BRICKS);
            add(event, BlockRegistry.SANDSTONE_BRICK_STAIRS);
            add(event, BlockRegistry.SANDSTONE_BRICK_SLAB);
            add(event, BlockRegistry.SANDSTONE_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_SANDSTONE_BRICKS);

            add(event, Blocks.RED_SANDSTONE);
            add(event, Blocks.CUT_RED_SANDSTONE);
            add(event, Blocks.CHISELED_RED_SANDSTONE);
            add(event, BlockRegistry.RED_SANDSTONE_BRICKS);
            add(event, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS);
            add(event, BlockRegistry.RED_SANDSTONE_BRICK_STAIRS);
            add(event, BlockRegistry.RED_SANDSTONE_BRICK_SLAB);
            add(event, BlockRegistry.RED_SANDSTONE_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_RED_SANDSTONE_BRICKS);

            add(event, Blocks.CALCITE);
            addDyedBlocks(event, Blocks.CALCITE, BlockRegistry.CALCITE, true, true);
            addDyedBlocks(event, BlockRegistry.CALCITE.get(DyeColor.PINK), BlockRegistry.POLISHED_CALCITE, true, true);
            addDyedBlocks(event, BlockRegistry.POLISHED_CALCITE.get(DyeColor.PINK), BlockRegistry.CALCITE_BRICKS, true, true);
            addDyedBlocks(event, BlockRegistry.CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CRACKED_CALCITE_BRICKS, true, true);
            addDyedBlocks(event, BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CHISELED_CALCITE_BRICKS, true, true);
            addDyedBlocks(event, BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.STORAGE_CALCITE_BRICKS, true, true);
            addDyedBlocks(event, BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CALCITE_BRICK_PEDESTAL, true, true);
            add(event, BlockRegistry.CALCITE_STAIRS);
            add(event, BlockRegistry.CALCITE_SLAB);
            add(event, BlockRegistry.CALCITE_WALL);
            add(event, BlockRegistry.CALCITE_PRESSURE_PLATE);
            add(event, BlockRegistry.CALCITE_BUTTON);
            add(event, BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS);
            add(event, BlockRegistry.POLISHED_WHITE_CALCITE_SLAB);
            add(event, BlockRegistry.POLISHED_WHITE_CALCITE_WALL);
            add(event, BlockRegistry.WHITE_CALCITE_BRICK_STAIRS);
            add(event, BlockRegistry.WHITE_CALCITE_BRICK_SLAB);
            add(event, BlockRegistry.WHITE_CALCITE_BRICK_WALL);
        }

        if (!ConfigRegistry.DISABLE_VANILLA_TABS.get()) {
            if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
                addAfter(event, Blocks.PRISMARINE, BlockRegistry.FUNGAL_STONE);
                addAfter(event, BlockRegistry.FUNGAL_STONE, BlockRegistry.DEEP_FUNGAL_STONE);

                addAfter(event, Items.PITCHER_POD, ItemRegistry.PIRANHA_PLANT_POD);

                addAfter(event, Blocks.PEARLESCENT_FROGLIGHT, BlockRegistry.GLOW_BLOCK);
            }

            if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
                addAfter(event, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.CALCITE);
                addDyedBlocks(event, Blocks.CALCITE, BlockRegistry.CALCITE, true, true);
                addDyedBlocks(event, BlockRegistry.CALCITE.get(DyeColor.PINK), BlockRegistry.POLISHED_CALCITE, true, true);
                addDyedBlocks(event, BlockRegistry.POLISHED_CALCITE.get(DyeColor.PINK), BlockRegistry.CALCITE_BRICKS, true, true);
                addDyedBlocks(event, BlockRegistry.CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CRACKED_CALCITE_BRICKS, true, true);
                addDyedBlocks(event, BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CHISELED_CALCITE_BRICKS, true, true);
                addDyedBlocks(event, BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.STORAGE_CALCITE_BRICKS, true, true);
                addDyedBlocks(event, BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CALCITE_BRICK_PEDESTAL, true, true);

                addAfter(event, Blocks.PINK_SHULKER_BOX, BlockRegistry.CLEAR_WARP_PIPE);
                addDyedBlocks(event, BlockRegistry.CLEAR_WARP_PIPE, BlockRegistry.WARP_PIPES, true, true);

                addAfter(event, Blocks.PINK_BANNER, BlockRegistry.CLASSIC_CHECKPOINT_FLAG);
                addDyedBlocks(event, BlockRegistry.CLASSIC_CHECKPOINT_FLAG, BlockRegistry.CHECKPOINT_FLAGS, true, true);

                addAfter(event, BlockRegistry.CHECKPOINT_FLAGS.get(DyeColor.PINK), BlockRegistry.CLASSIC_GOAL_POLE);
                addDyedBlocks(event, BlockRegistry.CLASSIC_GOAL_POLE, BlockRegistry.GOAL_POLES, true, true);
            }

            if (event.getTabKey() == CreativeModeTabs.COMBAT) {
                addBefore(event, Items.SHIELD, ItemRegistry.WRENCH);

                addAfter(event, Items.TOTEM_OF_UNDYING, ItemRegistry.SUPER_MUSHROOM);
                addAfter(event, ItemRegistry.SUPER_MUSHROOM, ItemRegistry.DASH_MUSHROOM);
                addAfter(event, ItemRegistry.DASH_MUSHROOM, ItemRegistry.ONE_UP_MUSHROOM);
                addAfter(event, ItemRegistry.ONE_UP_MUSHROOM, ItemRegistry.FIRE_FLOWER);
                addAfter(event, ItemRegistry.FIRE_FLOWER, ItemRegistry.ICE_FLOWER);
                addAfter(event, ItemRegistry.ICE_FLOWER, ItemRegistry.SUPER_STAR);
                addAfter(event, ItemRegistry.SUPER_STAR, ItemRegistry.GREEN_KOOPA_SHELL);
                addAfter(event, ItemRegistry.GREEN_KOOPA_SHELL, ItemRegistry.RED_KOOPA_SHELL);
                addAfter(event, ItemRegistry.RED_KOOPA_SHELL, ItemRegistry.GOLD_KOOPA_SHELL);

                addAfter(event, Items.TURTLE_HELMET, ItemRegistry.MARIO_HAT);
                addAfter(event, ItemRegistry.MARIO_HAT, ItemRegistry.MARIO_SHIRT);
                addAfter(event, ItemRegistry.MARIO_SHIRT, ItemRegistry.MARIO_PANTS);
                addAfter(event, ItemRegistry.MARIO_PANTS, ItemRegistry.MARIO_SHOES);
                addAfter(event, ItemRegistry.MARIO_SHOES, ItemRegistry.MARIO_FIRE_HAT);
                addAfter(event, ItemRegistry.MARIO_FIRE_HAT, ItemRegistry.MARIO_FIRE_SHIRT);
                addAfter(event, ItemRegistry.MARIO_FIRE_SHIRT, ItemRegistry.MARIO_FIRE_PANTS);
                addAfter(event, ItemRegistry.MARIO_FIRE_PANTS, ItemRegistry.MARIO_FIRE_SHOES);
                addAfter(event, ItemRegistry.MARIO_FIRE_SHOES, ItemRegistry.MARIO_ICE_HAT);
                addAfter(event, ItemRegistry.MARIO_ICE_HAT, ItemRegistry.MARIO_ICE_SHIRT);
                addAfter(event, ItemRegistry.MARIO_ICE_SHIRT, ItemRegistry.MARIO_ICE_PANTS);
                addAfter(event, ItemRegistry.MARIO_ICE_PANTS, ItemRegistry.MARIO_ICE_SHOES);

                addAfter(event, ItemRegistry.MARIO_ICE_SHOES, ItemRegistry.LUIGI_HAT);
                addAfter(event, ItemRegistry.LUIGI_HAT, ItemRegistry.LUIGI_SHIRT);
                addAfter(event, ItemRegistry.LUIGI_SHIRT, ItemRegistry.LUIGI_PANTS);
                addAfter(event, ItemRegistry.LUIGI_PANTS, ItemRegistry.LUIGI_SHOES);
                addAfter(event, ItemRegistry.LUIGI_SHOES, ItemRegistry.LUIGI_FIRE_HAT);
                addAfter(event, ItemRegistry.LUIGI_FIRE_HAT, ItemRegistry.LUIGI_FIRE_SHIRT);
                addAfter(event, ItemRegistry.LUIGI_FIRE_SHIRT, ItemRegistry.LUIGI_FIRE_PANTS);
                addAfter(event, ItemRegistry.LUIGI_FIRE_PANTS, ItemRegistry.LUIGI_FIRE_SHOES);
                addAfter(event, ItemRegistry.LUIGI_FIRE_SHOES, ItemRegistry.LUIGI_ICE_HAT);
                addAfter(event, ItemRegistry.LUIGI_ICE_HAT, ItemRegistry.LUIGI_ICE_SHIRT);
                addAfter(event, ItemRegistry.LUIGI_ICE_SHIRT, ItemRegistry.LUIGI_ICE_PANTS);
                addAfter(event, ItemRegistry.LUIGI_ICE_PANTS, ItemRegistry.LUIGI_ICE_SHOES);

                addAfter(event, ItemRegistry.LUIGI_ICE_SHOES, ItemRegistry.PEACH_CROWN);
                addAfter(event, ItemRegistry.PEACH_CROWN, ItemRegistry.PEACH_BODICE);
                addAfter(event, ItemRegistry.PEACH_BODICE, ItemRegistry.PEACH_DRESS);
                addAfter(event, ItemRegistry.PEACH_DRESS, ItemRegistry.PEACH_SHOES);
                addAfter(event, ItemRegistry.PEACH_SHOES, ItemRegistry.PEACH_FIRE_BODICE);
                addAfter(event, ItemRegistry.PEACH_FIRE_BODICE, ItemRegistry.PEACH_FIRE_DRESS);
                addAfter(event, ItemRegistry.PEACH_FIRE_DRESS, ItemRegistry.PEACH_FIRE_SHOES);
                addAfter(event, ItemRegistry.PEACH_FIRE_SHOES, ItemRegistry.PEACH_ICE_BODICE);
                addAfter(event, ItemRegistry.PEACH_ICE_BODICE, ItemRegistry.PEACH_ICE_DRESS);
                addAfter(event, ItemRegistry.PEACH_ICE_DRESS, ItemRegistry.PEACH_ICE_SHOES);

                addAfter(event, ItemRegistry.PEACH_ICE_SHOES, ItemRegistry.GREEN_KOOPA_SHOES);
                addAfter(event, ItemRegistry.GREEN_KOOPA_SHOES, ItemRegistry.RED_KOOPA_SHOES);
                addAfter(event, ItemRegistry.RED_KOOPA_SHOES, ItemRegistry.GOLDEN_KOOPA_SHOES);
            }

            if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
                addAfter(event, Blocks.STONE_BUTTON, BlockRegistry.FUNGAL_STONE_BUTTON);
                addAfter(event, BlockRegistry.FUNGAL_STONE_BUTTON, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON, BlockRegistry.AMETHYST_BUTTON);

                addAfter(event, Blocks.STONE_PRESSURE_PLATE, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE, BlockRegistry.AMETHYST_PRESSURE_PLATE);

                addAfter(event, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, BlockRegistry.FUNGAL_QUESTION_PANEL);

                addAfter(event, Items.DECORATED_POT, BlockRegistry.FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.FUNGAL_QUESTION_BLOCK, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK, BlockRegistry.STORAGE_FUNGAL_BRICKS);
                addAfter(event, Items.REDSTONE_LAMP, BlockRegistry.CLEAR_WARP_PIPE);
                addAfter(event, BlockRegistry.CLEAR_WARP_PIPE, BlockRegistry.WARP_PIPES.get(DyeColor.GREEN));
                addAfter(event, BlockRegistry.WARP_PIPES.get(DyeColor.GREEN), BlockRegistry.CHECKPOINT_FLAGS.get(DyeColor.RED));
                addAfter(event, BlockRegistry.CHECKPOINT_FLAGS.get(DyeColor.RED), BlockRegistry.GOAL_POLES.get(DyeColor.RED));
                addAfter(event, BlockRegistry.GOAL_POLES.get(DyeColor.RED), BlockRegistry.BRICK_PEDESTAL);
            }

            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                addAfter(event, Items.FISHING_ROD, ItemRegistry.WRENCH);
                addBefore(event, ItemRegistry.WRENCH, ItemRegistry.WARP_DISRUPTOR);
            }

            if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
                addAfter(event, Items.PUFFERFISH, ItemRegistry.DASH_MUSHROOM);
            }

            if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                addAfter(event, Items.GUSTER_BANNER_PATTERN, ItemRegistry.BOWSER_BANNER_PATTERN);
                addAfter(event, ItemRegistry.BOWSER_BANNER_PATTERN, ItemRegistry.PLUMBER_BANNER_PATTERN);
                addAfter(event, Items.SNORT_POTTERY_SHERD, ItemRegistry.BOWSER_POTTERY_SHERD);
                addAfter(event, ItemRegistry.BOWSER_POTTERY_SHERD, ItemRegistry.PLUMBER_POTTERY_SHERD);

                addAfter(event, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE);
                addAfter(event, ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE, ItemRegistry.LUIGI_COSTUME_SMITHING_TEMPLATE);
                addAfter(event, ItemRegistry.LUIGI_COSTUME_SMITHING_TEMPLATE, ItemRegistry.PEACH_COSTUME_SMITHING_TEMPLATE);
                addAfter(event, ItemRegistry.PEACH_COSTUME_SMITHING_TEMPLATE, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE);
                addAfter(event, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE);
            }

            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                addAfter(event, Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, ItemRegistry.MINI_GOOMBA_SPAWN_EGG);
                addAfter(event, ItemRegistry.MINI_GOOMBA_SPAWN_EGG, ItemRegistry.GOOMBA_SPAWN_EGG);
                addAfter(event, ItemRegistry.GOOMBA_SPAWN_EGG, ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG);
                addAfter(event, ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG, ItemRegistry.MEGA_GOOMBA_SPAWN_EGG);
                addAfter(event, ItemRegistry.MEGA_GOOMBA_SPAWN_EGG, ItemRegistry.FIRE_GOOMBA_SPAWN_EGG);
                addAfter(event, ItemRegistry.FIRE_GOOMBA_SPAWN_EGG, ItemRegistry.GREEN_KOOPA_TROOPA_SPAWN_EGG);
                addAfter(event, ItemRegistry.GREEN_KOOPA_TROOPA_SPAWN_EGG, ItemRegistry.RED_KOOPA_TROOPA_SPAWN_EGG);
                addAfter(event, ItemRegistry.RED_KOOPA_TROOPA_SPAWN_EGG, ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG);
                addAfter(event, ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG, ItemRegistry.PIRANHA_PLANT_SPAWN_EGG);
            }

            if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
                addAfter(event, Blocks.CHAIN, BlockRegistry.IRON_SPIKE);

                addAfter(event, Blocks.POLISHED_ANDESITE_SLAB, BlockRegistry.FUNGAL_STONE);
                addAfter(event, BlockRegistry.FUNGAL_STONE, BlockRegistry.FUNGAL_STONE_STAIRS);
                addAfter(event, BlockRegistry.FUNGAL_STONE_STAIRS, BlockRegistry.FUNGAL_STONE_SLAB);
                addAfter(event, BlockRegistry.FUNGAL_STONE_SLAB, BlockRegistry.FUNGAL_STONE_WALL);
                addAfter(event, BlockRegistry.FUNGAL_STONE_WALL, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE, BlockRegistry.FUNGAL_STONE_BUTTON);

                addAfter(event, BlockRegistry.FUNGAL_STONE_BUTTON, BlockRegistry.POLISHED_FUNGAL_STONE);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_STONE, BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS, BlockRegistry.POLISHED_FUNGAL_STONE_SLAB);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_STONE_SLAB, BlockRegistry.POLISHED_FUNGAL_STONE_WALL);

                addAfter(event, BlockRegistry.POLISHED_FUNGAL_STONE_WALL, BlockRegistry.DEEP_FUNGAL_STONE);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE, BlockRegistry.DEEP_FUNGAL_STONE_STAIRS);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_STAIRS, BlockRegistry.DEEP_FUNGAL_STONE_SLAB);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_SLAB, BlockRegistry.DEEP_FUNGAL_STONE_WALL);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_WALL, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON);

                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL);

                addAfter(event, Blocks.STONE_BRICK_WALL, BlockRegistry.STONE_BRICK_PEDESTAL);
                addAfter(event, Blocks.CHISELED_STONE_BRICKS, BlockRegistry.SMASHABLE_STONE_BRICKS);

                addAfter(event, Blocks.MOSSY_STONE_BRICK_WALL, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS);

                addAfter(event, Blocks.REINFORCED_DEEPSLATE, Blocks.CALCITE);
                addAfter(event, Blocks.CALCITE, BlockRegistry.CALCITE_STAIRS);
                addAfter(event, BlockRegistry.CALCITE_STAIRS, BlockRegistry.CALCITE_SLAB);
                addAfter(event, BlockRegistry.CALCITE_SLAB, BlockRegistry.CALCITE_WALL);
                addAfter(event, BlockRegistry.CALCITE_WALL, BlockRegistry.CALCITE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.CALCITE_PRESSURE_PLATE, BlockRegistry.CALCITE_BUTTON);

                addAfter(event, BlockRegistry.CALCITE_BUTTON, BlockRegistry.POLISHED_CALCITE.get(DyeColor.WHITE));
                addAfter(event, BlockRegistry.POLISHED_CALCITE.get(DyeColor.WHITE), BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS, BlockRegistry.POLISHED_WHITE_CALCITE_SLAB);
                addAfter(event, BlockRegistry.POLISHED_WHITE_CALCITE_SLAB, BlockRegistry.POLISHED_WHITE_CALCITE_WALL);
                addAfter(event, BlockRegistry.POLISHED_WHITE_CALCITE_WALL, BlockRegistry.CALCITE_BRICKS.get(DyeColor.WHITE));

                addAfter(event, BlockRegistry.CALCITE_BRICKS.get(DyeColor.WHITE), BlockRegistry.WHITE_CALCITE_BRICK_STAIRS);
                addAfter(event, BlockRegistry.WHITE_CALCITE_BRICK_STAIRS, BlockRegistry.WHITE_CALCITE_BRICK_SLAB);
                addAfter(event, BlockRegistry.WHITE_CALCITE_BRICK_SLAB, BlockRegistry.WHITE_CALCITE_BRICK_WALL);
                addAfter(event, BlockRegistry.WHITE_CALCITE_BRICK_WALL, BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.WHITE));
                addAfter(event, BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.WHITE), BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.WHITE));

                addAfter(event, Blocks.DEEPSLATE_BRICK_WALL, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS);
                addAfter(event, Blocks.DEEPSLATE_TILE_WALL, BlockRegistry.DEEPSLATE_TILE_PEDESTAL);
                addAfter(event, BlockRegistry.DEEPSLATE_TILE_PEDESTAL, BlockRegistry.SMASHABLE_DEEPSLATE_TILES);

                addAfter(event, Blocks.TUFF_BRICK_WALL, BlockRegistry.TUFF_BRICK_PEDESTAL);
                addAfter(event, Blocks.CHISELED_TUFF_BRICKS, BlockRegistry.SMASHABLE_TUFF_BRICKS);

                addAfter(event, Blocks.BRICK_WALL, BlockRegistry.BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.BRICK_PEDESTAL, BlockRegistry.SMASHABLE_BRICKS);

                addAfter(event, Blocks.MUD_BRICK_WALL, BlockRegistry.MUD_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.MUD_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_MUD_BRICKS);

                addAfter(event, Blocks.CUT_SANDSTONE_SLAB, BlockRegistry.SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.SANDSTONE_BRICKS, BlockRegistry.CRACKED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_SANDSTONE_BRICKS, BlockRegistry.SANDSTONE_BRICK_STAIRS);
                addAfter(event, BlockRegistry.SANDSTONE_BRICK_STAIRS, BlockRegistry.SANDSTONE_BRICK_SLAB);
                addAfter(event, BlockRegistry.SANDSTONE_BRICK_SLAB, BlockRegistry.SANDSTONE_BRICK_WALL);
                addAfter(event, BlockRegistry.SANDSTONE_BRICK_WALL, BlockRegistry.SANDSTONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.SANDSTONE_BRICK_PEDESTAL, BlockRegistry.CHISELED_SANDSTONE_BRICKS);

                addAfter(event, Blocks.CUT_RED_SANDSTONE_SLAB, BlockRegistry.RED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICKS, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS, BlockRegistry.RED_SANDSTONE_BRICK_STAIRS);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICK_STAIRS, BlockRegistry.RED_SANDSTONE_BRICK_SLAB);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICK_SLAB, BlockRegistry.RED_SANDSTONE_BRICK_WALL);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICK_WALL, BlockRegistry.RED_SANDSTONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICK_PEDESTAL, BlockRegistry.CHISELED_RED_SANDSTONE_BRICKS);

                addAfter(event, Blocks.PRISMARINE_BRICK_SLAB, BlockRegistry.PRISMARINE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.PRISMARINE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_PRISMARINE_BRICKS);

                addAfter(event, Blocks.DARK_PRISMARINE_SLAB, BlockRegistry.DARK_PRISMARINE_PEDESTAL);
                addAfter(event, BlockRegistry.DARK_PRISMARINE_PEDESTAL, BlockRegistry.SMASHABLE_DARK_PRISMARINE);

                addAfter(event, Blocks.AMETHYST_BLOCK, BlockRegistry.AMETHYST_STAIRS);
                addAfter(event, BlockRegistry.AMETHYST_STAIRS, BlockRegistry.AMETHYST_SLAB);
                addAfter(event, BlockRegistry.AMETHYST_SLAB, BlockRegistry.AMETHYST_WALL);
                addAfter(event, BlockRegistry.AMETHYST_WALL, BlockRegistry.AMETHYST_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.AMETHYST_PRESSURE_PLATE, BlockRegistry.AMETHYST_BUTTON);
                addAfter(event, BlockRegistry.AMETHYST_BUTTON, BlockRegistry.POLISHED_AMETHYST);
                addAfter(event, BlockRegistry.POLISHED_AMETHYST, BlockRegistry.POLISHED_AMETHYST_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_AMETHYST_STAIRS, BlockRegistry.POLISHED_AMETHYST_SLAB);
                addAfter(event, BlockRegistry.POLISHED_AMETHYST_SLAB, BlockRegistry.POLISHED_AMETHYST_WALL);
                addAfter(event, BlockRegistry.POLISHED_AMETHYST_WALL, BlockRegistry.AMETHYST_BRICKS);
                addAfter(event, BlockRegistry.AMETHYST_BRICKS, BlockRegistry.CRACKED_AMETHYST_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_AMETHYST_BRICKS, BlockRegistry.AMETHYST_BRICK_STAIRS);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_STAIRS, BlockRegistry.AMETHYST_BRICK_SLAB);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_SLAB, BlockRegistry.AMETHYST_BRICK_WALL);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_WALL, BlockRegistry.AMETHYST_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_PEDESTAL, BlockRegistry.CHISELED_AMETHYST_BRICKS);

                addAfter(event, BlockRegistry.CHISELED_AMETHYST_BRICKS, BlockRegistry.FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.FUNGAL_BRICKS, BlockRegistry.CRACKED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_FUNGAL_BRICKS, BlockRegistry.FUNGAL_BRICK_STAIRS);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_STAIRS, BlockRegistry.FUNGAL_BRICK_SLAB);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_SLAB, BlockRegistry.FUNGAL_BRICK_WALL);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_WALL, BlockRegistry.FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_PEDESTAL, BlockRegistry.CHISELED_FUNGAL_BRICKS);

                addAfter(event, BlockRegistry.CHISELED_FUNGAL_BRICKS, BlockRegistry.POLISHED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICKS, BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS, BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS, BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB, BlockRegistry.POLISHED_FUNGAL_BRICK_WALL);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_WALL, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL, BlockRegistry.CHISELED_POLISHED_FUNGAL_BRICKS);

                addAfter(event, BlockRegistry.CHISELED_POLISHED_FUNGAL_BRICKS, BlockRegistry.DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICKS, BlockRegistry.CRACKED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_DEEP_FUNGAL_BRICKS, BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS, BlockRegistry.DEEP_FUNGAL_BRICK_SLAB);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_SLAB, BlockRegistry.DEEP_FUNGAL_BRICK_WALL);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_WALL, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL, BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS);

                addAfter(event, BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS, BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL, BlockRegistry.CHISELED_POLISHED_DEEP_FUNGAL_BRICKS);

                addAfter(event, Blocks.NETHER_BRICK_FENCE, BlockRegistry.NETHER_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.NETHER_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_NETHER_BRICKS);

                addAfter(event, Blocks.RED_NETHER_BRICK_WALL, BlockRegistry.RED_NETHER_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.RED_NETHER_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS);

                addAfter(event, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS);

                addAfter(event, Blocks.END_STONE_BRICK_WALL, BlockRegistry.END_STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.END_STONE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_END_STONE_BRICKS);

                addAfter(event, Blocks.PURPUR_SLAB, BlockRegistry.PURPUR_BLOCK_PEDESTAL);
                addAfter(event, BlockRegistry.PURPUR_BLOCK_PEDESTAL, BlockRegistry.SMASHABLE_PURPUR_BLOCK);

                addAfter(event, Blocks.QUARTZ_BRICKS, BlockRegistry.SMASHABLE_QUARTZ_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_QUARTZ_BRICKS, BlockRegistry.QUARTZ_BRICK_PEDESTAL);

                addAfter(event, Blocks.CUT_COPPER_SLAB, BlockRegistry.CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_CUT_COPPER);

                addAfter(event, Blocks.EXPOSED_CUT_COPPER_SLAB, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER);

                addAfter(event, Blocks.WEATHERED_CUT_COPPER_SLAB, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER);

                addAfter(event, Blocks.OXIDIZED_CUT_COPPER_SLAB, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER);

                addAfter(event, Blocks.WAXED_CUT_COPPER_SLAB, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER);

                addAfter(event, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER);

                addAfter(event, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER);

                addAfter(event, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER);
            }

            if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
                addAfter(event, Blocks.PEARLESCENT_FROGLIGHT, BlockRegistry.GLOW_BLOCK);

                addBefore(event, Items.LIGHTNING_ROD, BlockRegistry.FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.FUNGAL_QUESTION_BLOCK, BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK, BlockRegistry.AMETHYST_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.AMETHYST_QUESTION_BLOCK, BlockRegistry.CALCITE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.CALCITE_QUESTION_BLOCK, BlockRegistry.STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.STONE_QUESTION_BRICKS, BlockRegistry.MOSSY_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.MOSSY_STONE_QUESTION_BRICKS, BlockRegistry.DEEPSLATE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.DEEPSLATE_QUESTION_BRICKS, BlockRegistry.DEEPSLATE_QUESTION_TILES);
                addAfter(event, BlockRegistry.DEEPSLATE_QUESTION_TILES, BlockRegistry.TUFF_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.TUFF_QUESTION_BRICKS, BlockRegistry.QUESTION_BRICKS);
                addAfter(event, BlockRegistry.QUESTION_BRICKS, BlockRegistry.MUD_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.MUD_QUESTION_BRICKS, BlockRegistry.SANDSTONE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.SANDSTONE_QUESTION_BLOCK, BlockRegistry.RED_SANDSTONE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.RED_SANDSTONE_QUESTION_BLOCK, BlockRegistry.PRISMARINE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.PRISMARINE_QUESTION_BRICKS, BlockRegistry.NETHER_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.NETHER_QUESTION_BRICKS, BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK, BlockRegistry.RED_NETHER_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.RED_NETHER_QUESTION_BRICKS, BlockRegistry.BLACKSTONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.BLACKSTONE_QUESTION_BRICKS, BlockRegistry.QUARTZ_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.QUARTZ_QUESTION_BRICKS, BlockRegistry.END_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.END_STONE_QUESTION_BRICKS, BlockRegistry.PURPUR_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.PURPUR_QUESTION_BLOCK, BlockRegistry.COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.COPPER_QUESTION_BLOCK, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK);

                addAfter(event, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK, BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK, BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK, BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_CALCITE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_CALCITE_QUESTION_BLOCK, BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES);
                addAfter(event, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES, BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS, BlockRegistry.INVISIBLE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS, BlockRegistry.INVISIBLE_SANDSTONE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_SANDSTONE_QUESTION_BLOCK, BlockRegistry.INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK, BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS, BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS, BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS, BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK, BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK);

                addAfter(event, BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.STORAGE_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_FUNGAL_BRICKS, BlockRegistry.STORAGE_POLISHED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_POLISHED_FUNGAL_BRICKS, BlockRegistry.STORAGE_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_DEEP_FUNGAL_BRICKS, BlockRegistry.STORAGE_POLISHED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_POLISHED_DEEP_FUNGAL_BRICKS, BlockRegistry.STORAGE_AMETHYST_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_AMETHYST_BRICKS, BlockRegistry.STORAGE_STONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_STONE_BRICKS, BlockRegistry.STORAGE_MOSSY_STONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_MOSSY_STONE_BRICKS, BlockRegistry.STORAGE_DEEPSLATE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_DEEPSLATE_BRICKS, BlockRegistry.STORAGE_DEEPSLATE_TILES);
                addAfter(event, BlockRegistry.STORAGE_DEEPSLATE_TILES, BlockRegistry.STORAGE_TUFF_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_TUFF_BRICKS, BlockRegistry.STORAGE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_BRICKS, BlockRegistry.STORAGE_MUD_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_MUD_BRICKS, BlockRegistry.STORAGE_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_SANDSTONE_BRICKS, BlockRegistry.STORAGE_RED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_RED_SANDSTONE_BRICKS, BlockRegistry.STORAGE_PRISMARINE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_PRISMARINE_BRICKS, BlockRegistry.STORAGE_DARK_PRISMARINE);
                addAfter(event, BlockRegistry.STORAGE_DARK_PRISMARINE, BlockRegistry.STORAGE_NETHER_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_NETHER_BRICKS, BlockRegistry.STORAGE_RED_NETHER_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_RED_NETHER_BRICKS, BlockRegistry.STORAGE_BLACKSTONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_BLACKSTONE_BRICKS, BlockRegistry.STORAGE_QUARTZ_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_QUARTZ_BRICKS, BlockRegistry.STORAGE_END_STONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_END_STONE_BRICKS, BlockRegistry.STORAGE_PURPUR_BLOCK);
                addAfter(event, BlockRegistry.STORAGE_PURPUR_BLOCK, BlockRegistry.STORAGE_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_CUT_COPPER, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER, BlockRegistry.STORAGE_WAXED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_WAXED_CUT_COPPER, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER);
                addDyedBlocks(event, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.STORAGE_CALCITE_BRICKS, false, false);

                addAfter(event, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.FUNGAL_BRICKS, BlockRegistry.POLISHED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICKS, BlockRegistry.DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICKS, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS, BlockRegistry.AMETHYST_BRICKS);
                addAfter(event, BlockRegistry.AMETHYST_BRICKS, BlockRegistry.SMASHABLE_STONE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_STONE_BRICKS, BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS, BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS, BlockRegistry.SMASHABLE_DEEPSLATE_TILES);
                addAfter(event, BlockRegistry.SMASHABLE_DEEPSLATE_TILES, BlockRegistry.SMASHABLE_TUFF_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_TUFF_BRICKS, BlockRegistry.SMASHABLE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_BRICKS, BlockRegistry.SMASHABLE_MUD_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_MUD_BRICKS, BlockRegistry.CRACKED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_SANDSTONE_BRICKS, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS, BlockRegistry.SMASHABLE_PRISMARINE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_PRISMARINE_BRICKS, BlockRegistry.SMASHABLE_DARK_PRISMARINE);
                addAfter(event, BlockRegistry.SMASHABLE_DARK_PRISMARINE, BlockRegistry.SMASHABLE_NETHER_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_NETHER_BRICKS, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS, BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS, BlockRegistry.SMASHABLE_QUARTZ_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_QUARTZ_BRICKS, BlockRegistry.SMASHABLE_END_STONE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_END_STONE_BRICKS, BlockRegistry.SMASHABLE_PURPUR_BLOCK);
                addAfter(event, BlockRegistry.SMASHABLE_PURPUR_BLOCK, BlockRegistry.SMASHABLE_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_CUT_COPPER, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER);

                addAfter(event, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_PEDESTAL, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL, BlockRegistry.AMETHYST_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_PEDESTAL, BlockRegistry.STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.STONE_BRICK_PEDESTAL, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL, BlockRegistry.DEEPSLATE_TILE_PEDESTAL);
                addAfter(event, BlockRegistry.DEEPSLATE_TILE_PEDESTAL, BlockRegistry.TUFF_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.TUFF_BRICK_PEDESTAL, BlockRegistry.BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.BRICK_PEDESTAL, BlockRegistry.MUD_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.MUD_BRICK_PEDESTAL, BlockRegistry.PRISMARINE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.PRISMARINE_BRICK_PEDESTAL, BlockRegistry.DARK_PRISMARINE_PEDESTAL);
                addAfter(event, BlockRegistry.DARK_PRISMARINE_PEDESTAL, BlockRegistry.NETHER_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.NETHER_BRICK_PEDESTAL, BlockRegistry.RED_NETHER_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.RED_NETHER_BRICK_PEDESTAL, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL, BlockRegistry.QUARTZ_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.QUARTZ_BRICK_PEDESTAL, BlockRegistry.END_STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.END_STONE_BRICK_PEDESTAL, BlockRegistry.PURPUR_BLOCK_PEDESTAL);
                addAfter(event, BlockRegistry.PURPUR_BLOCK_PEDESTAL, BlockRegistry.CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.CUT_COPPER_PEDESTAL, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL);
                addDyedBlocks(event, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.CALCITE_BRICK_PEDESTAL, false, false);

                addAfter(event, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.IRON_SPIKE);

                addAfter(event, Items.RESPAWN_ANCHOR, BlockRegistry.CLEAR_WARP_PIPE);
                addDyedBlocks(event, BlockRegistry.CLEAR_WARP_PIPE, BlockRegistry.WARP_PIPES, true, true);

                addBefore(event, Blocks.SKELETON_SKULL, BlockRegistry.CLASSIC_GOAL_POLE);
                addBefore(event, BlockRegistry.CLASSIC_GOAL_POLE, BlockRegistry.CLASSIC_CHECKPOINT_FLAG);
                addDyedBlocks(event, BlockRegistry.CLASSIC_CHECKPOINT_FLAG, BlockRegistry.CHECKPOINT_FLAGS, false, false);
                addDyedBlocks(event, BlockRegistry.CLASSIC_GOAL_POLE, BlockRegistry.GOAL_POLES, false, false);
            }
        }
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemLike item) {
        ItemStack stack = new ItemStack(item);
        add(event, stack);
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemStack stack) {
        if (stack.isEmpty()) {
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
            if (!processedColors.contains(color))
                additionalBlocks.add(entry.getValue().get());
        }

        Set<Block> listedBlocks = new HashSet<>();

        // Adds all dyed blocks
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

        // Adds any additional blocks that were not in the dyed blocks
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
