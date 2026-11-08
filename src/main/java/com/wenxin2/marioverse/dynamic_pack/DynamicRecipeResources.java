package com.wenxin2.marioverse.dynamic_pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DynamicRecipeResources implements PackResources {
    private final Map<ResourceLocation, byte[]> recipes = new HashMap<>();
    private final PackLocationInfo location;

    public DynamicRecipeResources(PackLocationInfo location) {
        this.location = location;
        this.generateRecipes(RegistryEventHandlers.WARP_DOORS, "marioverse:warp_door",
                "marioverse:warp_doors", TagRegistry.CRAFTS_WARP_DOOR);
        this.generateRecipes(RegistryEventHandlers.WARP_TRAPDOORS, "marioverse:warp_trapdoor",
                "marioverse:warp_trapdoors", TagRegistry.CRAFTS_WARP_TRAPDOOR);
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... path) {
        if (path.length == 1 && path[0].equals("pack.mcmeta")) {
            JsonObject root = new JsonObject();
            JsonObject pack = new JsonObject();

            pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
            pack.addProperty("description", "Marioverse dynamic recipes for warp doors & trapdoors");
            root.add("pack", pack);

            byte[] bytes = root.toString().getBytes(StandardCharsets.UTF_8);
            return () -> new ByteArrayInputStream(bytes);
        }
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type != PackType.SERVER_DATA)
            return null;

        byte[] data = this.recipes.get(location);
        return data == null ? null : () -> new ByteArrayInputStream(data);
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (type != PackType.SERVER_DATA || !path.equals("recipe"))
            return;

        for (Map.Entry<ResourceLocation, byte[]> entry : this.recipes.entrySet()) {
            if (entry.getKey().getNamespace().equals(namespace))
                output.accept(entry.getKey(), () -> new ByteArrayInputStream(entry.getValue()));
        }
    }

    @NotNull
    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.SERVER_DATA ? Set.of(Marioverse.MOD_ID) : Set.of();
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
        IoSupplier<InputStream> supplier = this.getRootResource("pack.mcmeta");
        if (supplier == null)
            return null;

        try (InputStream input = supplier.get()) {
            return DynamicServerResources.getMetadataFromStream(serializer, input);
        }
    }

    @NotNull
    @Override
    public PackLocationInfo location() {
        return this.location;
    }

    @Override
    public void close() {
    }

    private void generateRecipes(Map<Block, Block> conversions, String type, String group, TagKey<Item> extra) {
        for (Map.Entry<Block, Block> entry : conversions.entrySet()) {
            ResourceLocation sourceId = BuiltInRegistries.ITEM.getKey(entry.getKey().asItem());
            ResourceLocation warpId = BuiltInRegistries.ITEM.getKey(entry.getValue().asItem());

            JsonObject source = new JsonObject();
            source.addProperty("item", sourceId.toString());

            JsonObject ingredient = new JsonObject();
            ingredient.addProperty("tag", extra.location().toString());

            JsonArray ingredients = new JsonArray();
            ingredients.add(source);
            ingredients.add(ingredient);

            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("category", "building");
            json.addProperty("group", group);
            json.add("ingredients", ingredients);
            json.addProperty("result", warpId.toString());

            String recipePath = sourceId.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)
                    || sourceId.getNamespace().equals(Marioverse.MOD_ID)
                    ? "warp_" + sourceId.getPath() : sourceId.getNamespace() + "_warp_" + sourceId.getPath();
            ResourceLocation recipeId = ResourceLocation
                    .fromNamespaceAndPath(Marioverse.MOD_ID, "recipe/" + recipePath + ".json");
            this.recipes.put(recipeId, json.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
