package com.wenxin2.marioverse.integration.stone_zone_compat;

import net.mehvahdjukaar.stone_zone.api.set.stone.StoneTypeRegistry;

public class StoneTypeCompat {
    public static void init() {
        StoneTypeRegistry registry = StoneTypeRegistry.INSTANCE;
        registry.addSimpleFinder("marioverse", "deep_fungal")
                .childBlock("button", "deep_fungal_stone_button")
                .childBlock("cobblestone", "deep_fungal_cobblestone")
                .childBlock("slab", "deep_fungal_stone_slab")
                .childBlock("stairs", "deep_fungal_stone_stairs")
                .childBlock("polished", "polished_deep_fungal_stone")
                .childBlock("pressure_plate", "deep_fungal_stone_pressure_plate")
                .childBlock("wall", "deep_fungal_stone_wall");
        registry.addSimpleFinder("marioverse", "fungal_stone")
                .childBlock("button", "fungal_stone_button")
                .childBlock("cobblestone", "fungal_cobblestone")
                .childBlock("slab", "fungal_stone_slab")
                .childBlock("stairs", "fungal_stone_stairs")
                .childBlock("polished", "polished_fungal_stone")
                .childBlock("pressure_plate", "fungal_stone_pressure_plate")
                .childBlock("wall", "fungal_stone_wall");
        registry.addSimpleFinder("marioverse", "fortstone")
                .childBlock("tile_slab", "polished_fortstone_tile_slab")
                .childBlock("tile_stairs", "polished_fortstone_tile_stairs")
                .childBlock("tiles", "polished_fortstone_tiles")
                .childBlock("tile_wall", "polished_fortstone_tile_wall");
    }
}
