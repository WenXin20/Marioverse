package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.init.BlockFamilyRegistry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class BlockLootTableGen extends LootTableProvider {
    public BlockLootTableGen(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockSubProvider::new, LootContextParamSets.BLOCK)), completableFuture);
    }

    public static class BlockSubProvider extends BlockLootSubProvider {
        public BlockSubProvider(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
        }

        @NotNull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Marioverse.BLOCKS.getEntries().stream().map(DeferredHolder::value)
                    .filter(value -> value instanceof Block)
                    .map(value -> (Block) value).toList();
        }

        @Override
        protected void generate() {
            Marioverse.BLOCKS.getEntries().forEach(deferredHolder -> {
                Block block = deferredHolder.get();
                if (block.getLootTable() != BuiltInLootTables.EMPTY) {
                    if (isBlockInVariants(block))
                        this.genBlockVariants(block);
                    else this.dropSelf(block);
                }
            });
        }

        private boolean isBlockInVariants(Block block) {
            // Iterate over all BlockFamilies
            for (BlockFamilyExtended blockFamily : BlockFamilyRegistry.getAllExtendedFamilies().toList()) {
                for (Map.Entry<BlockFamilyExtended.Variant, Block> entry : blockFamily.getVariants().entrySet()) {
                    if (entry.getValue().equals(block))
                        return true;
                }
            }
            return false;
        }

        private void genBlockVariants(Block block) {
            BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
                blockFamily.getVariants().forEach((variant, variantBlock) -> {
                    BlockFamilyExtended.Variant smashableBlocks = BlockFamilyExtended.Variant.SMASHABLE_BLOCKS;

                    if (variant == smashableBlocks)
                        add(variantBlock, createSilkTouchOnlyTable(variantBlock));
                    else dropSelf(variantBlock);
                });
            });
        }
    }
}
