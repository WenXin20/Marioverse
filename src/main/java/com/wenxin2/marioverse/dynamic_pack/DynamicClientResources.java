package com.wenxin2.marioverse.dynamic_pack;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class DynamicClientResources implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<ResourceLocation, byte[]> resources = new HashMap<>();
    private final PackLocationInfo location;

    public DynamicClientResources(PackLocationInfo location) {
        this.location = location;
        generateBlockstates();
        generateItemModels(); // optional, if you want
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... path) {
        if (path.length == 1 && path[0].equals("pack.mcmeta")) {
            JsonObject root = new JsonObject();
            JsonObject pack = new JsonObject();

            pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES));
            pack.addProperty("description", "Dynamic Client Resources");

            root.add("pack", pack);

            byte[] bytes = root.toString().getBytes(StandardCharsets.UTF_8);
            return () -> new ByteArrayInputStream(bytes);
        }
        return null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type != PackType.CLIENT_RESOURCES)
            return null;

        byte[] data = resources.get(location);
        return data != null ? () -> new ByteArrayInputStream(data) : null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (type != PackType.CLIENT_RESOURCES && !namespace.equals(Marioverse.MOD_ID))
            return;

        for (Map.Entry<ResourceLocation, byte[]> entry : resources.entrySet()) {
            ResourceLocation blockID = entry.getKey();

            if (!blockID.getNamespace().equals(namespace))
                continue;

            if (!blockID.getPath().startsWith(path))
                continue;

            output.accept(blockID, () -> new ByteArrayInputStream(entry.getValue()));
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? Set.of(Marioverse.MOD_ID) : Set.of();
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
        IoSupplier<InputStream> iosupplier = this.getRootResource("pack.mcmeta");
        if (iosupplier == null)
            return null;
        else {
            Object object;
            try (InputStream inputstream = iosupplier.get()) {
                object = getMetadataFromStream(serializer, inputstream);
            }
            return (T)object;
        }
    }

    @Nullable
    public static <T> T getMetadataFromStream(MetadataSectionSerializer<T> serializer, InputStream input) {
        JsonObject jsonobject;
        try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            jsonobject = GsonHelper.parse(bufferedreader);
        } catch (Exception exception1) {
            LOGGER.error("Couldn't load {} metadata", serializer.getMetadataSectionName(), exception1);
            return null;
        }

        if (!jsonobject.has(serializer.getMetadataSectionName())) {
            return null;
        } else {
            try {
                return serializer.fromJson(GsonHelper.getAsJsonObject(jsonobject, serializer.getMetadataSectionName()));
            } catch (Exception exception) {
                LOGGER.error("Couldn't load {} metadata", serializer.getMetadataSectionName(), exception);
                return null;
            }
        }
    }

    @Override
    public PackLocationInfo location() {
        return this.location;
    }

    @Override
    public void close() {
    }

    private void generateBlockstates() {
        JsonObject json = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject model = new JsonObject();

        model.addProperty("model", "minecraft:block/air");
        variants.add("", model);
        json.add("variants", variants);

        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);

        for (Block warp : RegistryEventHandlers.WARP_DOORS.values()) {
            ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(warp);

            ResourceLocation blockstateId = ResourceLocation
                    .fromNamespaceAndPath(blockID.getNamespace(), "blockstates/" + blockID.getPath() + ".json");

            resources.put(blockstateId, bytes);
        }
    }

    private void generateItemModels() {
        for (Block warp : RegistryEventHandlers.WARP_DOORS.values()) {
            ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(warp);

            ResourceLocation modelId = ResourceLocation
                    .fromNamespaceAndPath(blockID.getNamespace(), "models/item/" + blockID.getPath() + ".json");

            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:item/generated");

            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", "minecraft:item/barrier");
            json.add("textures", textures);

            resources.put(modelId, json.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
