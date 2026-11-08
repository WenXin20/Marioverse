package com.wenxin2.marioverse.integration.fast_paintings_compat;

import net.neoforged.fml.ModList;

public class FastPaintingsProvider {
    public static final String MOD_ID = "fastpaintings";
    private static Boolean fastPaintingsLoaded;

    public static boolean isLoaded() {
        if (fastPaintingsLoaded == null)
            fastPaintingsLoaded = ModList.get().isLoaded(FastPaintingsProvider.MOD_ID);
        return fastPaintingsLoaded;
    }
}
