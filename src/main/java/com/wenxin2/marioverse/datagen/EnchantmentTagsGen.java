package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EnchantmentTagsGen extends EnchantmentTagsProvider {
    public EnchantmentTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(TagRegistry.BYPASSES_BOO_INVULNERABILITY_ENCHANTS)
                .add(Enchantments.SMITE);

        tag(TagRegistry.PREVENTS_DRY_BONES_RESURRECTION_ENCHANTS)
                .add(Enchantments.SMITE);
    }
}