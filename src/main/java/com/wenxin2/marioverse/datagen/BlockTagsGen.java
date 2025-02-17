package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.init.BlockFamiliesRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockTagsGen extends BlockTagsProvider {
    public BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider lookupProvider) {
        BlockRegistry.GOAL_POLES.values().forEach(block -> tag(TagRegistry.DYEABLE_GOAL_POLE_BLOCKS).add(block.get()));
        BlockRegistry.WARP_PIPES.values().forEach(block -> tag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS).add(block.get()));

        for (DyeColor color : DyeColor.values()) {
            tag(TagRegistry.blockTags("c", "dyed/" + color))
                    .add(BlockRegistry.GOAL_POLES.get(color).get())
                    .add(BlockRegistry.WARP_PIPES.get(color).get());
        }

        BlockFamiliesRegistry.getAllExtendedFamilies().forEach(family -> {
            family.getVariants().forEach((variant, block) -> {
                if (variant == BlockFamilyExtended.Variant.INVISIBLE_QUESTION_BLOCK)
                    tag(TagRegistry.INVISIBLE_QUESTION_BLOCKS).add(block);
                if (variant == BlockFamilyExtended.Variant.PEDESTAL)
                    tag(TagRegistry.BRICK_PEDESTAL_BLOCKS).add(block);
                if (variant == BlockFamilyExtended.Variant.QUESTION_BLOCK)
                    tag(TagRegistry.QUESTION_BLOCKS).add(block);
                if (variant == BlockFamilyExtended.Variant.SMASHABLE_BLOCKS)
                    tag(TagRegistry.SMASHABLE_BLOCKS).add(block);
                if (variant == BlockFamilyExtended.Variant.STORAGE_BRICKS)
                    tag(TagRegistry.STORAGE_BRICK_BLOCKS).add(block);
            });
        });

        tag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.DYEABLE_GOAL_POLE_BLOCKS)
                .add(BlockRegistry.CLASSIC_GOAL_POLE.get());

        tag(TagRegistry.WARP_PIPE_BLOCKS)
                .addTag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS)
                .add(BlockRegistry.CLEAR_WARP_PIPE.get());

        tag(TagRegistry.BONKABLE_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .add(BlockRegistry.POLISHED_AMETHYST.get())
                .add(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
                .add(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
                .add(BlockRegistry.POLISHED_AMETHYST_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get());

        tag(TagRegistry.SMASHABLE_BLOCKS)
                .add(BlockRegistry.AMETHYST_BRICKS.get())
                .add(BlockRegistry.AMETHYST_BRICK_PEDESTAL.get())
                .add(BlockRegistry.AMETHYST_BRICK_SLAB.get())
                .add(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
                .add(BlockRegistry.AMETHYST_BRICK_WALL.get())
                .add(BlockRegistry.CHISELED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.CRACKED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.FUNGAL_BRICKS.get())
                .add(BlockRegistry.FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.FUNGAL_BRICK_WALL.get())
                .replace(false);

        tag(Tags.Blocks.STONES)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.FUNGAL_STONE.get());

        tag(BlockTags.ANCIENT_CITY_REPLACEABLE)
                .add(BlockRegistry.DEEPSLATE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.DEEPSLATE_TILE_PEDESTAL.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_TILES.get());

        tag(BlockTags.BASE_STONE_OVERWORLD)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.FUNGAL_STONE.get());

        tag(BlockTags.CRYSTAL_SOUND_BLOCKS)
                .add(BlockRegistry.AMETHYST_BRICKS.get())
                .add(BlockRegistry.AMETHYST_BRICK_SLAB.get())
                .add(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
                .add(BlockRegistry.AMETHYST_BRICK_WALL.get())
                .add(BlockRegistry.AMETHYST_BUTTON.get())
                .add(BlockRegistry.AMETHYST_PRESSURE_PLATE.get())
                .add(BlockRegistry.AMETHYST_QUESTION_BLOCK.get())
                .add(BlockRegistry.AMETHYST_SLAB.get())
                .add(BlockRegistry.AMETHYST_STAIRS.get())
                .add(BlockRegistry.AMETHYST_WALL.get())
                .add(BlockRegistry.CHISELED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.CRACKED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK.get())
                .add(BlockRegistry.POLISHED_AMETHYST.get())
                .add(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
                .add(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
                .add(BlockRegistry.POLISHED_AMETHYST_WALL.get())
                .add(BlockRegistry.STORAGE_AMETHYST_BRICKS.get());

        tag(BlockTags.FEATURES_CANNOT_REPLACE)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS);

        tag(BlockTags.GUARDED_BY_PIGLINS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .add(BlockRegistry.COIN.get());

        tag(BlockTags.IMPERMEABLE)
                .add(BlockRegistry.CLEAR_WARP_PIPE.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS);

        tag(BlockTags.SCULK_REPLACEABLE_WORLD_GEN)
                .add(BlockRegistry.DEEPSLATE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.DEEPSLATE_TILE_PEDESTAL.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_TILES.get());

        tag(BlockTags.SLABS)
                .add(BlockRegistry.AMETHYST_BRICK_SLAB.get())
                .add(BlockRegistry.AMETHYST_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get());

        tag(BlockTags.STAIRS)
                .add(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
                .add(BlockRegistry.AMETHYST_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get());

        tag(BlockTags.STONE_BUTTONS)
                .add(BlockRegistry.AMETHYST_BUTTON.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_BUTTON.get())
                .add(BlockRegistry.FUNGAL_STONE_BUTTON.get());

        tag(BlockTags.STONE_PRESSURE_PLATES)
                .add(BlockRegistry.AMETHYST_PRESSURE_PLATE.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE.get())
                .add(BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE.get());

        tag(BlockTags.WALLS)
                .add(BlockRegistry.AMETHYST_BRICK_WALL.get())
                .add(BlockRegistry.AMETHYST_WALL.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_AMETHYST_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(TagRegistry.BRICK_PEDESTAL_BLOCKS)
                .addTag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS)
                .add(BlockRegistry.AMETHYST_BRICK_SLAB.get())
                .add(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
                .add(BlockRegistry.AMETHYST_BRICK_WALL.get())
                .add(BlockRegistry.AMETHYST_BRICKS.get())
                .add(BlockRegistry.AMETHYST_BUTTON.get())
                .add(BlockRegistry.AMETHYST_PRESSURE_PLATE.get())
                .add(BlockRegistry.AMETHYST_SLAB.get())
                .add(BlockRegistry.AMETHYST_STAIRS.get())
                .add(BlockRegistry.AMETHYST_WALL.get())
                .add(BlockRegistry.CHISELED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.COIN.get())
                .add(BlockRegistry.CRACKED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_BUTTON.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.FUNGAL_STONE.get())
                .add(BlockRegistry.FUNGAL_STONE_BUTTON.get())
                .add(BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE.get())
                .add(BlockRegistry.FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_AMETHYST.get())
                .add(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
                .add(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
                .add(BlockRegistry.POLISHED_AMETHYST_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get())
                .replace(false);
    }
}