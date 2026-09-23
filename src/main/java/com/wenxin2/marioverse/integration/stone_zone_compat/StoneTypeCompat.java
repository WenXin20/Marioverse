package com.wenxin2.marioverse.integration.stone_zone_compat;

import net.mehvahdjukaar.stone_zone.api.set.stone.StoneTypeRegistry;

public class StoneTypeCompat {
    public static void init() {
        StoneTypeRegistry registry = StoneTypeRegistry.INSTANCE;
        registry.addSimpleFinder("marioverse", "deep_fungal")
                .childBlock("cobblestone", "deep_fungal_cobblestone");
    }
}
