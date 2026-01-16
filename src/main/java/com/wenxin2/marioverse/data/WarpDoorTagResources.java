package com.wenxin2.marioverse.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class WarpDoorTagResources implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackLocationInfo location;
    private final Path root;

    public WarpDoorTagResources(PackLocationInfo location, Path path) {
        this.location = location;
        this.root = path;
    }

    private static final Set<TagKey<Block>> BLOCK_TAGS = Set.of(
            TagRegistry.WARP_DOOR_BLOCKS,
            BlockTags.DOORS,
            BlockTags.MINEABLE_WITH_PICKAXE,
            BlockTags.WOODEN_DOORS
    );

    private static final Set<TagKey<Item>> ITEM_TAGS = Set.of(
            TagRegistry.WARP_DOOR_ITEMS,
            ItemTags.DOORS,
            ItemTags.WOODEN_DOORS
    );

    @Override
    public IoSupplier<InputStream> getRootResource(String... path) {
        if (path.length == 1 && path[0].equals("pack.mcmeta")) {
            JsonObject root = new JsonObject();
            JsonObject pack = new JsonObject();

            pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
            pack.addProperty("description", "Warp Door & Trapdoor Tags");

            root.add("pack", pack);

            byte[] bytes = root.toString().getBytes(StandardCharsets.UTF_8);
            return () -> new ByteArrayInputStream(bytes);
        }
        return null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type != PackType.SERVER_DATA)
            return null;
        LOGGER.info("Resource requested: {}", location);

        for (TagKey<Block> tag : BLOCK_TAGS) {
            if (location.equals(tag.location().withPrefix("tags/block/").withSuffix(".json"))) {
                return buildBlockTag(tag);
            }
        }

        for (TagKey<Item> tag : ITEM_TAGS) {
            if (location.equals(tag.location().withPrefix("tags/item/").withSuffix(".json"))) {
                return buildItemTag(tag);
            }
        }
        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (type != PackType.SERVER_DATA)
            return;
        LOGGER.info("Listing resources for namespace={} path={}", namespace, path);

        if (path.equals("tags/block")) {
            for (TagKey<Block> tag : BLOCK_TAGS) {
                ResourceLocation id = tag.location();
                if (!id.getNamespace().equals(namespace))
                    continue;

                ResourceLocation out = ResourceLocation
                        .fromNamespaceAndPath(namespace, "tags/block/" + id.getPath() + ".json");

                LOGGER.info("Exposing block tag {}", out);
                output.accept(out, () -> buildBlockTag(tag).get());
            }
        }

        if (path.equals("tags/item")) {
            for (TagKey<Item> tag : ITEM_TAGS) {
                ResourceLocation id = tag.location();
                if (!id.getNamespace().equals(namespace))
                    continue;

                ResourceLocation out = ResourceLocation
                        .fromNamespaceAndPath(namespace, "tags/item/" + id.getPath() + ".json");

                LOGGER.info("Exposing item tag {}", out);

                output.accept(out, () -> buildItemTag(tag).get());
            }
        }
    }

    @NotNull
    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.SERVER_DATA
                ? Set.of("minecraft", Marioverse.MOD_ID)
                : Set.of();
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
        IoSupplier<InputStream> iosupplier = this.getRootResource(new String[]{"pack.mcmeta"});
        if (iosupplier == null) {
            return null;
        } else {
            Object object;
            try (InputStream inputstream = iosupplier.get()) {
                object = getMetadataFromStream(serializer, inputstream);
            }
            return (T)object;
        }
    }

    @Override
    public PackLocationInfo location() {
        return this.location;
    }

    @Override
    public void close() {
    }

    private IoSupplier<InputStream> buildBlockTag(TagKey<Block> tag) {
        JsonObject json = new JsonObject();
        JsonArray values = new JsonArray();

        for (Map.Entry<Block, Block> entry : RegistryEventHandlers.WARP_DOORS.entrySet()) {
            Block source = entry.getKey();
            Block warp = entry.getValue();

            if (source instanceof DoorBlock door) {
                boolean isWooden = door.type().canOpenByHand();

                if (tag.equals(BlockTags.DOORS) && isWooden)
                    continue;
                if (tag.equals(BlockTags.MINEABLE_WITH_PICKAXE) && isWooden)
                    continue;
                if (tag.equals(BlockTags.WOODEN_DOORS) && !isWooden)
                    continue;

                values.add(BuiltInRegistries.BLOCK.getKey(warp).toString());
            }
        }

        json.addProperty("replace", false);
        json.add("values", values);

        byte[] data = json.toString().getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(data);
    }

    private IoSupplier<InputStream> buildItemTag(TagKey<Item> tag) {
        JsonObject json = new JsonObject();
        JsonArray values = new JsonArray();

        for (Map.Entry<Block, Block> entry : RegistryEventHandlers.WARP_DOORS.entrySet()) {
            Block source = entry.getKey();
            Block warp = entry.getValue();

            if (source instanceof DoorBlock door) {
                boolean isWooden = door.type().canOpenByHand();

                if (tag.equals(ItemTags.DOORS) && isWooden)
                    continue;
                if (tag.equals(ItemTags.WOODEN_DOORS) && !isWooden)
                    continue;

                values.add(BuiltInRegistries.ITEM.getKey(warp.asItem()).toString());
            }
        }

        json.addProperty("replace", false);
        json.add("values", values);

        byte[] data = json.toString().getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(data);
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
}