package com.wenxin2.marioverse.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

/** Generates assets/marioverse/atlases/arrow.json - the entity-side paletted_permutations atlas. */
public class ArrowAtlasGen implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public ArrowAtlasGen(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        JsonArray textures = new JsonArray();
        for (ArrowDirection direction : ArrowDirection.values()) {
            if (direction == ArrowDirection.NONE)
                continue;
            textures.add("marioverse:entity/signs/arrow/pattern/" + direction.getSerializedName());
        }
        for (ArrowDirection direction : ArrowDirection.values()) {
            if (direction == ArrowDirection.NONE)
                continue;
            textures.add("marioverse:entity/signs/large_arrow/pattern/" + direction.getSerializedName());
        }

        JsonObject permutations = new JsonObject();
        for (DyeColor dyeColor : DyeColor.values())
            permutations.addProperty(dyeColor.getSerializedName(), "marioverse:palettes/arrow/" + dyeColor.getSerializedName());

        JsonObject source = new JsonObject();
        source.addProperty("type", "minecraft:paletted_permutations");
        source.add("textures", textures);
        source.addProperty("palette_key", "marioverse:palettes/arrow/gray_key");
        source.add("permutations", permutations);

        JsonArray sources = new JsonArray();
        sources.add(source);

        JsonObject root = new JsonObject();
        root.add("sources", sources);

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "arrow");
        return DataProvider.saveStable(cache, root, this.pathProvider.json(id));
    }

    @Override
    public String getName() {
        return "Arrow Atlas";
    }
}
