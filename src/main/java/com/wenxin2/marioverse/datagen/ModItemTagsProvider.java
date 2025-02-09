package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider lookupProvider) {
        copy(TagRegistry.BRICK_PEDESTAL_BLOCKS, TagRegistry.BRICK_PEDESTAL_ITEMS);
        copy(TagRegistry.INVISIBLE_QUESTION_BLOCKS, TagRegistry.INVISIBLE_QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.QUESTION_BLOCKS, TagRegistry.QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.SMASHABLE_BLOCKS, TagRegistry.SMASHABLE_BLOCK_ITEMS);
        copy(TagRegistry.STORAGE_BRICK_BLOCKS, TagRegistry.STORAGE_BRICK_ITEMS);
        copy(TagRegistry.BONKABLE_BLOCKS, TagRegistry.BONKABLE_BLOCK_ITEMS);
    }
}