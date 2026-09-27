package com.wenxin2.marioverse.integration.stone_zone_compat;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.stone_zone.api.set.stone.StoneTypeRegistry;

public class StoneTypeCompat {
    public static void init() {
        StoneTypeRegistry registry = StoneTypeRegistry.INSTANCE;
        registry.addSimpleFinder("marioverse", "deep_fungal")
                .childBlock("button", "deep_fungal_stone_button")
                .childBlock("cobblestone", "deep_fungal_cobblestone")
                .childBlock("polished", "polished_deep_fungal_stone")
                .childBlock("pressure_plate", "deep_fungal_stone_pressure_plate")
                .childBlock("slab", "deep_fungal_stone_slab")
                .childBlock("stairs", "deep_fungal_stone_stairs")
                .childBlock("wall", "deep_fungal_stone_wall");
        registry.addSimpleFinder("marioverse", "fungal_stone")
                .childBlock("brick_slab", "fungal_stone_brick_slab")
                .childBlock("brick_stairs", "fungal_stone_brick_stairs")
                .childBlock("brick_wall", "fungal_stone_brick_wall")
                .childBlock("bricks", "fungal_stone_bricks")
                .childBlock("button", "fungal_stone_button")
                .childBlock("cobblestone", "fungal_cobblestone")
                .childBlock("cracked_bricks", "cracked_fungal_stone_bricks")
                .childBlock("polished", "polished_fungal_stone")
                .childBlock("slab", "fungal_stone_slab")
                .childBlock("stairs", "fungal_stone_stairs")
                .childBlock("pressure_plate", "fungal_stone_pressure_plate")
                .childBlock("wall", "fungal_stone_wall");
        registry.addSimpleFinder("marioverse", "fortstone")
                .childBlock("tile_slab", "polished_fortstone_tile_slab")
                .childBlock("tile_stairs", "polished_fortstone_tile_stairs")
                .childBlock("tile_wall", "polished_fortstone_tile_wall")
                .childBlock("tiles", "polished_fortstone_tiles");

        if (!PlatHelper.isModLoaded("gemsrealm"))
            registry.addSimpleFinder("marioverse", "amethyst")
                    .stone("minecraft:amethyst_block");
    }
}
