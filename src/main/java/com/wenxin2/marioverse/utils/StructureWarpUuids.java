package com.wenxin2.marioverse.utils;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class StructureWarpUuids {
    private static final String UUID_KEY = "UUID";
    private static final String WARP_UUID_KEY = "WarpUUID";
    private static final String[] STALE_POSITION_KEYS = {"WarpPos", "WarpTarget", "WarpSubLevel", "WarpSubLevelPos"};
    private static final ThreadLocal<Long> PLACEMENT_SEED = new ThreadLocal<>();

    public static long getStructureSeed(long worldSeed, @Nullable ResourceLocation structure, ChunkPos chunkPos) {
        long structureHash = structure != null ? structure.toString().hashCode() : 0L;
        return worldSeed ^ (structureHash * 0x9E3779B97F4A7C15L) ^ (chunkPos.toLong() * 0xC2B2AE3D27D4EB4FL);
    }

    public static <T> T callWithSeed(long seed, Supplier<T> action) {
        Long previous = PLACEMENT_SEED.get();
        PLACEMENT_SEED.set(seed);

        try {
            return action.get();
        } finally {
            if (previous != null)
                PLACEMENT_SEED.set(previous);
            else PLACEMENT_SEED.remove();
        }
    }

    public static <T> T callWithPlacementSeed(Supplier<T> action) {
        if (PLACEMENT_SEED.get() != null)
            return action.get();
        return StructureWarpUuids.callWithSeed(UUID.randomUUID().getMostSignificantBits(), action);
    }

    public static CompoundTag remapBlockEntityTag(CompoundTag tag) {
        Long seed = PLACEMENT_SEED.get();
        if (seed == null || !(tag.hasUUID(UUID_KEY) || tag.hasUUID(WARP_UUID_KEY)))
            return tag;

        CompoundTag remapped = tag.copy();
        StructureWarpUuids.remapKey(remapped, UUID_KEY, seed);
        StructureWarpUuids.remapKey(remapped, WARP_UUID_KEY, seed);
        StructureWarpUuids.removeStalePositions(remapped);
        return remapped;
    }

    public static boolean remapEntityTag(CompoundTag tag) {
        Long seed = PLACEMENT_SEED.get();
        if (seed == null || !tag.hasUUID(UUID_KEY))
            return false;

        boolean isPainting = EntityType.by(tag).map(PaintingRipples::isRipplePaintingType).orElse(false);
        if (!tag.hasUUID(WARP_UUID_KEY) && !isPainting)
            return false;

        StructureWarpUuids.remapKey(tag, UUID_KEY, seed);
        StructureWarpUuids.remapKey(tag, WARP_UUID_KEY, seed);
        StructureWarpUuids.removeStalePositions(tag);
        return true;
    }

    private static void remapKey(CompoundTag tag, String key, long seed) {
        if (tag.hasUUID(key))
            tag.putUUID(key, StructureWarpUuids.remap(tag.getUUID(key), seed));
    }

    private static void removeStalePositions(CompoundTag tag) {
        for (String key : STALE_POSITION_KEYS)
            tag.remove(key);
    }

    private static UUID remap(UUID uuid, long seed) {
        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        buffer.putLong(seed);
        return UUID.nameUUIDFromBytes(buffer.array());
    }
}
