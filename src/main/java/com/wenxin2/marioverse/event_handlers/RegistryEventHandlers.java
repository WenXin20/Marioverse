package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.datagen.ModBlockLootTableProvider;
import com.wenxin2.marioverse.datagen.ModBlockRecipeProvider;
import com.wenxin2.marioverse.datagen.ModBlockStateProvider;
import com.wenxin2.marioverse.datagen.ModItemModelProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class RegistryEventHandlers {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModBlockLootTableProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new ModBlockRecipeProvider(output, lookupProvider));
    }
}
