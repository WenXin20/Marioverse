package com.wenxin2.marioverse.integration.dramatic_doors_compat;

import com.fizzware.dramaticdoors.neoforge.blocks.ShortDoorBlock;
import com.fizzware.dramaticdoors.neoforge.blocks.TallDoorBlock;
import com.fizzware.dramaticdoors.neoforge.compat.DDCompatRecipe;
import com.google.common.collect.Iterables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.MultiPartWarpDoor;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

public class DramaticDoorsCompat {
    public static final String MOD_ID = "dramaticdoors";
    private static final Set<String> SHORT_DOOR_STATES = Set.of("facing", "hinge", "open", "powered", "waterlogged");
    private static final Set<String> TALL_DOOR_STATES = Set.of("facing", "hinge", "open", "powered", "waterlogged", "third");

    public static void registerWarpDoors(RegisterEvent.RegisterHelper<Block> helper, Map<Block, Block> warpDoors) {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream().toList();
        Set<ResourceLocation> missingSources = DramaticDoorsCompat.findDoorsWithMissingSource();

        for (Block block : blocks) {
            if (block instanceof MultiPartWarpDoor)
                continue;

            ResourceLocation sourceID = BuiltInRegistries.BLOCK.getKey(block);
            if (missingSources.contains(sourceID) || missingSources.contains(DramaticDoorsCompat.getTallCounterpart(sourceID)))
                continue;

            Set<String> propertyNames = block.getStateDefinition().getProperties().stream()
                    .map(Property::getName).collect(Collectors.toSet());
            Block warp;

            if (block instanceof TallDoorBlock tallDoor && propertyNames.equals(TALL_DOOR_STATES))
                warp = new WarpTallDoorBlock(tallDoor);
            else if (block instanceof ShortDoorBlock shortDoor && propertyNames.equals(SHORT_DOOR_STATES))
                warp = new WarpShortDoorBlock(shortDoor);
            else continue;

            ResourceLocation warpID = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                    sourceID.getNamespace() + "_warp_" + sourceID.getPath());

            helper.register(warpID, warp);
            warpDoors.put(block, warp);
        }
    }

    private static Set<ResourceLocation> findDoorsWithMissingSource() {
        Set<ResourceLocation> missing = new HashSet<>();

        for (JsonObject recipe : Iterables.concat(DDCompatRecipe.TALL_DOOR_RECIPES, DDCompatRecipe.SHORT_DOOR_RECIPES)) {
            ResourceLocation result = DramaticDoorsCompat.getResult(recipe.get("result"));
            if (result != null && !DramaticDoorsCompat.hasLoadedIngredients(recipe.get("key")))
                missing.add(result);
        }
        return missing;
    }

    private static ResourceLocation getTallCounterpart(ResourceLocation id) {
        return id.getPath().startsWith("short_")
                ? id.withPath("tall_" + id.getPath().substring("short_".length())) : id;
    }

    @Nullable
    private static ResourceLocation getResult(@Nullable JsonElement result) {
        if (result == null)
            return null;
        if (result.isJsonPrimitive())
            return ResourceLocation.tryParse(result.getAsString());
        if (!result.isJsonObject())
            return null;

        JsonObject object = result.getAsJsonObject();
        JsonElement id = object.has("id") ? object.get("id") : object.get("item");
        return id != null && id.isJsonPrimitive() ? ResourceLocation.tryParse(id.getAsString()) : null;
    }

    private static boolean hasLoadedIngredients(@Nullable JsonElement element) {
        if (element == null)
            return true;

        if (element.isJsonArray()) {
            for (JsonElement entry : element.getAsJsonArray())
                if (!DramaticDoorsCompat.hasLoadedIngredients(entry))
                    return false;
            return true;
        }

        if (!element.isJsonObject())
            return true;

        JsonObject object = element.getAsJsonObject();
        if (object.has("item") && object.get("item").isJsonPrimitive()) {
            ResourceLocation id = ResourceLocation.tryParse(object.get("item").getAsString());
            return id == null || ModList.get().isLoaded(id.getNamespace());
        }

        for (Map.Entry<String, JsonElement> entry : object.entrySet())
            if (!DramaticDoorsCompat.hasLoadedIngredients(entry.getValue()))
                return false;
        return true;
    }
}
