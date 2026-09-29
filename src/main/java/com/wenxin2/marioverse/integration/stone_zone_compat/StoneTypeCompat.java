package com.wenxin2.marioverse.integration.stone_zone_compat;

import com.google.gson.JsonObject;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.stone_zone.api.set.mud.MudTypeRegistry;
import net.mehvahdjukaar.stone_zone.api.set.stone.StoneType;
import net.mehvahdjukaar.stone_zone.api.set.stone.StoneTypeRegistry;
import net.mehvahdjukaar.stone_zone.api.set.stone.VanillaStoneTypes;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

public class StoneTypeCompat {
    public static void init() {
        StoneTypeRegistry registry = StoneTypeRegistry.INSTANCE;
        MudTypeRegistry mudRegistry = MudTypeRegistry.INSTANCE;

        mudRegistry.addSimpleFinder("marioverse", "wet_mud");

        if (!PlatHelper.isModLoaded("gemsrealm"))
            registry.addSimpleFinder("marioverse", "amethyst")
                    .stone("minecraft:amethyst_block");

        registry.addSimpleFinder("marioverse", "deep_fungal")
                .childBlock("button", "deep_fungal_stone_button")
                .childBlock("cobblestone", "deep_fungal_cobblestone")
                .childBlock("polished", "polished_deep_fungal_stone")
                .childBlock("pressure_plate", "deep_fungal_stone_pressure_plate")
                .childBlock("slab", "deep_fungal_stone_slab")
                .childBlock("stairs", "deep_fungal_stone_stairs")
                .childBlock("wall", "deep_fungal_stone_wall");
        registry.addSimpleFinder("marioverse", "fungal_stone")
                .childBlock("brick_slab", "fungal_brick_slab")
                .childBlock("brick_stairs", "fungal_brick_stairs")
                .childBlock("brick_wall", "fungal_brick_wall")
                .childBlock("bricks", "fungal_bricks")
                .childBlock("button", "fungal_stone_button")
                .childBlock("cobblestone", "fungal_cobblestone")
                .childBlock("cracked_bricks", "cracked_fungal_bricks")
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

        if (ConfigRegistry.ENABLE_DYED_CALCITE_STONE_ZONE.get()) {
            registry.addSimpleFinder("marioverse", "light_gray_calcite");
            registry.addSimpleFinder("marioverse", "gray_calcite");
            registry.addSimpleFinder("marioverse", "black_calcite");
            registry.addSimpleFinder("marioverse", "brown_calcite");
            registry.addSimpleFinder("marioverse", "red_calcite");
            registry.addSimpleFinder("marioverse", "orange_calcite");
            registry.addSimpleFinder("marioverse", "yellow_calcite");
            registry.addSimpleFinder("marioverse", "lime_calcite");
            registry.addSimpleFinder("marioverse", "green_calcite");
            registry.addSimpleFinder("marioverse", "cyan_calcite");
            registry.addSimpleFinder("marioverse", "blue_calcite");
            registry.addSimpleFinder("marioverse", "light_blue_calcite");
            registry.addSimpleFinder("marioverse", "purple_calcite");
            registry.addSimpleFinder("marioverse", "magenta_calcite");
            registry.addSimpleFinder("marioverse", "pink_calcite");
        }

        BlockSetAPI.addDynamicRegistration(Marioverse.MOD_ID, registrator -> {
            StoneType calcite = VanillaStoneTypes.CALCITE;
            calcite.addChild("brick_slab", BlockRegistry.WHITE_CALCITE_BRICK_SLAB.get());
            calcite.addChild("brick_stairs", BlockRegistry.WHITE_CALCITE_BRICK_STAIRS.get());
            calcite.addChild("brick_wall", BlockRegistry.WHITE_CALCITE_BRICK_WALL.get());
            calcite.addChild("bricks", BlockRegistry.CALCITE_BRICKS.get(DyeColor.WHITE).get());
            calcite.addChild("button", BlockRegistry.CALCITE_BUTTON.get());
            calcite.addChild("cracked_bricks", BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.WHITE).get());
            calcite.addChild("polished", BlockRegistry.POLISHED_CALCITE.get(DyeColor.WHITE).get());
            calcite.addChild("polished_slab", BlockRegistry.POLISHED_WHITE_CALCITE_SLAB.get());
            calcite.addChild("polished_stairs", BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS.get());
            calcite.addChild("polished_wall", BlockRegistry.POLISHED_WHITE_CALCITE_WALL.get());
            calcite.addChild("pressure_plate", BlockRegistry.CALCITE_PRESSURE_PLATE.get());
            calcite.addChild("slab", BlockRegistry.CALCITE_SLAB.get());
            calcite.addChild("stairs", BlockRegistry.CALCITE_STAIRS.get());
            calcite.addChild("wall", BlockRegistry.CALCITE_WALL.get());
        }, BuiltInRegistries.BLOCK);
    }

    public static Map<ResourceLocation, byte[]> createCalciteColorSets() {
        Map<ResourceLocation, byte[]> colorSets = new HashMap<>();
        if (!ConfigRegistry.ENABLE_DYED_CALCITE_STONE_ZONE.get())
            return colorSets;

        Map<String, EnumMap<DyeColor, Block>> childrenByKey = new HashMap<>();
        for (DyeColor color : DyeColor.values()) {
            if (color == DyeColor.WHITE)
                continue;

            StoneType stoneType = StoneTypeRegistry.INSTANCE.get(ResourceLocation
                    .fromNamespaceAndPath(Marioverse.MOD_ID, color.getName() + "_calcite"));
            if (stoneType == null)
                continue;

            for (Map.Entry<String, Object> child : stoneType.getChildren()) {
                if (child.getValue() instanceof Block block
                        && !BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(Marioverse.MOD_ID))
                    childrenByKey.computeIfAbsent(child.getKey(), key -> new EnumMap<>(DyeColor.class)).put(color, block);
            }
        }

        childrenByKey.forEach((key, blocks) -> {
            Block whiteBlock = VanillaStoneTypes.CALCITE.getBlockOfThis(key);
            if (whiteBlock == null) {
                Marioverse.LOGGER.debug("No calcite counterpart for Stone Zone child {}, skipping color set", key);
                return;
            }
            blocks.put(DyeColor.WHITE, whiteBlock);

            JsonObject colors = new JsonObject();
            colors.addProperty("default", BuiltInRegistries.BLOCK.getKey(whiteBlock).toString());
            blocks.forEach((color, block) -> colors.addProperty(color.getName(), BuiltInRegistries.BLOCK.getKey(block).toString()));

            Map.Entry<DyeColor, Block> dyed = blocks.entrySet().stream()
                    .filter(entry -> entry.getKey() != DyeColor.WHITE).findFirst().orElseThrow();
            ResourceLocation dyedId = BuiltInRegistries.BLOCK.getKey(dyed.getValue());
            ResourceLocation setId = dyedId.withPath(dyedId.getPath()
                    .replaceFirst(dyed.getKey().getName() + "_calcite", "calcite"));

            JsonObject json = new JsonObject();
            json.addProperty("id", setId.toString());
            json.add("colors", colors);
            json.addProperty("replace", true);

            colorSets.put(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                            "color_sets/" + setId.getNamespace() + "/" + setId.getPath() + ".json"),
                    json.toString().getBytes(StandardCharsets.UTF_8));
        });
        return colorSets;
    }
}
