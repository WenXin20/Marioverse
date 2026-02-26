package com.wenxin2.marioverse.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

public class LinkedSwitchSavedData extends SavedData {
    public static final String ID = "marioverse_linked_switches";
    private final Map<ChunkPos, Map<BlockPos, Set<BlockPos>>> blocksMap = new HashMap<>();

    public static LinkedSwitchSavedData create() {
        return new LinkedSwitchSavedData();
    }

    public static LinkedSwitchSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        LinkedSwitchSavedData data = create();
        ListTag chunks = tag.getList("Chunks", Tag.TAG_COMPOUND);

        for (Tag t : chunks) {
            CompoundTag chunkTag = (CompoundTag) t;

            ChunkPos chunkPos = new ChunkPos(chunkTag.getInt("X"), chunkTag.getInt("Z"));
            Map<BlockPos, Set<BlockPos>> switchMap = new HashMap<>();
            ListTag switches = chunkTag.getList("Switches", Tag.TAG_COMPOUND);

            for (Tag st : switches) {
                CompoundTag switchTag = (CompoundTag) st;
                BlockPos switchPos = BlockPos.of(switchTag.getLong("SwitchPos"));

                Set<BlockPos> positions = new HashSet<>();
                ListTag posList = switchTag.getList("Pos", Tag.TAG_LONG);

                for (Tag p : posList) {
                    positions.add(BlockPos.of(((LongTag) p).getAsLong()));
                }
                switchMap.put(switchPos, positions);
            }
            data.blocksMap.put(chunkPos, switchMap);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag chunks = new ListTag();

        for (var chunkEntry : blocksMap.entrySet()) {
            CompoundTag chunkTag = new CompoundTag();
            chunkTag.putInt("X", chunkEntry.getKey().x);
            chunkTag.putInt("Z", chunkEntry.getKey().z);
            ListTag switches = new ListTag();

            for (var switchEntry : chunkEntry.getValue().entrySet()) {
                CompoundTag switchTag = new CompoundTag();
                switchTag.putLong("SwitchPos", switchEntry.getKey().asLong());

                ListTag posList = new ListTag();
                for (BlockPos pos : switchEntry.getValue()) {
                    posList.add(LongTag.valueOf(pos.asLong()));
                }
                switchTag.put("Pos", posList);
                switches.add(switchTag);
            }
            chunkTag.put("Switches", switches);
            chunks.add(chunkTag);
        }
        tag.put("Chunks", chunks);
        return tag;
    }

    public static LinkedSwitchSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData
                .Factory<>(LinkedSwitchSavedData::create, LinkedSwitchSavedData::load), ID);
    }

    public boolean isLinked(BlockPos switchPos, BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);

        Map<BlockPos, Set<BlockPos>> switchMap = blocksMap.get(chunkPos);
        if (switchMap == null)
            return false;

        Set<BlockPos> set = switchMap.get(switchPos);
        if (set == null)
            return false;

        return set.contains(blockPos);
    }

    public boolean isLinked(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);

        Map<BlockPos, Set<BlockPos>> switchMap = blocksMap.get(chunkPos);
        if (switchMap == null)
            return false;

        for (Set<BlockPos> set : switchMap.values()) {
            if (set.contains(blockPos))
                return true;
        }

        return false;
    }

    public void link(BlockPos switchPos, BlockPos blockPos) {
        this.unlink(blockPos);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        blocksMap.computeIfAbsent(chunkPos, k -> new HashMap<>())
                .computeIfAbsent(switchPos, k -> new HashSet<>())
                .add(blockPos);

        this.setDirty();
    }

    public void unlink(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);

        Map<BlockPos, Set<BlockPos>> switchMap = blocksMap.get(chunkPos);
        if (switchMap == null) return;

        for (Iterator<Map.Entry<BlockPos, Set<BlockPos>>> iterator = switchMap.entrySet().iterator(); iterator.hasNext();) {
            var entry = iterator.next();
            Set<BlockPos> set = entry.getValue();

            if (set.remove(blockPos)) {

                if (set.isEmpty())
                    iterator.remove();

                if (switchMap.isEmpty())
                    blocksMap.remove(chunkPos);

                this.setDirty();
                return;
            }
        }
    }

    public Map<BlockPos, Set<BlockPos>> getChunk(ChunkPos chunkPos) {
        return blocksMap.getOrDefault(chunkPos, Map.of());
    }

    public Collection<Set<BlockPos>> allPositions(BlockPos switchPos) {

        List<Set<BlockPos>> result = new ArrayList<>();

        for (Map<BlockPos, Set<BlockPos>> switchMap : blocksMap.values()) {
            Set<BlockPos> set = switchMap.get(switchPos);
            if (set != null)
                result.add(set);
        }

        return result;
    }
}