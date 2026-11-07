package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

/** Adds arrow item overlay sprites (paletted_permutations) to the vanilla blocks atlas. */
public class ArrowItemSpriteSourceGen extends SpriteSourceProvider {
    public ArrowItemSpriteSourceGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void gather() {
        List<ResourceLocation> textures = new ArrayList<>();
        for (ArrowDirection direction : ArrowDirection.values()) {
            if (direction == ArrowDirection.NONE)
                continue;
            textures.add(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/arrow_sign/pattern/" + direction.getSerializedName()));
        }
        for (ArrowDirection direction : ArrowDirection.values()) {
            if (direction == ArrowDirection.NONE)
                continue;
            textures.add(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/large_arrow_sign/pattern/" + direction.getSerializedName()));
        }

        Map<String, ResourceLocation> permutations = new LinkedHashMap<>();
        for (DyeColor dyeColor : DyeColor.values())
            permutations.put(dyeColor.getSerializedName(),
                    ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "palettes/arrow/" + dyeColor.getSerializedName()));

        ResourceLocation paletteKey = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "palettes/arrow/gray_key");

        this.atlas(BLOCKS_ATLAS).addSource(new PalettedPermutations(textures, paletteKey, permutations));
    }
}
