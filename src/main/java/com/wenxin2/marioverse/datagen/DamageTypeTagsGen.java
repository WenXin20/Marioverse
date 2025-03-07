package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.DamageTypeRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DamageTypeTagsGen extends DamageTypeTagsProvider {
    public DamageTypeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(DamageTypeTags.IS_FIRE)
                .add(DamageTypeRegistry.FIREBALL)
                .add(DamageTypeRegistry.PLAYER_FIREBALL);

        tag(TagRegistry.IS_BONKED)
                .add(DamageTypeRegistry.BONKED)
                .add(DamageTypeRegistry.PLAYER_BONKED);

        tag(TagRegistry.IS_FIREBALL)
                .add(DamageTypeRegistry.FIREBALL)
                .add(DamageTypeRegistry.PLAYER_FIREBALL);

        tag(TagRegistry.IS_ICE_BALL)
                .add(DamageTypeRegistry.ICE_BALL)
                .add(DamageTypeRegistry.PLAYER_ICE_BALL);

        tag(TagRegistry.IS_PIRANHA_CHOMP)
                .add(DamageTypeRegistry.PIRANHA_CHOMP)
                .add(DamageTypeRegistry.PLAYER_PIRANHA_CHOMP);

        tag(TagRegistry.IS_SHRAPNEL)
                .add(DamageTypeRegistry.SHRAPNEL)
                .add(DamageTypeRegistry.PLAYER_SHRAPNEL);

        tag(TagRegistry.IS_SUPER_STAR)
                .add(DamageTypeRegistry.SUPER_STAR)
                .add(DamageTypeRegistry.PLAYER_SUPER_STAR);

        tag(TagRegistry.SHIELD_BLOCKS)
                .addTag(TagRegistry.IS_FIREBALL);
    }
}