package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.GameEventRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.ParrotImitation;
import net.neoforged.neoforge.registries.datamaps.builtin.Strippable;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

public class DataMapGen extends DataMapProvider {
    public DataMapGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE.asItem().builtInRegistryHolder(), new Compostable(0.85F), false)
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.asItem().builtInRegistryHolder(), new Compostable(0.65F), false)
                .add(BlockRegistry.DANGO_BLOSSOM.asItem().builtInRegistryHolder(), new Compostable(0.65F), false)
                .add(BlockRegistry.MUSHROOT_LEAVES.asItem().builtInRegistryHolder(), new Compostable(0.3F), false)
                .add(BlockRegistry.MUSHROOT_SAPLING.asItem().builtInRegistryHolder(), new Compostable(0.3F), false)
                .add(ItemRegistry.PIRANHA_PLANT_POD, new Compostable(0.3F), false)
                .add(BlockRegistry.RED_MUSHROOM_TRAMPOLINE.asItem().builtInRegistryHolder(), new Compostable(0.85F), false)
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.asItem().builtInRegistryHolder(), new Compostable(0.65F), false);

        builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(TagRegistry.FLAMMABLE_BRIDGE_ITEMS, new FurnaceFuel(100), false)
                .add(TagRegistry.FLAMMABLE_BRIDGE_STAIR_ITEMS, new FurnaceFuel(100), false);

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

        builder(NeoForgeDataMaps.PARROT_IMITATIONS)
                .add(TagRegistry.CHEEP_CHEEPS, new ParrotImitation(SoundRegistry.PARROT_IMITATES_CHEEP_CHEEP.get()), false)
                .add(TagRegistry.KOOPA_SHELL_ENTITIES, new ParrotImitation(SoundRegistry.PARROT_IMITATES_KOOPA_SHELL.get()), false)
                .add(TagRegistry.KOOPA_TROOPA_ENTITIES, new ParrotImitation(SoundRegistry.PARROT_IMITATES_KOOPA_TROOPA.get()), false)
                .add(EntityRegistry.BOO, new ParrotImitation(SoundRegistry.PARROT_IMITATES_BOO.get()), false)
                .add(EntityRegistry.DRY_BONES, new ParrotImitation(SoundRegistry.PARROT_IMITATES_DRY_BONES.get()), false)
                .add(EntityRegistry.GOOMBA, new ParrotImitation(SoundRegistry.PARROT_IMITATES_GOOMBA.get()), false)
                .add(EntityRegistry.HEFTY_GOOMBA, new ParrotImitation(SoundRegistry.PARROT_IMITATES_HEFTY_GOOMBA.get()), false)
                .add(EntityRegistry.MEGA_GOOMBA, new ParrotImitation(SoundRegistry.PARROT_IMITATES_MEGA_GOOMBA.get()), false)
                .add(EntityRegistry.MINI_GOOMBA, new ParrotImitation(SoundRegistry.PARROT_IMITATES_MINI_GOOMBA.get()), false)
                .add(EntityRegistry.PIRANHA_PLANT, new ParrotImitation(SoundRegistry.PARROT_IMITATES_PIRANHA_PLANT.get()), false)
                .add(EntityRegistry.PORCUPUFFER, new ParrotImitation(SoundRegistry.PARROT_IMITATES_PORCUPUFFER.get()), false)
                .add(EntityRegistry.SPLUNKIN, new ParrotImitation(SoundRegistry.PARROT_IMITATES_SPLUNKIN.get()), false)
                .add(EntityRegistry.SUPER_STAR, new ParrotImitation(SoundRegistry.PARROT_IMITATES_SUPER_STAR.get()), false);

        builder(NeoForgeDataMaps.STRIPPABLES)
                .add(BlockRegistry.MUSHROOT_LOG, new Strippable(BlockRegistry.STRIPPED_MUSHROOT_LOG.get()), false)
                .add(BlockRegistry.MUSHROOT_WOOD, new Strippable(BlockRegistry.STRIPPED_MUSHROOT_WOOD.get()), false);

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