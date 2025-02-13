package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BannerPatternRegistry {
    public static final ResourceKey<BannerPattern> BOWSER;
    public static final ResourceKey<BannerPattern> PLUMBER;

    static {
        BOWSER = register("bowser");
        PLUMBER = register("plumber");
    }

    private static ResourceKey<BannerPattern> register(String name) {
        return ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    public static void bootstrap(BootstrapContext<BannerPattern> context) {
        context.register(BOWSER, new BannerPattern(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "bowser"),
                "block.marioverse.banner.bowser"));
        context.register(PLUMBER, new BannerPattern(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "plumber"),
                "block.marioverse.banner.plumber"));
    }

    public static void init() {}
}
