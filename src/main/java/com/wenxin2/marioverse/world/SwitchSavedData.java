package com.wenxin2.marioverse.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

public class SwitchSavedData extends SavedData {
    public static final String ID = "marioverse_switch_state";
    private final Map<ChunkPos, Set<BlockPos>> blocksMap = new HashMap<>();

    private boolean isOn;

    public static SwitchSavedData create() {
        return new SwitchSavedData();
    }

    public static SwitchSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        SwitchSavedData data = create();
        data.isOn = tag.getBoolean("IsOn");

        ListTag chunks = tag.getList("Chunks", Tag.TAG_COMPOUND);
        for (Tag t : chunks) {
            CompoundTag c = (CompoundTag) t;
            ChunkPos chunkPos = new ChunkPos(c.getInt("X"), c.getInt("Z"));

            Set<BlockPos> set = new HashSet<>();
            ListTag positions = c.getList("Pos", Tag.TAG_LONG);
            for (Tag p : positions) {
                set.add(BlockPos.of(((LongTag) p).getAsLong()));
            }

            data.blocksMap.put(chunkPos, set);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putBoolean("IsOn", isOn);

        ListTag chunks = new ListTag();
        for (var entry : blocksMap.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putInt("X", entry.getKey().x);
            c.putInt("Z", entry.getKey().z);

            ListTag posList = new ListTag();
            for (BlockPos pos : entry.getValue()) {
                posList.add(LongTag.valueOf(pos.asLong()));
            }
            c.put("Pos", posList);
            chunks.add(c);
        }

        tag.put("Chunks", chunks);
        return tag;
    }

    public boolean isActive() {
        return isOn;
    }

    public void setOn(boolean on) {
        if (this.isOn != on) {
            this.isOn = on;
            this.setDirty();
        }
    }

    public static SwitchSavedData get(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(SwitchSavedData::create, SwitchSavedData::load),
                        SwitchSavedData.ID);
    }

    public void add(BlockPos pos) {
        blocksMap.computeIfAbsent(new ChunkPos(pos), k -> new HashSet<>()).add(pos);
        setDirty();
    }

    public void remove(BlockPos pos) {
        ChunkPos cp = new ChunkPos(pos);
        Set<BlockPos> set = blocksMap.get(cp);
        if (set != null && set.remove(pos)) {
            if (set.isEmpty()) blocksMap.remove(cp);
            setDirty();
        }
    }

    public Set<BlockPos> getPositions(ChunkPos chunkPos) {
        return blocksMap.getOrDefault(chunkPos, Set.of());
    }

    public Collection<Set<BlockPos>> allPositions() {
        return blocksMap.values();
    }
}
