package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.init.BlockFamilyRegistry;
import com.wenxin2.marioverse.init.DataComponentRegistry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
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
                    else if (block instanceof GoalPoleBlock)
                        this.add(block, this.createNameableBlockEntityTable(block));
                    else if (block instanceof WarpPipeBlock)
                        this.add(block, this.createNameableWarpPipeBETable(block));
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
                        add(variantBlock, this.createSilkTouchOnlyTable(variantBlock));
                    else dropSelf(variantBlock);
                });
            });
        }

        protected LootTable.Builder createNameableWarpPipeBETable(Block block) {
            return LootTable.lootTable().withPool(this.applyExplosionCondition(block,
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(block)
                                    .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                            .include(DataComponents.CUSTOM_NAME)
                                            .include(DataComponentRegistry.PIPE_NAME.get()))))
            );
        }
    }
}
