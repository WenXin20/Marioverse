package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;

public class CopperMapProvider extends DataMapProvider {
    public CopperMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather() {
        // We create a builder for the EXAMPLE_DATA data map and add our entries using #add.
        builder(NeoForgeDataMaps.OXIDIZABLES)
                .add(BlockRegistry.CUT_COPPER_PEDESTAL, new Oxidizable(BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL.get()), false)
                .add(BlockRegistry.COPPER_QUESTION_BLOCK, new Oxidizable(BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK, new Oxidizable(BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.SMASHABLE_CUT_COPPER, new Oxidizable(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER.get()), false)
                .add(BlockRegistry.STORAGE_CUT_COPPER, new Oxidizable(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER.get()), false);
    }
}