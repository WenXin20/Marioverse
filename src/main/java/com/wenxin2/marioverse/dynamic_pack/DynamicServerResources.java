package com.wenxin2.marioverse.dynamic_pack;

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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class DynamicServerResources implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<ResourceLocation, byte[]> lootTables = new HashMap<>();
    private final PackLocationInfo location;

    public DynamicServerResources(PackLocationInfo location) {
        this.location = location;
        this.generateLootTables();
    }

    private static final Set<TagKey<Block>> DOOR_BLOCK_TAGS = Set.of(
            TagRegistry.WARP_DOOR_BLOCKS,
            BlockTags.DOORS,
            BlockTags.MINEABLE_WITH_PICKAXE,
            BlockTags.WOODEN_DOORS
    );

    private static final Set<TagKey<Item>> DOOR_ITEM_TAGS = Set.of(
            TagRegistry.WARP_DOOR_ITEMS,
            ItemTags.DOORS,
            ItemTags.WOODEN_DOORS
    );

    private static final Set<TagKey<Block>> TRAPDOOR_BLOCK_TAGS = Set.of(
            TagRegistry.WARP_TRAPDOOR_BLOCKS,
            BlockTags.TRAPDOORS,
            BlockTags.MINEABLE_WITH_PICKAXE,
            BlockTags.WOODEN_TRAPDOORS
    );

    private static final Set<TagKey<Item>> TRAPDOOR_ITEM_TAGS = Set.of(
            TagRegistry.WARP_TRAPDOOR_ITEMS,
            ItemTags.TRAPDOORS,
            ItemTags.WOODEN_TRAPDOORS
    );

    private void generateLootTables() {
        for (Map.Entry<Block, Block> entry : RegistryEventHandlers.WARP_DOORS.entrySet()) {
            ResourceLocation warpId   = BuiltInRegistries.BLOCK.getKey(entry.getValue());

            ResourceLocation lootId = ResourceLocation
                    .fromNamespaceAndPath(Marioverse.MOD_ID, "blocks/" + warpId.getPath() + ".json");

            JsonObject json = createDoorLootTable(warpId);
            lootTables.put(lootId, json.toString().getBytes(StandardCharsets.UTF_8));
        }

        for (Map.Entry<Block, Block> entry : RegistryEventHandlers.WARP_TRAPDOORS.entrySet()) {
            ResourceLocation warpId   = BuiltInRegistries.BLOCK.getKey(entry.getValue());

            ResourceLocation lootId = ResourceLocation
                    .fromNamespaceAndPath(Marioverse.MOD_ID, "blocks/" + warpId.getPath() + ".json");

            JsonObject json = createTrapDoorLootTable(warpId);
            lootTables.put(lootId, json.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... path) {
        if (path.length == 1 && path[0].equals("pack.mcmeta")) {
            JsonObject root = new JsonObject();
            JsonObject pack = new JsonObject();

            pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
            pack.addProperty("description", "Marioverse dynamic data for warp doors & trapdoors");

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

        for (TagKey<Block> tag : DOOR_BLOCK_TAGS) {
            if (location.equals(tag.location().withPrefix("tags/block/").withSuffix(".json"))) {
                return buildDoorBlockTag(tag);
            }
        }

        for (TagKey<Item> tag : DOOR_ITEM_TAGS) {
            if (location.equals(tag.location().withPrefix("tags/item/").withSuffix(".json"))) {
                return buildDoorItemTag(tag);
            }
        }

        for (TagKey<Block> tag : TRAPDOOR_BLOCK_TAGS) {
            if (location.equals(tag.location().withPrefix("tags/block/").withSuffix(".json"))) {
                return buildTrapDoorBlockTag(tag);
            }
        }

        for (TagKey<Item> tag : TRAPDOOR_ITEM_TAGS) {
            if (location.equals(tag.location().withPrefix("tags/item/").withSuffix(".json"))) {
                return buildTrapDoorItemTag(tag);
            }
        }

        byte[] data = lootTables.get(location);
        if (data != null)
            return () -> new ByteArrayInputStream(data);

        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (type != PackType.SERVER_DATA)
            return;

        if (path.equals("tags/block")) {
            for (TagKey<Block> tag : DOOR_BLOCK_TAGS) {
                ResourceLocation id = tag.location();
                if (!id.getNamespace().equals(namespace))
                    continue;

                ResourceLocation out = ResourceLocation
                        .fromNamespaceAndPath(namespace, "tags/block/" + id.getPath() + ".json");

                output.accept(out, () -> buildDoorBlockTag(tag).get());
            }

            for (TagKey<Block> tag : TRAPDOOR_BLOCK_TAGS) {
                ResourceLocation id = tag.location();
                if (!id.getNamespace().equals(namespace))
                    continue;

                ResourceLocation out = ResourceLocation
                        .fromNamespaceAndPath(namespace, "tags/block/" + id.getPath() + ".json");

                output.accept(out, () -> buildTrapDoorBlockTag(tag).get());
            }
        }

        if (path.equals("tags/item")) {
            for (TagKey<Item> tag : DOOR_ITEM_TAGS) {
                ResourceLocation id = tag.location();
                if (!id.getNamespace().equals(namespace))
                    continue;

                ResourceLocation out = ResourceLocation
                        .fromNamespaceAndPath(namespace, "tags/item/" + id.getPath() + ".json");

                output.accept(out, () -> buildDoorItemTag(tag).get());
            }
        }

        if (path.equals("tags/item")) {
            for (TagKey<Item> tag : TRAPDOOR_ITEM_TAGS) {
                ResourceLocation id = tag.location();
                if (!id.getNamespace().equals(namespace))
                    continue;

                ResourceLocation out = ResourceLocation
                        .fromNamespaceAndPath(namespace, "tags/item/" + id.getPath() + ".json");

                output.accept(out, () -> buildTrapDoorItemTag(tag).get());
            }
        }

        if (path.equals("loot_table")) {
            for (Map.Entry<ResourceLocation, byte[]> entry : lootTables.entrySet()) {
                ResourceLocation blockID = entry.getKey();
                if (!blockID.getNamespace().equals(namespace))
                    continue;

                ResourceLocation out = ResourceLocation
                        .fromNamespaceAndPath(namespace, "loot_table/" + blockID.getPath());

                output.accept(out, () -> new ByteArrayInputStream(entry.getValue()));
            }
        }
    }

    @NotNull
    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.SERVER_DATA ? Set.of("minecraft", Marioverse.MOD_ID) : Set.of();
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

    private IoSupplier<InputStream> buildDoorBlockTag(TagKey<Block> tag) {
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

    private IoSupplier<InputStream> buildTrapDoorBlockTag(TagKey<Block> tag) {
        JsonObject json = new JsonObject();
        JsonArray values = new JsonArray();

        for (Map.Entry<Block, Block> entry : RegistryEventHandlers.WARP_TRAPDOORS.entrySet()) {
            Block source = entry.getKey();
            Block warp = entry.getValue();

            if (source instanceof TrapDoorBlock trapDoor) {
                boolean isWooden = trapDoor.getType().canOpenByHand();

                if (tag.equals(BlockTags.TRAPDOORS) && isWooden)
                    continue;
                if (tag.equals(BlockTags.MINEABLE_WITH_PICKAXE) && isWooden)
                    continue;
                if (tag.equals(BlockTags.WOODEN_TRAPDOORS) && !isWooden)
                    continue;

                values.add(BuiltInRegistries.BLOCK.getKey(warp).toString());
            }
        }

        json.addProperty("replace", false);
        json.add("values", values);

        byte[] data = json.toString().getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(data);
    }

    private IoSupplier<InputStream> buildDoorItemTag(TagKey<Item> tag) {
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

    private IoSupplier<InputStream> buildTrapDoorItemTag(TagKey<Item> tag) {
        JsonObject json = new JsonObject();
        JsonArray values = new JsonArray();

        for (Map.Entry<Block, Block> entry : RegistryEventHandlers.WARP_TRAPDOORS.entrySet()) {
            Block source = entry.getKey();
            Block warp = entry.getValue();

            if (source instanceof TrapDoorBlock trapDoor) {
                boolean isWooden = trapDoor.getType().canOpenByHand();

                if (tag.equals(ItemTags.TRAPDOORS) && isWooden)
                    continue;
                if (tag.equals(ItemTags.WOODEN_TRAPDOORS) && !isWooden)
                    continue;

                values.add(BuiltInRegistries.ITEM.getKey(warp.asItem()).toString());
            }
        }

        json.addProperty("replace", false);
        json.add("values", values);

        byte[] data = json.toString().getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(data);
    }

    private static JsonObject createDoorLootTable(ResourceLocation blockId) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:block");

        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);

        JsonArray poolConditions = new JsonArray();
        JsonObject survivesExplosion = new JsonObject();
        survivesExplosion.addProperty("condition", "minecraft:survives_explosion");
        poolConditions.add(survivesExplosion);
        pool.add("conditions", poolConditions);

        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", blockId.toString());

        JsonArray entryConditions = new JsonArray();
        JsonObject blockStateCondition = new JsonObject();
        blockStateCondition.addProperty("block", blockId.toString());
        blockStateCondition.addProperty("condition", "minecraft:block_state_property");

        JsonObject properties = new JsonObject();
        properties.addProperty("half", "lower");
        blockStateCondition.add("properties", properties);

        entryConditions.add(blockStateCondition);
        entry.add("conditions", entryConditions);

        JsonArray entries = new JsonArray();
        entries.add(entry);
        pool.add("entries", entries);

        JsonArray pools = new JsonArray();
        pools.add(pool);
        root.add("pools", pools);

        root.addProperty("random_sequence", "marioverse:blocks/" + blockId.getPath());

        return root;
    }

    private static JsonObject createTrapDoorLootTable(ResourceLocation blockId) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:block");

        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);

        JsonArray poolConditions = new JsonArray();
        JsonObject survivesExplosion = new JsonObject();
        survivesExplosion.addProperty("condition", "minecraft:survives_explosion");
        poolConditions.add(survivesExplosion);
        pool.add("conditions", poolConditions);

        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", blockId.toString());

        JsonArray entries = new JsonArray();
        entries.add(entry);
        pool.add("entries", entries);

        JsonArray pools = new JsonArray();
        pools.add(pool);
        root.add("pools", pools);

        root.addProperty("random_sequence", "marioverse:blocks/" + blockId.getPath());

        return root;
    }
}