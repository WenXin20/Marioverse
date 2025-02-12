package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.BannerPatternRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BannerPatternTagsGen extends BannerPatternTagsProvider {
    public BannerPatternTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(TagRegistry.BOWSER_BANNER_PATTERN)
                .add(BannerPatternRegistry.BOWSER);

        tag(TagRegistry.PLUMBER_BANNER_PATTERN)
                .add(BannerPatternRegistry.PLUMBER);
    }
}