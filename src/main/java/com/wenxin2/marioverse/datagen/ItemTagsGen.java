package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemTagsGen extends ItemTagsProvider {
    public ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                       CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        copy(TagRegistry.BRICK_PEDESTAL_BLOCKS, TagRegistry.BRICK_PEDESTAL_ITEMS);
        copy(TagRegistry.INVISIBLE_QUESTION_BLOCKS, TagRegistry.INVISIBLE_QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.QUESTION_BLOCKS, TagRegistry.QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.SMASHABLE_BLOCKS, TagRegistry.SMASHABLE_BLOCK_ITEMS);
        copy(TagRegistry.STORAGE_BRICK_BLOCKS, TagRegistry.STORAGE_BRICK_ITEMS);
        copy(TagRegistry.BONKABLE_BLOCKS, TagRegistry.BONKABLE_BLOCK_ITEMS);

        copy(Tags.Blocks.STONES, Tags.Items.STONES);

        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.STONE_BUTTONS, ItemTags.STONE_BUTTONS);
        copy(BlockTags.WALLS, ItemTags.WALLS);

        tag(ItemTags.CHEST_ARMOR)
                .add(ItemRegistry.FIRE_SHIRT.get());

        tag(ItemTags.DECORATED_POT_SHERDS)
                .add(ItemRegistry.BOWSER_POTTERY_SHERD.get())
                .add(ItemRegistry.PLUMBER_POTTERY_SHERD.get());

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ItemRegistry.PIPE_WRENCH.get());

        tag(ItemTags.FOOT_ARMOR)
                .add(ItemRegistry.FIRE_SHOES.get());

        tag(ItemTags.HEAD_ARMOR)
                .add(ItemRegistry.FIRE_HAT.get());

        tag(ItemTags.LEG_ARMOR)
                .add(ItemRegistry.FIRE_OVERALLS.get());

        tag(ItemTags.PIGLIN_LOVED)
                .add(BlockRegistry.COIN.asItem())
                .add(BlockRegistry.FUNGAL_QUESTION_BLOCK.asItem())
                .add(BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.asItem())
                .add(ItemRegistry.SUPER_STAR.get());

        tag(ItemTags.STONE_CRAFTING_MATERIALS)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.asItem())
                .add(BlockRegistry.FUNGAL_STONE.asItem());

        tag(ItemTags.STONE_TOOL_MATERIALS)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.asItem())
                .add(BlockRegistry.FUNGAL_STONE.asItem());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ItemRegistry.FIRE_HAT.get())
                .add(ItemRegistry.FIRE_OVERALLS.get())
                .add(ItemRegistry.FIRE_SHIRT.get())
                .add(ItemRegistry.FIRE_SHOES.get());
    }
}