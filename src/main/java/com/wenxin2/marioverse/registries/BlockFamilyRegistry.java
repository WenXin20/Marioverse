package com.wenxin2.marioverse.registries;

import com.google.common.collect.Maps;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class BlockFamilyRegistry extends BlockFamilies {
    private static final Map<Block, BlockFamilyExtended> MAP = Maps.newHashMap();

    public static final BlockFamilyExtended ACACIA_LOG = familyBuilder(Blocks.ACACIA_LOG)
            .bridge(BlockRegistry.ACACIA_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.ACACIA_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_ACACIA_LOG = familyBuilder(Blocks.STRIPPED_ACACIA_LOG)
            .bridge(BlockRegistry.STRIPPED_ACACIA_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_ACACIA_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended BAMBOO_BLOCK = familyBuilder(Blocks.BAMBOO_BLOCK)
            .bridge(BlockRegistry.BAMBOO_BRIDGE.get())
            .bridgeStairs(BlockRegistry.BAMBOO_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_BAMBOO_BLOCK = familyBuilder(Blocks.STRIPPED_BAMBOO_BLOCK)
            .bridge(BlockRegistry.STRIPPED_BAMBOO_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_BAMBOO_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended BIRCH_LOG = familyBuilder(Blocks.BIRCH_LOG)
            .bridge(BlockRegistry.BIRCH_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.BIRCH_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_BIRCH_LOG = familyBuilder(Blocks.STRIPPED_BIRCH_LOG)
            .bridge(BlockRegistry.STRIPPED_BIRCH_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_BIRCH_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended CHERRY_LOG = familyBuilder(Blocks.CHERRY_LOG)
            .bridge(BlockRegistry.CHERRY_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.CHERRY_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_CHERRY_LOG = familyBuilder(Blocks.STRIPPED_CHERRY_LOG)
            .bridge(BlockRegistry.STRIPPED_CHERRY_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_CHERRY_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended CRIMSON_STEM = familyBuilder(Blocks.CRIMSON_STEM)
            .bridge(BlockRegistry.CRIMSON_STEM_BRIDGE.get())
            .bridgeStairs(BlockRegistry.CRIMSON_STEM_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_CRIMSON_STEM = familyBuilder(Blocks.STRIPPED_CRIMSON_STEM)
            .bridge(BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended MANGROVE_LOG = familyBuilder(Blocks.MANGROVE_LOG)
            .bridge(BlockRegistry.MANGROVE_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.MANGROVE_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_MANGROVE_LOG = familyBuilder(Blocks.STRIPPED_MANGROVE_LOG)
            .bridge(BlockRegistry.STRIPPED_MANGROVE_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_MANGROVE_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended MUSHROOT_LOG = familyBuilder(BlockRegistry.MUSHROOT_LOG.get())
            .bridge(BlockRegistry.MUSHROOT_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.MUSHROOT_LOG_BRIDGE_STAIRS.get())
            .logPlatform(BlockRegistry.MUSHROOT_LOG_PLATFORM.get())
            .planks(BlockRegistry.MUSHROOT_PLANKS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_MUSHROOT_LOG = familyBuilder(BlockRegistry.STRIPPED_MUSHROOT_LOG.get())
            .bridge(BlockRegistry.STRIPPED_MUSHROOT_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_MUSHROOT_LOG_BRIDGE_STAIRS.get())
            .hangingSign(BlockRegistry.MUSHROOT_HANGING_SIGN.get(), BlockRegistry.MUSHROOT_WALL_HANGING_SIGN.get())
            .logPlatform(BlockRegistry.STRIPPED_MUSHROOT_LOG_PLATFORM.get())
            .planks(BlockRegistry.MUSHROOT_PLANKS.get())
            .getFamily();

    public static final BlockFamilyExtended MUSHROOT_WOOD = familyBuilder(BlockRegistry.MUSHROOT_WOOD.get())
            .planks(BlockRegistry.MUSHROOT_PLANKS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_MUSHROOT_WOOD = familyBuilder(BlockRegistry.STRIPPED_MUSHROOT_WOOD.get())
            .hangingSign(BlockRegistry.MUSHROOT_HANGING_SIGN.get(), BlockRegistry.MUSHROOT_WALL_HANGING_SIGN.get())
            .planks(BlockRegistry.MUSHROOT_PLANKS.get())
            .getFamily();

    public static final BlockFamilyExtended DARK_OAK_LOG = familyBuilder(Blocks.DARK_OAK_LOG)
            .bridge(BlockRegistry.DARK_OAK_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.DARK_OAK_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_DARK_OAK_LOG = familyBuilder(Blocks.STRIPPED_DARK_OAK_LOG)
            .bridge(BlockRegistry.STRIPPED_DARK_OAK_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_DARK_OAK_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended JUNGLE_LOG = familyBuilder(Blocks.JUNGLE_LOG)
            .bridge(BlockRegistry.JUNGLE_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.JUNGLE_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_JUNGLE_LOG = familyBuilder(Blocks.STRIPPED_JUNGLE_LOG)
            .bridge(BlockRegistry.STRIPPED_JUNGLE_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_JUNGLE_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended OAK_LOG = familyBuilder(Blocks.OAK_LOG)
            .bridge(BlockRegistry.OAK_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.OAK_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_OAK_LOG = familyBuilder(Blocks.STRIPPED_OAK_LOG)
            .bridge(BlockRegistry.STRIPPED_OAK_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_OAK_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended SPRUCE_LOG = familyBuilder(Blocks.SPRUCE_LOG)
            .bridge(BlockRegistry.SPRUCE_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.SPRUCE_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_SPRUCE_LOG = familyBuilder(Blocks.STRIPPED_SPRUCE_LOG)
            .bridge(BlockRegistry.STRIPPED_SPRUCE_LOG_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_SPRUCE_LOG_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended WARPED_STEM = familyBuilder(Blocks.WARPED_STEM)
            .bridge(BlockRegistry.WARPED_STEM_BRIDGE.get())
            .bridgeStairs(BlockRegistry.WARPED_STEM_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended STRIPPED_WARPED_STEM = familyBuilder(Blocks.STRIPPED_WARPED_STEM)
            .bridge(BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE.get())
            .bridgeStairs(BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE_STAIRS.get())
            .getFamily();

    public static final BlockFamilyExtended ACACIA_SLAB = familyBuilder(Blocks.ACACIA_SLAB)
            .picketFence(BlockRegistry.ACACIA_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended BAMBOO_SLAB = familyBuilder(Blocks.BAMBOO_SLAB)
            .picketFence(BlockRegistry.BAMBOO_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended BIRCH_SLAB = familyBuilder(Blocks.BIRCH_SLAB)
            .picketFence(BlockRegistry.BIRCH_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended CHERRY_SLAB = familyBuilder(Blocks.CHERRY_SLAB)
            .picketFence(BlockRegistry.CHERRY_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended CRIMSON_SLAB = familyBuilder(Blocks.CRIMSON_SLAB)
            .picketFence(BlockRegistry.CRIMSON_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended DARK_OAK_SLAB = familyBuilder(Blocks.DARK_OAK_SLAB)
            .picketFence(BlockRegistry.DARK_OAK_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended JUNGLE_SLAB = familyBuilder(Blocks.JUNGLE_SLAB)
            .picketFence(BlockRegistry.JUNGLE_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended MANGROVE_SLAB = familyBuilder(Blocks.MANGROVE_SLAB)
            .picketFence(BlockRegistry.MANGROVE_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended MUSHROOT_SLAB = familyBuilder(BlockRegistry.MUSHROOT_SLAB.get())
            .picketFence(BlockRegistry.MUSHROOT_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended MUSHROOT_BOARD_SLAB = familyBuilder(BlockRegistry.MUSHROOT_BOARD_SLAB.get())
            .picketFence(BlockRegistry.MUSHROOT_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended MUSHROOT_PANEL_SLAB = familyBuilder(BlockRegistry.MUSHROOT_PANEL_SLAB.get())
            .picketFence(BlockRegistry.MUSHROOT_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended OAK_SLAB = familyBuilder(Blocks.OAK_SLAB)
            .picketFence(BlockRegistry.OAK_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended SPRUCE_SLAB = familyBuilder(Blocks.SPRUCE_SLAB)
            .picketFence(BlockRegistry.SPRUCE_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended WARPED_SLAB = familyBuilder(Blocks.WARPED_SLAB)
            .picketFence(BlockRegistry.WARPED_PICKET_FENCE.get())
            .getFamily();

    public static final BlockFamilyExtended MUSHROOT_PLANKS = familyBuilder(BlockRegistry.MUSHROOT_PLANKS.get())
            .boards(BlockRegistry.MUSHROOT_BOARDS.get())
            .button(BlockRegistry.MUSHROOT_BUTTON.get())
            .door(BlockRegistry.MUSHROOT_DOOR.get())
            .hardBlock(BlockRegistry.HARD_MUSHROOT_BLOCK.get())
            .fence(BlockRegistry.MUSHROOT_FENCE.get())
            .fenceGate(BlockRegistry.MUSHROOT_FENCE_GATE.get())
            .panelsFromBoards(BlockRegistry.MUSHROOT_PANELS.get())
            .pressurePlate(BlockRegistry.MUSHROOT_PRESSURE_PLATE.get())
            .sign(BlockRegistry.MUSHROOT_SIGN.get(), BlockRegistry.MUSHROOT_WALL_SIGN.get())
            .slab(BlockRegistry.MUSHROOT_SLAB.get())
            .stairs(BlockRegistry.MUSHROOT_STAIRS.get())
            .trapdoor(BlockRegistry.MUSHROOT_TRAPDOOR.get())
            .window(BlockRegistry.MUSHROOT_FRAMED_WINDOW.get())
            .getFamily();

    public static final BlockFamilyExtended MUSHROOT_BOARDS = familyBuilder(BlockRegistry.MUSHROOT_BOARDS.get())
            .hardBlock(BlockRegistry.HARD_MUSHROOT_BLOCK.get())
            .panels(BlockRegistry.MUSHROOT_PANELS.get())
            .slab(BlockRegistry.MUSHROOT_BOARD_SLAB.get())
            .stairs(BlockRegistry.MUSHROOT_BOARD_STAIRS.get())
            .wall(BlockRegistry.MUSHROOT_BOARD_WALL.get())
            .window(BlockRegistry.MUSHROOT_FRAMED_WINDOW.get())
            .getFamily();

    public static final BlockFamilyExtended MUSHROOT_PANELS = familyBuilder(BlockRegistry.MUSHROOT_PANELS.get())
            .hardBlock(BlockRegistry.HARD_MUSHROOT_BLOCK.get())
            .slab(BlockRegistry.MUSHROOT_PANEL_SLAB.get())
            .stairs(BlockRegistry.MUSHROOT_PANEL_STAIRS.get())
            .wall(BlockRegistry.MUSHROOT_PANEL_WALL.get())
            .window(BlockRegistry.MUSHROOT_FRAMED_WINDOW.get())
            .getFamily();

    public static final BlockFamilyExtended HARD_MUSHROOT_BLOCK = familyBuilder(BlockRegistry.HARD_MUSHROOT_BLOCK.get())
            .slab(BlockRegistry.HARD_MUSHROOT_SLAB.get())
            .stairs(BlockRegistry.HARD_MUSHROOT_STAIRS.get())
            .wall(BlockRegistry.HARD_MUSHROOT_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended MUSHROOT_FRAMED_WINDOW = familyBuilder(BlockRegistry.MUSHROOT_FRAMED_WINDOW.get())
            .windowPane(BlockRegistry.MUSHROOT_FRAMED_WINDOW_PANE.get())
            .getFamily();

    public static final BlockFamilyExtended AMETHYST = familyBuilder(Blocks.AMETHYST_BLOCK)
            .button(BlockRegistry.AMETHYST_BUTTON.get())
            .polished(BlockRegistry.POLISHED_AMETHYST.get())
            .pressurePlate(BlockRegistry.AMETHYST_PRESSURE_PLATE.get())
            .slab(BlockRegistry.AMETHYST_SLAB.get())
            .stairs(BlockRegistry.AMETHYST_STAIRS.get())
            .wall(BlockRegistry.AMETHYST_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended AMETHYST_BRICKS = familyBuilder(BlockRegistry.AMETHYST_BRICKS.get())
            .chiseled(BlockRegistry.CHISELED_AMETHYST_BRICKS.get())
            .cracked(BlockRegistry.CRACKED_AMETHYST_BRICKS.get())
            .pedestal(BlockRegistry.AMETHYST_BRICK_PEDESTAL.get())
            .slab(BlockRegistry.AMETHYST_BRICK_SLAB.get())
            .stairs(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_AMETHYST_BRICKS.get())
            .wall(BlockRegistry.AMETHYST_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_AMETHYST = familyBuilder(BlockRegistry.POLISHED_AMETHYST.get())
            .bricks(BlockRegistry.AMETHYST_BRICKS.get())
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.AMETHYST_QUESTION_BLOCK.get())
            .slab(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
            .stairs(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
            .wall(BlockRegistry.POLISHED_AMETHYST_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended CALCITE_CHECKERED_TILES = familyBuilder(BlockRegistry.CALCITE_CHECKERED_TILES.get())
            .slab(BlockRegistry.CALCITE_CHECKERED_TILE_SLAB.get())
            .stairs(BlockRegistry.CALCITE_CHECKERED_TILE_STAIRS.get())
            .wall(BlockRegistry.CALCITE_CHECKERED_TILE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended CALCITE = familyBuilder(Blocks.CALCITE)
            .button(BlockRegistry.CALCITE_BUTTON.get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.WHITE).get())
            .pressurePlate(BlockRegistry.CALCITE_PRESSURE_PLATE.get())
            .slab(BlockRegistry.CALCITE_SLAB.get())
            .stairs(BlockRegistry.CALCITE_STAIRS.get())
            .wall(BlockRegistry.CALCITE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended LIGHT_GRAY_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.LIGHT_GRAY).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.LIGHT_GRAY).get())
            .getFamily();

    public static final BlockFamilyExtended GRAY_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.GRAY).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.GRAY).get())
            .getFamily();

    public static final BlockFamilyExtended BLACK_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.BLACK).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.BLACK).get())
            .getFamily();

    public static final BlockFamilyExtended BROWN_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.BROWN).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.BROWN).get())
            .getFamily();

    public static final BlockFamilyExtended RED_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.RED).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.RED).get())
            .getFamily();

    public static final BlockFamilyExtended ORANGE_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.ORANGE).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.ORANGE).get())
            .getFamily();

    public static final BlockFamilyExtended YELLOW_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.YELLOW).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.YELLOW).get())
            .getFamily();

    public static final BlockFamilyExtended LIME_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.LIME).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.LIME).get())
            .getFamily();

    public static final BlockFamilyExtended GREEN_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.GREEN).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.GREEN).get())
            .getFamily();

    public static final BlockFamilyExtended CYAN_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.CYAN).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.CYAN).get())
            .getFamily();

    public static final BlockFamilyExtended LIGHT_BLUE_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.LIGHT_BLUE).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.LIGHT_BLUE).get())
            .getFamily();

    public static final BlockFamilyExtended BLUE_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.BLUE).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.BLUE).get())
            .getFamily();

    public static final BlockFamilyExtended PURPLE_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.PURPLE).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.PURPLE).get())
            .getFamily();

    public static final BlockFamilyExtended MAGENTA_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.MAGENTA).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.MAGENTA).get())
            .getFamily();

    public static final BlockFamilyExtended PINK_CALCITE = familyBuilder(BlockRegistry.CALCITE.get(DyeColor.PINK).get())
            .polished(BlockRegistry.POLISHED_CALCITE.get(DyeColor.PINK).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.WHITE).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.WHITE).get())
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_CALCITE_QUESTION_BLOCK.get())
            .questionBlockTag(BlockRegistry.CALCITE_QUESTION_BLOCK.get())
            .slab(BlockRegistry.POLISHED_WHITE_CALCITE_SLAB.get())
            .stairs(BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS.get())
            .wall(BlockRegistry.POLISHED_WHITE_CALCITE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_LIGHT_GRAY_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.LIGHT_GRAY).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.LIGHT_GRAY).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_GRAY_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.GRAY).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.GRAY).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_BLACK_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.BLACK).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.BLACK).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_BROWN_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.BROWN).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.BROWN).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_RED_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.RED).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.RED).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_ORANGE_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.ORANGE).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.ORANGE).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_YELLOW_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.YELLOW).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.YELLOW).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_LIME_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.LIME).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.LIME).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_GREEN_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.GREEN).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.GREEN).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_CYAN_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.CYAN).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.CYAN).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_LIGHT_BLUE_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.LIGHT_BLUE).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.LIGHT_BLUE).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_BLUE_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.BLUE).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.BLUE).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_PURPLE_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.PURPLE).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.PURPLE).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_MAGENTA_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.MAGENTA).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.MAGENTA).get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_PINK_CALCITE = familyBuilder(BlockRegistry.POLISHED_CALCITE.get(DyeColor.PINK).get())
            .bricks(BlockRegistry.CALCITE_BRICKS.get(DyeColor.PINK).get())
            .getFamily();

    public static final BlockFamilyExtended CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.WHITE).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.WHITE).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.WHITE).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.WHITE).get())
            .slab(BlockRegistry.WHITE_CALCITE_BRICK_SLAB.get())
            .stairs(BlockRegistry.WHITE_CALCITE_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.WHITE).get())
            .wall(BlockRegistry.WHITE_CALCITE_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended LIGHT_GRAY_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.LIGHT_GRAY).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.LIGHT_GRAY).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.LIGHT_GRAY).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.LIGHT_GRAY).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.LIGHT_GRAY).get())
            .getFamily();

    public static final BlockFamilyExtended GRAY_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.GRAY).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.GRAY).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.GRAY).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.GRAY).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.GRAY).get())
            .getFamily();

    public static final BlockFamilyExtended BLACK_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.BLACK).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.BLACK).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.BLACK).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.BLACK).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.BLACK).get())
            .getFamily();

    public static final BlockFamilyExtended BROWN_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.BROWN).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.BROWN).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.BROWN).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.BROWN).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.BROWN).get())
            .getFamily();

    public static final BlockFamilyExtended RED_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.RED).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.RED).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.RED).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.RED).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.RED).get())
            .getFamily();

    public static final BlockFamilyExtended ORANGE_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.ORANGE).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.ORANGE).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.ORANGE).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.ORANGE).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.ORANGE).get())
            .getFamily();

    public static final BlockFamilyExtended YELLOW_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.YELLOW).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.YELLOW).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.YELLOW).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.YELLOW).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.YELLOW).get())
            .getFamily();

    public static final BlockFamilyExtended LIME_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.LIME).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.LIME).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.LIME).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.LIME).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.LIME).get())
            .getFamily();

    public static final BlockFamilyExtended GREEN_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.GREEN).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.GREEN).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.GREEN).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.GREEN).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.GREEN).get())
            .getFamily();

    public static final BlockFamilyExtended CYAN_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.CYAN).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.CYAN).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.CYAN).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.CYAN).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.CYAN).get())
            .getFamily();

    public static final BlockFamilyExtended LIGHT_BLUE_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.LIGHT_BLUE).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.LIGHT_BLUE).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.LIGHT_BLUE).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.LIGHT_BLUE).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.LIGHT_BLUE).get())
            .getFamily();

    public static final BlockFamilyExtended BLUE_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.BLUE).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.BLUE).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.BLUE).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.BLUE).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.BLUE).get())
            .getFamily();

    public static final BlockFamilyExtended PURPLE_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.PURPLE).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.PURPLE).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.PURPLE).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.PURPLE).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.PURPLE).get())
            .getFamily();

    public static final BlockFamilyExtended MAGENTA_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.MAGENTA).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.MAGENTA).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.MAGENTA).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.MAGENTA).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.MAGENTA).get())
            .getFamily();

    public static final BlockFamilyExtended PINK_CALCITE_BRICKS = familyBuilder(BlockRegistry.CALCITE_BRICKS.get(DyeColor.PINK).get())
            .chiseled(BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.PINK).get())
            .cracked(BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.PINK).get())
            .pedestal(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.PINK).get())
            .storageBricks(BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.PINK).get())
            .getFamily();

    public static final BlockFamilyExtended DEEP_FUNGAL_STONE = familyBuilder(BlockRegistry.DEEP_FUNGAL_STONE.get())
            .button(BlockRegistry.DEEP_FUNGAL_STONE_BUTTON.get())
            .cobble(BlockRegistry.DEEP_FUNGAL_COBBLESTONE.get())
            .polished(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
            .pressurePlate(BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE.get())
            .rocky(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE.get())
            .slab(BlockRegistry.DEEP_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.DEEP_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.DEEP_FUNGAL_STONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended ROCKY_DEEP_FUNGAL_STONE = familyBuilder(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE.get())
            .cobble(BlockRegistry.DEEP_FUNGAL_COBBLESTONE.get())
            .polished(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
            .slab(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended DEEP_FUNGAL_COBBLESTONE = familyBuilder(BlockRegistry.DEEP_FUNGAL_COBBLESTONE.get())
            .bricks(BlockRegistry.DEEP_FUNGAL_BRICKS.get())
            .slab(BlockRegistry.DEEP_FUNGAL_COBBLESTONE_SLAB.get())
            .stairs(BlockRegistry.DEEP_FUNGAL_COBBLESTONE_STAIRS.get())
            .wall(BlockRegistry.DEEP_FUNGAL_COBBLESTONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended DEEP_FUNGAL_BRICKS = familyBuilder(BlockRegistry.DEEP_FUNGAL_BRICKS.get())
            .chiseled(BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS.get())
            .cracked(BlockRegistry.CRACKED_DEEP_FUNGAL_BRICKS.get())
            .pedestal(BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL.get())
            .slab(BlockRegistry.DEEP_FUNGAL_BRICK_SLAB.get())
            .stairs(BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_DEEP_FUNGAL_BRICKS.get())
            .wall(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_DEEP_FUNGAL_STONE = familyBuilder(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
            .bricks(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS.get())
            .hardBlock(BlockRegistry.HARD_DEEP_FUNGAL_BLOCK.get())
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK.get())
            .questionPanel(BlockRegistry.DEEP_FUNGAL_QUESTION_PANEL.get())
            .slab(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_DEEP_FUNGAL_BRICKS = familyBuilder(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS.get())
            .chiseled(BlockRegistry.CHISELED_POLISHED_DEEP_FUNGAL_BRICKS.get())
            .cracked(BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS.get())
            .pedestal(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL.get())
            .slab(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB.get())
            .stairs(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_POLISHED_DEEP_FUNGAL_BRICKS.get())
            .wall(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended HARD_DEEP_FUNGAL_BLOCK = familyBuilder(BlockRegistry.HARD_DEEP_FUNGAL_BLOCK.get())
            .slab(BlockRegistry.HARD_DEEP_FUNGAL_SLAB.get())
            .stairs(BlockRegistry.HARD_DEEP_FUNGAL_STAIRS.get())
            .wall(BlockRegistry.HARD_DEEP_FUNGAL_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended FUNGAL_STONE = familyBuilder(BlockRegistry.FUNGAL_STONE.get())
            .button(BlockRegistry.FUNGAL_STONE_BUTTON.get())
            .cobble(BlockRegistry.FUNGAL_COBBLESTONE.get())
            .polished(BlockRegistry.POLISHED_FUNGAL_STONE.get())
            .pressurePlate(BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE.get())
            .rocky(BlockRegistry.ROCKY_FUNGAL_STONE.get())
            .slab(BlockRegistry.FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.FUNGAL_STONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended ROCKY_FUNGAL_STONE = familyBuilder(BlockRegistry.ROCKY_FUNGAL_STONE.get())
            .cobble(BlockRegistry.FUNGAL_COBBLESTONE.get())
            .polished(BlockRegistry.POLISHED_FUNGAL_STONE.get())
            .slab(BlockRegistry.ROCKY_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.ROCKY_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.ROCKY_FUNGAL_STONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended FUNGAL_COBBLESTONE = familyBuilder(BlockRegistry.FUNGAL_COBBLESTONE.get())
            .bricks(BlockRegistry.FUNGAL_BRICKS.get())
            .slab(BlockRegistry.FUNGAL_COBBLESTONE_SLAB.get())
            .stairs(BlockRegistry.FUNGAL_COBBLESTONE_STAIRS.get())
            .wall(BlockRegistry.FUNGAL_COBBLESTONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended FUNGAL_BRICKS = familyBuilder(BlockRegistry.FUNGAL_BRICKS.get())
            .chiseled(BlockRegistry.CHISELED_FUNGAL_BRICKS.get())
            .cracked(BlockRegistry.CRACKED_FUNGAL_BRICKS.get())
            .pedestal(BlockRegistry.FUNGAL_BRICK_PEDESTAL.get())
            .slab(BlockRegistry.FUNGAL_BRICK_SLAB.get())
            .stairs(BlockRegistry.FUNGAL_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_FUNGAL_BRICKS.get())
            .wall(BlockRegistry.FUNGAL_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_FUNGAL_STONE = familyBuilder(BlockRegistry.POLISHED_FUNGAL_STONE.get())
            .bricks(BlockRegistry.POLISHED_FUNGAL_BRICKS.get())
            .hardBlock(BlockRegistry.HARD_FUNGAL_BLOCK.get())
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.FUNGAL_QUESTION_BLOCK.get())
            .questionPanel(BlockRegistry.FUNGAL_QUESTION_PANEL.get())
            .slab(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_FUNGAL_BRICKS = familyBuilder(BlockRegistry.POLISHED_FUNGAL_BRICKS.get())
            .chiseled(BlockRegistry.CHISELED_POLISHED_FUNGAL_BRICKS.get())
            .cracked(BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS.get())
            .pedestal(BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL.get())
            .slab(BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB.get())
            .stairs(BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_POLISHED_FUNGAL_BRICKS.get())
            .wall(BlockRegistry.POLISHED_FUNGAL_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended HARD_FUNGAL_BLOCK = familyBuilder(BlockRegistry.HARD_FUNGAL_BLOCK.get())
            .slab(BlockRegistry.HARD_FUNGAL_SLAB.get())
            .stairs(BlockRegistry.HARD_FUNGAL_STAIRS.get())
            .wall(BlockRegistry.HARD_FUNGAL_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended SAND = familyBuilder(Blocks.SAND)
            .quicksand(BlockRegistry.QUICKSAND.get())
            .getFamily();

    public static final BlockFamilyExtended CUT_SANDSTONE = familyBuilder(Blocks.CUT_SANDSTONE)
            .bricks(BlockRegistry.SANDSTONE_BRICKS.get())
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_SANDSTONE_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.SANDSTONE_QUESTION_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended SANDSTONE_BRICKS = familyBuilder(BlockRegistry.SANDSTONE_BRICKS.get())
            .chiseled(BlockRegistry.CHISELED_SANDSTONE_BRICKS.get())
            .cracked(BlockRegistry.CRACKED_SANDSTONE_BRICKS.get())
            .pedestal(BlockRegistry.SANDSTONE_BRICK_PEDESTAL.get())
            .slab(BlockRegistry.SANDSTONE_BRICK_SLAB.get())
            .stairs(BlockRegistry.SANDSTONE_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_SANDSTONE_BRICKS.get())
            .wall(BlockRegistry.SANDSTONE_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended CUT_RED_SANDSTONE = familyBuilder(Blocks.CUT_RED_SANDSTONE)
            .bricks(BlockRegistry.RED_SANDSTONE_BRICKS.get())
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.RED_SANDSTONE_QUESTION_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended RED_SAND = familyBuilder(Blocks.RED_SAND)
            .quicksand(BlockRegistry.RED_QUICKSAND.get())
            .getFamily();

    public static final BlockFamilyExtended RED_SANDSTONE_BRICKS = familyBuilder(BlockRegistry.RED_SANDSTONE_BRICKS.get())
            .chiseled(BlockRegistry.CHISELED_RED_SANDSTONE_BRICKS.get())
            .cracked(BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS.get())
            .pedestal(BlockRegistry.RED_SANDSTONE_BRICK_PEDESTAL.get())
            .slab(BlockRegistry.RED_SANDSTONE_BRICK_SLAB.get())
            .stairs(BlockRegistry.RED_SANDSTONE_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_RED_SANDSTONE_BRICKS.get())
            .wall(BlockRegistry.RED_SANDSTONE_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended BLACKSTONE_BRICKS = familyBuilder(Blocks.POLISHED_BLACKSTONE_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.BLACKSTONE_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.BLACKSTONE_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_BLACKSTONE_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended BRICKS = familyBuilder(Blocks.BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended DARK_PRISMARINE = familyBuilder(Blocks.DARK_PRISMARINE)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK.get())
            .pedestal(BlockRegistry.DARK_PRISMARINE_PEDESTAL.get())
            .questionBlock(BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK.get())
            .smashableBlock(BlockRegistry.SMASHABLE_DARK_PRISMARINE.get())
            .storageBricks(BlockRegistry.STORAGE_DARK_PRISMARINE.get())
            .getFamily();

    public static final BlockFamilyExtended DEEPSLATE_BRICKS = familyBuilder(Blocks.DEEPSLATE_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.DEEPSLATE_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.DEEPSLATE_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_DEEPSLATE_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended DEEPSLATE_TILES = familyBuilder(Blocks.DEEPSLATE_TILES)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES.get())
            .pedestal(BlockRegistry.DEEPSLATE_TILE_PEDESTAL.get())
            .questionBlock(BlockRegistry.DEEPSLATE_QUESTION_TILES.get())
            .smashableBlock(BlockRegistry.SMASHABLE_DEEPSLATE_TILES.get())
            .storageBricks(BlockRegistry.STORAGE_DEEPSLATE_TILES.get())
            .getFamily();

    public static final BlockFamilyExtended END_STONE_BRICKS = familyBuilder(Blocks.END_STONE_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.END_STONE_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.END_STONE_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_END_STONE_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_END_STONE_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended MOSSY_STONE_BRICKS = familyBuilder(Blocks.MOSSY_STONE_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.MOSSY_STONE_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_MOSSY_STONE_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended MUD_BRICKS = familyBuilder(Blocks.MUD_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.MUD_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.MUD_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_MUD_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_MUD_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended NETHER_BRICKS = familyBuilder(Blocks.NETHER_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.NETHER_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.NETHER_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_NETHER_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_NETHER_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended PRISMARINE_BRICKS = familyBuilder(Blocks.PRISMARINE_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.PRISMARINE_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.PRISMARINE_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_PRISMARINE_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_PRISMARINE_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended PURPUR_BLOCK = familyBuilder(Blocks.PURPUR_BLOCK)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK.get())
            .pedestal(BlockRegistry.PURPUR_BLOCK_PEDESTAL.get())
            .questionBlock(BlockRegistry.PURPUR_QUESTION_BLOCK.get())
            .smashableBlock(BlockRegistry.SMASHABLE_PURPUR_BLOCK.get())
            .storageBricks(BlockRegistry.STORAGE_PURPUR_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended QUARTZ_BRICKS = familyBuilder(Blocks.QUARTZ_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.QUARTZ_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.QUARTZ_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_QUARTZ_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_QUARTZ_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended RED_NETHER_BRICKS = familyBuilder(Blocks.RED_NETHER_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.RED_NETHER_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.RED_NETHER_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_RED_NETHER_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_RED_NETHER_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended STONE_BRICKS = familyBuilder(Blocks.STONE_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.STONE_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.STONE_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_STONE_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_STONE_BRICKS.get())
            .getFamily();

    public static final BlockFamilyExtended TUFF_BRICKS = familyBuilder(Blocks.TUFF_BRICKS)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS.get())
            .pedestal(BlockRegistry.TUFF_BRICK_PEDESTAL.get())
            .questionBlock(BlockRegistry.TUFF_QUESTION_BRICKS.get())
            .smashableBlock(BlockRegistry.SMASHABLE_TUFF_BRICKS.get())
            .storageBricks(BlockRegistry.STORAGE_TUFF_BRICKS.get())
            .getFamily();
    
    public static final BlockFamilyExtended COPPER_BLOCK = familyBuilder(Blocks.COPPER_BLOCK)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.COPPER_QUESTION_BLOCK.get())
            .getFamily();
    
    public static final BlockFamilyExtended CUT_COPPER = familyBuilder(Blocks.CUT_COPPER)
            .pedestal(BlockRegistry.CUT_COPPER_PEDESTAL.get())
            .smashableBlock(BlockRegistry.SMASHABLE_CUT_COPPER.get())
            .storageBricks(BlockRegistry.STORAGE_CUT_COPPER.get())
            .getFamily();
    
    public static final BlockFamilyExtended WAXED_COPPER_BLOCK = familyBuilder(Blocks.WAXED_COPPER_BLOCK)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.WAXED_COPPER_QUESTION_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended WAXED_CUT_COPPER = familyBuilder(Blocks.WAXED_CUT_COPPER)
            .pedestal(BlockRegistry.WAXED_CUT_COPPER_PEDESTAL.get())
            .smashableBlock(BlockRegistry.SMASHABLE_WAXED_CUT_COPPER.get())
            .storageBricks(BlockRegistry.STORAGE_WAXED_CUT_COPPER.get())
            .getFamily();

    public static final BlockFamilyExtended EXPOSED_COPPER = familyBuilder(Blocks.EXPOSED_COPPER)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended EXPOSED_CUT_COPPER = familyBuilder(Blocks.EXPOSED_CUT_COPPER)
            .pedestal(BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL.get())
            .smashableBlock(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER.get())
            .storageBricks(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER.get())
            .getFamily();

    public static final BlockFamilyExtended WAXED_EXPOSED_COPPER = familyBuilder(Blocks.WAXED_EXPOSED_COPPER)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended WAXED_EXPOSED_CUT_COPPER = familyBuilder(Blocks.WAXED_EXPOSED_CUT_COPPER)
            .pedestal(BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL.get())
            .smashableBlock(BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER.get())
            .storageBricks(BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER.get())
            .getFamily();

    public static final BlockFamilyExtended WEATHERED_COPPER = familyBuilder(Blocks.WEATHERED_COPPER)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended WEATHERED_CUT_COPPER = familyBuilder(Blocks.WEATHERED_CUT_COPPER)
            .pedestal(BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL.get())
            .smashableBlock(BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER.get())
            .storageBricks(BlockRegistry.STORAGE_WEATHERED_CUT_COPPER.get())
            .getFamily();

    public static final BlockFamilyExtended WAXED_WEATHERED_COPPER = familyBuilder(Blocks.WAXED_WEATHERED_COPPER)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended WAXED_WEATHERED_CUT_COPPER = familyBuilder(Blocks.WAXED_WEATHERED_CUT_COPPER)
            .pedestal(BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL.get())
            .smashableBlock(BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER.get())
            .storageBricks(BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER.get())
            .getFamily();

    public static final BlockFamilyExtended OXIDIZED_COPPER = familyBuilder(Blocks.OXIDIZED_COPPER)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended OXIDIZED_CUT_COPPER = familyBuilder(Blocks.OXIDIZED_CUT_COPPER)
            .pedestal(BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL.get())
            .smashableBlock(BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER.get())
            .storageBricks(BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER.get())
            .getFamily();

    public static final BlockFamilyExtended WAXED_OXIDIZED_COPPER = familyBuilder(Blocks.WAXED_OXIDIZED_COPPER)
            .invisibleQuestionBlock(BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK.get())
            .questionBlock(BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK.get())
            .getFamily();

    public static final BlockFamilyExtended WAXED_OXIDIZED_CUT_COPPER = familyBuilder(Blocks.WAXED_OXIDIZED_CUT_COPPER)
            .pedestal(BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL.get())
            .smashableBlock(BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER.get())
            .storageBricks(BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER.get())
            .getFamily();


    private static BlockFamilyExtended.Builder familyBuilder(Block block) {
        BlockFamilyExtended.Builder builder = new BlockFamilyExtended.Builder(block);
        BlockFamilyExtended BlockFamilyExtended = MAP.put(block, builder.getFamily());
        if (BlockFamilyExtended != null) {
            throw new IllegalStateException("Duplicate family definition for " + BuiltInRegistries.BLOCK.getKey(block));
        } else {
            return builder;
        }
    }

    @NotNull
    public static Stream<BlockFamilyExtended> getAllExtendedFamilies() {
        return MAP.values().stream();
    }
}
