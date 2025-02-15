package com.wenxin2.marioverse.init;

import com.google.common.collect.Maps;
import com.wenxin2.marioverse.Marioverse;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class BlockFamiliesRegistry extends BlockFamilies {
    private static final Map<Block, BlockFamily> MAP = Maps.newHashMap();

    public static final Map<BlockFamily.Variant, Integer> STONECUTTING_OUTPUTS = Map.of(
            BlockFamily.Variant.SLAB, 2,
            BlockFamily.Variant.STAIRS, 1,
            BlockFamily.Variant.WALL, 1,
            BlockFamily.Variant.CHISELED, 1,
            BlockFamily.Variant.CUT, 1,
            BlockFamily.Variant.POLISHED, 1
    );

    public static final BlockFamily AMETHYST = familyBuilder(Blocks.AMETHYST_BLOCK)
            .button(BlockRegistry.AMETHYST_BUTTON.get())
            .polished(BlockRegistry.POLISHED_AMETHYST.get())
            .pressurePlate(BlockRegistry.AMETHYST_PRESSURE_PLATE.get())
            .slab(BlockRegistry.AMETHYST_SLAB.get())
            .stairs(BlockRegistry.AMETHYST_STAIRS.get())
            .wall(BlockRegistry.AMETHYST_WALL.get())
            .recipeGroupPrefix(Marioverse.MOD_ID + ":amethyst")
            .getFamily();

    public static final BlockFamily AMETHYST_BRICKS = familyBuilder(BlockRegistry.AMETHYST_BRICKS.get())
            .chiseled(BlockRegistry.CHISELED_AMETHYST_BRICKS.get())
            .cracked(BlockRegistry.CRACKED_AMETHYST_BRICKS.get())
            .slab(BlockRegistry.AMETHYST_BRICK_SLAB.get())
            .stairs(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
            .wall(BlockRegistry.AMETHYST_BRICK_WALL.get())
            .recipeGroupPrefix(Marioverse.MOD_ID + ":amethyst_brick")
            .getFamily();

    public static final BlockFamily POLISHED_AMETHYST = familyBuilder(BlockRegistry.POLISHED_AMETHYST.get())
            .slab(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
            .stairs(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
            .wall(BlockRegistry.POLISHED_AMETHYST_WALL.get())
            .recipeGroupPrefix(Marioverse.MOD_ID + ":polished_amethyst")
            .getFamily();

    public static final BlockFamily DEEP_FUNGAL_STONE = familyBuilder(BlockRegistry.DEEP_FUNGAL_STONE.get())
            .button(BlockRegistry.DEEP_FUNGAL_STONE_BUTTON.get())
            .polished(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
            .pressurePlate(BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE.get())
            .slab(BlockRegistry.DEEP_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.DEEP_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.DEEP_FUNGAL_STONE_WALL.get())
            .recipeGroupPrefix(Marioverse.MOD_ID + ":deep_fungal_stone")
            .getFamily();

    public static final BlockFamily DEEP_FUNGAL_BRICKS = familyBuilder(BlockRegistry.DEEP_FUNGAL_BRICKS.get())
            .slab(BlockRegistry.DEEP_FUNGAL_BRICK_SLAB.get())
            .stairs(BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS.get())
            .wall(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
            .recipeGroupPrefix(Marioverse.MOD_ID + ":deep_fungal_brick")
            .getFamily();

    public static final BlockFamily POLISHED_DEEP_FUNGAL_STONE = familyBuilder(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
            .slab(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
            .recipeGroupPrefix(Marioverse.MOD_ID + ":polished_deep_fungal_stone")
            .getFamily();

    public static final BlockFamily FUNGAL_STONE = familyBuilder(BlockRegistry.FUNGAL_STONE.get())
            .button(BlockRegistry.FUNGAL_STONE_BUTTON.get())
            .polished(BlockRegistry.POLISHED_FUNGAL_STONE.get())
            .pressurePlate(BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE.get())
            .slab(BlockRegistry.FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.FUNGAL_STONE_WALL.get())
            .recipeGroupPrefix(Marioverse.MOD_ID + ":fungal_stone")
            .getFamily();

    public static final BlockFamily FUNGAL_BRICKS = familyBuilder(BlockRegistry.FUNGAL_BRICKS.get())
            .slab(BlockRegistry.FUNGAL_BRICK_SLAB.get())
            .stairs(BlockRegistry.FUNGAL_BRICK_STAIRS.get())
            .wall(BlockRegistry.FUNGAL_BRICK_WALL.get())
            .recipeGroupPrefix(Marioverse.MOD_ID + ":fungal_brick")
            .getFamily();

    public static final BlockFamily POLISHED_FUNGAL_STONE = familyBuilder(BlockRegistry.POLISHED_FUNGAL_STONE.get())
            .slab(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get())
            .stairs(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get())
            .wall(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get())
            .recipeGroupPrefix(Marioverse.MOD_ID + ":polished_fungal_stone")
            .getFamily();


    private static BlockFamily.Builder familyBuilder(Block block) {
        BlockFamily.Builder builder = new BlockFamily.Builder(block);
        BlockFamily blockfamily = MAP.put(block, builder.getFamily());
        if (blockfamily != null) {
            throw new IllegalStateException("Duplicate family definition for " + BuiltInRegistries.BLOCK.getKey(block));
        } else {
            return builder;
        }
    }

    @NotNull
    public static Stream<BlockFamily> getAllFamilies() {
        return MAP.values().stream();
    }
}
