package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.datagen.AdvancementDataGen;
import com.wenxin2.marioverse.datagen.CopperMapDataGen;
import com.wenxin2.marioverse.datagen.BannerPatternTagsGen;
import com.wenxin2.marioverse.datagen.BiomeTagsGen;
import com.wenxin2.marioverse.datagen.BlockLootTableGen;
import com.wenxin2.marioverse.datagen.BlockRecipeGen;
import com.wenxin2.marioverse.datagen.BlockStateGen;
import com.wenxin2.marioverse.datagen.BlockTagsGen;
import com.wenxin2.marioverse.datagen.DamageTypeTagsGen;
import com.wenxin2.marioverse.datagen.EntityTypeTagsGen;
import com.wenxin2.marioverse.datagen.ItemModelGen;
import com.wenxin2.marioverse.datagen.ItemTagsGen;
import com.wenxin2.marioverse.datagen.RegistryDataGen;
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
        BlockTagsGen blockTags = new BlockTagsGen(output, lookupProvider, existingFileHelper);

        generator.addProvider(event.includeClient(), new BlockStateGen(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGen(output, existingFileHelper));

        generator.addProvider(event.includeServer(), new AdvancementDataGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new CopperMapDataGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new RegistryDataGen(output, lookupProvider));

        generator.addProvider(event.includeServer(), new BannerPatternTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BiomeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockLootTableGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new BlockRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new BlockTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new DamageTypeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new EntityTypeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ItemTagsGen(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
    }
}
