package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.GameEventRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

public class DataMapGen extends DataMapProvider {
    public DataMapGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(TagRegistry.FLAMMABLE_WOODEN_BRIDGE_ITEMS, new FurnaceFuel(100), false)
                .add(TagRegistry.FLAMMABLE_WOODEN_BRIDGE_STAIR_ITEMS, new FurnaceFuel(100), false);

        builder(NeoForgeDataMaps.VIBRATION_FREQUENCIES)
                .add(GameEventRegistry.CHECKPOINT_ACTIVATED, new VibrationFrequency(11), false);

        builder(NeoForgeDataMaps.OXIDIZABLES)
                .add(BlockRegistry.CUT_COPPER_PEDESTAL, new Oxidizable(BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL.get()), false)
                .add(BlockRegistry.COPPER_QUESTION_BLOCK, new Oxidizable(BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK, new Oxidizable(BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.SMASHABLE_CUT_COPPER, new Oxidizable(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER.get()), false)
                .add(BlockRegistry.STORAGE_CUT_COPPER, new Oxidizable(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER.get()), false)

                .add(BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, new Oxidizable(BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL.get()), false)
                .add(BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, new Oxidizable(BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK, new Oxidizable(BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, new Oxidizable(BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER.get()), false)
                .add(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER, new Oxidizable(BlockRegistry.STORAGE_WEATHERED_CUT_COPPER.get()), false)

                .add(BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, new Oxidizable(BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL.get()), false)
                .add(BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, new Oxidizable(BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK, new Oxidizable(BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, new Oxidizable(BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER.get()), false)
                .add(BlockRegistry.STORAGE_WEATHERED_CUT_COPPER, new Oxidizable(BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER.get()), false);

        builder(NeoForgeDataMaps.WAXABLES)
                .add(BlockRegistry.CUT_COPPER_PEDESTAL, new Waxable(BlockRegistry.WAXED_CUT_COPPER_PEDESTAL.get()), false)
                .add(BlockRegistry.COPPER_QUESTION_BLOCK, new Waxable(BlockRegistry.WAXED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK, new Waxable(BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.SMASHABLE_CUT_COPPER, new Waxable(BlockRegistry.SMASHABLE_WAXED_CUT_COPPER.get()), false)
                .add(BlockRegistry.STORAGE_CUT_COPPER, new Waxable(BlockRegistry.STORAGE_WAXED_CUT_COPPER.get()), false)

                .add(BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, new Waxable(BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL.get()), false)
                .add(BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, new Waxable(BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK, new Waxable(BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, new Waxable(BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER.get()), false)
                .add(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER, new Waxable(BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER.get()), false)

                .add(BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, new Waxable(BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL.get()), false)
                .add(BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, new Waxable(BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK, new Waxable(BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, new Waxable(BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER.get()), false)
                .add(BlockRegistry.STORAGE_WEATHERED_CUT_COPPER, new Waxable(BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER.get()), false)

                .add(BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, new Waxable(BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL.get()), false)
                .add(BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK, new Waxable(BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK, new Waxable(BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK.get()), false)
                .add(BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, new Waxable(BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER.get()), false)
                .add(BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER, new Waxable(BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER.get()), false);
    }
}