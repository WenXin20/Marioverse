package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BiomeTagsGen extends BiomeTagsProvider {
    public BiomeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(TagRegistry.HAS_FIRE_GOOMBA)
                .addTag(BiomeTags.IS_NETHER);

        tag(TagRegistry.HAS_GOOMBA)
                .addTag(Tags.Biomes.IS_PLAINS)
                .addTag(Tags.Biomes.IS_SPOOKY)
                .addTag(Tags.Biomes.IS_SWAMP)
                .addTag(BiomeTags.IS_FOREST)
                .add(Biomes.PLAINS)
                .add(Biomes.SUNFLOWER_PLAINS);

        tag(TagRegistry.HAS_PIRANHA_PLANT)
                .addTag(Tags.Biomes.IS_JUNGLE)
                .addTag(BiomeTags.IS_JUNGLE);

        tag(TagRegistry.HAS_WORLD_1_2)
                .addTag(Tags.Biomes.IS_MUSHROOM)
                .addTag(Tags.Biomes.IS_PLAINS)
                .addTag(Tags.Biomes.IS_SPOOKY)
                .addTag(Tags.Biomes.IS_SWAMP)
                .addTag(BiomeTags.IS_FOREST)
                .addTag(BiomeTags.IS_HILL)
                .addTag(BiomeTags.IS_JUNGLE)
                .addTag(BiomeTags.IS_SAVANNA)
                .addTag(BiomeTags.IS_TAIGA)
                .add(Biomes.MUSHROOM_FIELDS)
                .add(Biomes.PLAINS)
                .add(Biomes.SNOWY_PLAINS)
                .add(Biomes.SUNFLOWER_PLAINS);
    }
}