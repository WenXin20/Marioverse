package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemTagsGen extends ItemTagsProvider {
    public  ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                       CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        copy(TagRegistry.BONKABLE_BLOCKS, TagRegistry.BONKABLE_BLOCK_ITEMS);
        copy(TagRegistry.BRICK_PEDESTAL_BLOCKS, TagRegistry.BRICK_PEDESTAL_ITEMS);
        copy(TagRegistry.CHECKPOINT_FLAG_BLOCKS, TagRegistry.CHECKPOINT_FLAG_ITEMS);
        copy(TagRegistry.DYEABLE_CHECKPOINT_FLAG_BLOCKS, TagRegistry.DYEABLE_CHECKPOINT_FLAG_ITEMS);
        copy(TagRegistry.DYEABLE_GOAL_POLE_BLOCKS, TagRegistry.DYEABLE_GOAL_POLE_ITEMS);
        copy(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS, TagRegistry.DYEABLE_WARP_PIPE_ITEMS);
        copy(TagRegistry.GOAL_POLE_BLOCKS, TagRegistry.GOAL_POLE_ITEMS);
        copy(TagRegistry.INVISIBLE_QUESTION_BLOCKS, TagRegistry.INVISIBLE_QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.QUESTION_BLOCKS, TagRegistry.QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.SMASHABLE_BLOCKS, TagRegistry.SMASHABLE_BLOCK_ITEMS);
        copy(TagRegistry.STORAGE_BRICK_BLOCKS, TagRegistry.STORAGE_BRICK_ITEMS);
        copy(TagRegistry.WARP_PIPE_BLOCKS, TagRegistry.WARP_PIPE_ITEMS);

        copy(Tags.Blocks.STONES, Tags.Items.STONES);

        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.STONE_BUTTONS, ItemTags.STONE_BUTTONS);
        copy(BlockTags.WALLS, ItemTags.WALLS);

        for (DyeColor color : DyeColor.values()) {
            tag(TagRegistry.itemTags("c", "dyed/" + color))
                    .add(BlockRegistry.CHECKPOINT_FLAGS.get(color).asItem())
                    .add(BlockRegistry.GOAL_POLES.get(color).asItem())
                    .add(BlockRegistry.WARP_PIPES.get(color).asItem());
        }

        tag(TagRegistry.CANNOT_PLACE_IN_CHECKPOINT_FLAGS);

        tag(TagRegistry.CANNOT_PLACE_IN_QUESTION_BLOCKS);

        tag(TagRegistry.MARIO_COSTUMES)
                .add(ItemRegistry.FIRE_HAT.get())
                .add(ItemRegistry.FIRE_PANTS.get())
                .add(ItemRegistry.FIRE_SHIRT.get())
                .add(ItemRegistry.FIRE_SHOES.get())
                .add(ItemRegistry.ICE_HAT.get())
                .add(ItemRegistry.ICE_PANTS.get())
                .add(ItemRegistry.ICE_SHIRT.get())
                .add(ItemRegistry.ICE_SHOES.get())
                .add(ItemRegistry.MARIO_HAT.get())
                .add(ItemRegistry.MARIO_PANTS.get())
                .add(ItemRegistry.MARIO_SHIRT.get())
                .add(ItemRegistry.MARIO_SHOES.get());

        tag(TagRegistry.MARIO_POWER_UP_COSTUMES)
                .add(ItemRegistry.FIRE_HAT.get())
                .add(ItemRegistry.FIRE_PANTS.get())
                .add(ItemRegistry.FIRE_SHIRT.get())
                .add(ItemRegistry.FIRE_SHOES.get())
                .add(ItemRegistry.ICE_HAT.get())
                .add(ItemRegistry.ICE_PANTS.get())
                .add(ItemRegistry.ICE_SHIRT.get())
                .add(ItemRegistry.ICE_SHOES.get());

        tag(TagRegistry.POWER_UP_COSTUMES)
                .add(ItemRegistry.FIRE_HAT.get())
                .add(ItemRegistry.FIRE_PANTS.get())
                .add(ItemRegistry.FIRE_SHIRT.get())
                .add(ItemRegistry.FIRE_SHOES.get())
                .add(ItemRegistry.ICE_HAT.get())
                .add(ItemRegistry.ICE_PANTS.get())
                .add(ItemRegistry.ICE_SHIRT.get())
                .add(ItemRegistry.ICE_SHOES.get())
                .add(ItemRegistry.MARIO_HAT.get())
                .add(ItemRegistry.MARIO_PANTS.get())
                .add(ItemRegistry.MARIO_SHIRT.get())
                .add(ItemRegistry.MARIO_SHOES.get());

        tag(TagRegistry.POWER_UP_ITEMS)
                .add(ItemRegistry.FIRE_FLOWER.get())
                .add(ItemRegistry.ICE_FLOWER.get())
                .add(ItemRegistry.MUSHROOM.get())
                .add(ItemRegistry.ONE_UP_MUSHROOM.get())
                .add(ItemRegistry.SUPER_STAR.get());

        tag(TagRegistry.WARP_PIPE_CANNOT_SPAWN_ITEMS)
                .addTag(TagRegistry.WARP_PIPE_ITEMS)
                .add(ItemRegistry.PIPE_WRENCH.get())
                .add(ItemRegistry.WARP_DISRUPTOR.get());

        tag(ItemTags.CHEST_ARMOR)
                .add(ItemRegistry.FIRE_SHIRT.get())
                .add(ItemRegistry.ICE_SHIRT.get())
                .add(ItemRegistry.MARIO_SHIRT.get());

        tag(ItemTags.DECORATED_POT_SHERDS)
                .add(ItemRegistry.BOWSER_POTTERY_SHERD.get())
                .add(ItemRegistry.PLUMBER_POTTERY_SHERD.get());

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ItemRegistry.PIPE_WRENCH.get());

        tag(ItemTags.FOOT_ARMOR)
                .add(ItemRegistry.FIRE_SHOES.get())
                .add(ItemRegistry.ICE_SHOES.get())
                .add(ItemRegistry.MARIO_SHOES.get());

        tag(ItemTags.FREEZE_IMMUNE_WEARABLES)
                .add(ItemRegistry.ICE_HAT.get())
                .add(ItemRegistry.ICE_PANTS.get())
                .add(ItemRegistry.ICE_SHIRT.get())
                .add(ItemRegistry.ICE_SHOES.get());

        tag(ItemTags.HEAD_ARMOR)
                .add(ItemRegistry.FIRE_HAT.get())
                .add(ItemRegistry.ICE_HAT.get())
                .add(ItemRegistry.MARIO_HAT.get());

        tag(ItemTags.LEG_ARMOR)
                .add(ItemRegistry.FIRE_PANTS.get())
                .add(ItemRegistry.ICE_PANTS.get())
                .add(ItemRegistry.MARIO_PANTS.get());

        tag(ItemTags.PIGLIN_LOVED)
                .add(BlockRegistry.COIN.asItem())
                .add(BlockRegistry.FUNGAL_QUESTION_BLOCK.asItem())
                .add(BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.asItem())
                .add(BlockRegistry.STAR_COIN.asItem())
                .add(ItemRegistry.SUPER_STAR.get());

        tag(ItemTags.STONE_CRAFTING_MATERIALS)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.asItem())
                .add(BlockRegistry.FUNGAL_STONE.asItem());

        tag(ItemTags.STONE_TOOL_MATERIALS)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.asItem())
                .add(BlockRegistry.FUNGAL_STONE.asItem());

        tag(ItemTags.TRIMMABLE_ARMOR);
    }
}