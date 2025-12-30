package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.PipeBubblesBlock;
import com.wenxin2.marioverse.blocks.PottedPiranhaPlantBlock;
import com.wenxin2.marioverse.blocks.QuicksandBlock;
import com.wenxin2.marioverse.blocks.StarCoinBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.WaterSpoutBlock;
import com.wenxin2.marioverse.blocks.states.QuadrantBlockStates;
import com.wenxin2.marioverse.blocks.states.TripleBlockStates;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.registries.BlockFamilyRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
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
                    .filter(block -> block instanceof Block && !(block instanceof PipeBubblesBlock) && !(block instanceof WaterSpoutBlock))
                    .map(block -> (Block) block).toList();
        }

        @Override
        protected void generate() {
            Marioverse.BLOCKS.getEntries().forEach(deferredHolder -> {
                Block block = deferredHolder.get();
                if (block.getLootTable() != BuiltInLootTables.EMPTY) {
                    if (isBlockInVariants(block))
                        this.genBlockVariants();
                    else if (block instanceof CheckpointFlagBlock)
                        this.add(block, this.createCheckpointFlagDrop(block));
                    else if (block instanceof GoalPoleBlock)
                        this.add(block, this.createNameableBlockEntityTable(block));
                    else if (block instanceof PipeBubblesBlock)
                        this.add(block, this.createNoLootDrop());
                    else if (block instanceof StarCoinBlock)
                        this.add(block, this.createStarCoinDrop(block));
                    else if (block instanceof WarpPipeBlock)
                        this.add(block, this.createNameableWarpPipeBEDrop(block));
                    else if (block instanceof WaterSpoutBlock)
                        this.add(block, this.createNoLootDrop());
                    else if (block instanceof PottedPiranhaPlantBlock)
                        this.add(block, this.createPotFlowerItemTable(ItemRegistry.PIRANHA_PLANT_POD));
                    else if (block instanceof FlowerPotBlock pot && pot.getPotted() == BlockRegistry.DANGO_BLOSSOM.get())
                        this.add(block, this.createPotFlowerItemTable(BlockRegistry.DANGO_BLOSSOM.get()));
                    else this.dropSelf(block);
                }
            });
        }

        private boolean isBlockInVariants(Block block) {
            for (BlockFamilyExtended blockFamily : BlockFamilyRegistry.getAllExtendedFamilies().toList()) {
                for (Map.Entry<BlockFamilyExtended.Variant, Block> entry : blockFamily.getVariants().entrySet()) {
                    if (entry.getValue().equals(block))
                        return true;
                }
            }
            return false;
        }

        private void genBlockVariants() {
            BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
                blockFamily.getVariants().forEach((variant, variantBlock) -> {
                    BlockFamilyExtended.Variant quicksand = BlockFamilyExtended.Variant.QUICKSAND;
                    BlockFamilyExtended.Variant slabs = BlockFamilyExtended.Variant.SLAB;
                    BlockFamilyExtended.Variant smashableBlocks = BlockFamilyExtended.Variant.SMASHABLE_BLOCKS;

                    if (variant == quicksand)
                        dropOther(variantBlock, blockFamily.getBaseBlock());
                    else if (variant == smashableBlocks)
                        add(variantBlock, this.createSilkTouchOnlyTable(variantBlock));
                    else if (variant == slabs)
                        add(variantBlock, this.createSlabItemTable(variantBlock));
                    else dropSelf(variantBlock);
                });
            });
        }

        protected LootTable.Builder createCheckpointFlagDrop(Block block) {
            return LootTable.lootTable().withPool(this.applyExplosionCondition(block,
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(block)
                                    .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                            .include(DataComponents.CUSTOM_NAME)
                                            .include(DataComponents.CONTAINER)
                                            .include(DataComponents.LOCK)
                                            .include(DataComponents.CONTAINER_LOOT)))
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(StatePropertiesPredicate.Builder.properties()
                                            .hasProperty(CheckpointFlagBlock.PART, TripleBlockStates.BOTTOM))))
            );
        }

        protected LootTable.Builder createNameableWarpPipeBEDrop(Block block) {
            return LootTable.lootTable().withPool(this.applyExplosionCondition(block,
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(block)
                                    .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                            .include(DataComponents.CUSTOM_NAME)
                                            .include(DataComponentRegistry.PIPE_NAME.get()))))
            );
        }

        protected LootTable.Builder createNoLootDrop() {
            return LootTable.lootTable();
        }

        protected LootTable.Builder createStarCoinDrop(Block block) {
            return LootTable.lootTable().withPool(this.applyExplosionCondition(block,
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(block)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(StarCoinBlock.HALF, DoubleBlockHalf.LOWER)
                                                    .hasProperty(StarCoinBlock.QUADRANT, QuadrantBlockStates.NORTH_WEST)))))
            );
        }

        protected LootTable.Builder createStorageBlockDrop(Block block) {
            return LootTable.lootTable().withPool(this.applyExplosionCondition(block,
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(block)
                                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                        .include(DataComponents.CUSTOM_NAME)
                                                        .include(DataComponents.CONTAINER)
                                                        .include(DataComponents.LOCK)
                                                        .include(DataComponents.CONTAINER_LOOT))))
            );
        }
    }
}
