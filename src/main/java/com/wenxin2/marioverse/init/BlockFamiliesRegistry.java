package com.wenxin2.marioverse.init;

import com.google.common.collect.Maps;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class BlockFamiliesRegistry extends BlockFamilies {
    private static final Map<Block, BlockFamilyExtended> MAP = Maps.newHashMap();

    public static final Map<BlockFamilyExtended.Variant, Integer> STONECUTTING_OUTPUTS = Map.of(
            BlockFamilyExtended.Variant.BRICKS, 1,
            BlockFamilyExtended.Variant.CHISELED, 1,
            BlockFamilyExtended.Variant.CUT, 1,
            BlockFamilyExtended.Variant.PEDESTAL, 1,
            BlockFamilyExtended.Variant.POLISHED, 1,
            BlockFamilyExtended.Variant.SLAB, 2,
            BlockFamilyExtended.Variant.STAIRS, 1,
            BlockFamilyExtended.Variant.WALL, 1
    );

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
            .questionBlock(BlockRegistry.AMETHYST_QUESTION_BLOCK.get())
            .slab(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
            .stairs(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
            .wall(BlockRegistry.POLISHED_AMETHYST_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended DEEP_FUNGAL_STONE = familyBuilder(BlockRegistry.DEEP_FUNGAL_STONE.get())
            .button(BlockRegistry.DEEP_FUNGAL_STONE_BUTTON.get())
            .polished(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
            .pressurePlate(BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE.get())
            .slab(BlockRegistry.DEEP_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.DEEP_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.DEEP_FUNGAL_STONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended DEEP_FUNGAL_BRICKS = familyBuilder(BlockRegistry.DEEP_FUNGAL_BRICKS.get())
            .pedestal(BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL.get())
            .slab(BlockRegistry.DEEP_FUNGAL_BRICK_SLAB.get())
            .stairs(BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_DEEP_FUNGAL_BRICKS.get())
            .wall(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_DEEP_FUNGAL_STONE = familyBuilder(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
            .bricks(BlockRegistry.DEEP_FUNGAL_BRICKS.get())
            .questionBlock(BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK.get())
            .slab(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended FUNGAL_STONE = familyBuilder(BlockRegistry.FUNGAL_STONE.get())
            .button(BlockRegistry.FUNGAL_STONE_BUTTON.get())
            .polished(BlockRegistry.POLISHED_FUNGAL_STONE.get())
            .pressurePlate(BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE.get())
            .slab(BlockRegistry.FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.FUNGAL_STONE_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended FUNGAL_BRICKS = familyBuilder(BlockRegistry.FUNGAL_BRICKS.get())
            .pedestal(BlockRegistry.FUNGAL_BRICK_PEDESTAL.get())
            .slab(BlockRegistry.FUNGAL_BRICK_SLAB.get())
            .stairs(BlockRegistry.FUNGAL_BRICK_STAIRS.get())
            .storageBricks(BlockRegistry.STORAGE_FUNGAL_BRICKS.get())
            .wall(BlockRegistry.FUNGAL_BRICK_WALL.get())
            .getFamily();

    public static final BlockFamilyExtended POLISHED_FUNGAL_STONE = familyBuilder(BlockRegistry.POLISHED_FUNGAL_STONE.get())
            .bricks(BlockRegistry.FUNGAL_BRICKS.get())
            .questionBlock(BlockRegistry.FUNGAL_QUESTION_BLOCK.get())
            .slab(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get())
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
