package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

public class ModBlockLootTableProvider extends LootTableProvider {

    public ModBlockLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockSubProvider::new, LootContextParamSets.BLOCK)), completableFuture);
    }

    public static class BlockSubProvider extends BlockLootSubProvider {
        private final Set<Block> generatedBlocks = new HashSet<>();

        public BlockSubProvider(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
        }

        @NotNull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return generatedBlocks;
        }

        @Override
        protected void generate() {
            generatedBlocks.add(BlockRegistry.COPPER_QUESTION_BLOCK.get());
            generatedBlocks.add(BlockRegistry.CUT_COPPER_PEDESTAL.get());
            generatedBlocks.add(BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK.get());
            generatedBlocks.add(BlockRegistry.SMASHABLE_CUT_COPPER.get());
            generatedBlocks.add(BlockRegistry.STORAGE_CUT_COPPER.get());

            generatedBlocks.add(BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK.get());
            generatedBlocks.add(BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL.get());
            generatedBlocks.add(BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK.get());
            generatedBlocks.add(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER.get());
            generatedBlocks.add(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER.get());

            add(BlockRegistry.SMASHABLE_CUT_COPPER.get(), createSilkTouchOnlyTable(BlockRegistry.SMASHABLE_CUT_COPPER.get()));
            add(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER.get(), createSilkTouchOnlyTable(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER.get()));

            dropSelf(BlockRegistry.COPPER_QUESTION_BLOCK.get());
            dropSelf(BlockRegistry.CUT_COPPER_PEDESTAL.get());
            dropSelf(BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK.get());
            dropSelf(BlockRegistry.STORAGE_CUT_COPPER.get());

            dropSelf(BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK.get());
            dropSelf(BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL.get());
            dropSelf(BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK.get());
            dropSelf(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER.get());
        }
    }
}
