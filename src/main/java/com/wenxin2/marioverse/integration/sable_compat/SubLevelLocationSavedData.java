package com.wenxin2.marioverse.integration.sable_compat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

public class SubLevelLocationSavedData extends SavedData {
    public static final String FILE_ID = "marioverse_sub_level_locations";
    private static final String LOCATIONS = "Locations";
    private static final String SUB_LEVEL = "SubLevel";
    private static final String CHUNK_BOUNDS = "ChunkBounds";
    private final Map<UUID, ChunkBounds> locations = new HashMap<>();

    public record ChunkBounds(int minX, int minZ, int maxX, int maxZ) {}

    public static SubLevelLocationSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(SubLevelLocationSavedData::new,
                SubLevelLocationSavedData::load, null), FILE_ID);
    }

    public void setLocation(UUID subLevel, ChunkBounds bounds) {
        if (!bounds.equals(this.locations.get(subLevel))) {
            this.locations.put(subLevel, bounds);
            this.setDirty();
        }
    }

    public void removeLocation(UUID subLevel) {
        if (this.locations.remove(subLevel) != null)
            this.setDirty();
    }

    @Nullable
    public ChunkBounds getLocation(UUID subLevel) {
        return this.locations.get(subLevel);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();

        for (Map.Entry<UUID, ChunkBounds> entry : this.locations.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            ChunkBounds bounds = entry.getValue();

            entryTag.putUUID(SUB_LEVEL, entry.getKey());
            entryTag.putIntArray(CHUNK_BOUNDS, new int[] {bounds.minX(), bounds.minZ(), bounds.maxX(), bounds.maxZ()});
            list.add(entryTag);
        }

        tag.put(LOCATIONS, list);
        return tag;
    }

    private static SubLevelLocationSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        SubLevelLocationSavedData data = new SubLevelLocationSavedData();
        ListTag list = tag.getList(LOCATIONS, Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            int[] bounds = entryTag.getIntArray(CHUNK_BOUNDS);

            if (bounds.length == 4)
                data.locations.put(entryTag.getUUID(SUB_LEVEL), new ChunkBounds(bounds[0], bounds[1], bounds[2], bounds[3]));
        }
        return data;
    }
}
