package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimPattern;

public class TrimPatternRegistry {
    public static final ResourceKey<TrimPattern> LUIGI;
    public static final ResourceKey<TrimPattern> MARIO;
    public static final ResourceKey<TrimPattern> PRINCESS;

    static {
        LUIGI = ResourceKey.create(Registries.TRIM_PATTERN,
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "luigi"));
        MARIO = ResourceKey.create(Registries.TRIM_PATTERN,
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mario"));
        PRINCESS = ResourceKey.create(Registries.TRIM_PATTERN,
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "princess"));
    }
}
