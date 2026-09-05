package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PlacedFeatureRegistry {
    public static final ResourceKey<PlacedFeature> MUSHROOT;
    public static final ResourceKey<PlacedFeature> SHROOMGRASS_BONEMEAL;

    static {
        MUSHROOT = ResourceKey.create(Marioverse.PLACED_FEATURES.getRegistryKey(),
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mushroot"));
        SHROOMGRASS_BONEMEAL = ResourceKey.create(Marioverse.PLACED_FEATURES.getRegistryKey(),
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "shroomgrass_bonemeal"));
    }

    public static void init() {
    }
}