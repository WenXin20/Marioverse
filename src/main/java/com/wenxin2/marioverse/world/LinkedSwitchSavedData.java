package com.wenxin2.marioverse.world;

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
    public final Map<BlockPos, Map<ChunkPos, Set<BlockPos>>> switchMap = new HashMap<>();

    public static LinkedSwitchSavedData create() {
        return new LinkedSwitchSavedData();
    }

    public static LinkedSwitchSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        LinkedSwitchSavedData data = create();

        ListTag switches = tag.getList("Switches", Tag.TAG_COMPOUND);

        for (Tag t : switches) {
            CompoundTag switchTag = (CompoundTag) t;

            BlockPos switchPos = BlockPos.of(switchTag.getLong("SwitchPos"));
            Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

            ListTag chunks = switchTag.getList("Chunks", Tag.TAG_COMPOUND);
            for (Tag ct : chunks) {
                CompoundTag c = (CompoundTag) ct;
                ChunkPos chunkPos = new ChunkPos(c.getInt("X"), c.getInt("Z"));

                Set<BlockPos> set = new HashSet<>();
                ListTag positions = c.getList("Pos", Tag.TAG_LONG);
                for (Tag p : positions) {
                    set.add(BlockPos.of(((LongTag) p).getAsLong()));
                }

                chunkMap.put(chunkPos, set);
            }

            data.switchMap.put(switchPos, chunkMap);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {

        ListTag switches = new ListTag();

        for (var entry : switchMap.entrySet()) {

            CompoundTag switchTag = new CompoundTag();
            switchTag.putLong("SwitchPos", entry.getKey().asLong());

            ListTag chunks = new ListTag();

            for (var chunkEntry : entry.getValue().entrySet()) {
                CompoundTag chunkTag = new CompoundTag();

                chunkTag.putInt("X", chunkEntry.getKey().x);
                chunkTag.putInt("Z", chunkEntry.getKey().z);

                ListTag posList = new ListTag();
                for (BlockPos pos : chunkEntry.getValue()) {
                    posList.add(LongTag.valueOf(pos.asLong()));
                }

                chunkTag.put("Pos", posList);
                chunks.add(chunkTag);
            }

            switchTag.put("Chunks", chunks);
            switches.add(switchTag);
        }

        tag.put("Switches", switches);
        return tag;
    }

    public static LinkedSwitchSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(LinkedSwitchSavedData::create, LinkedSwitchSavedData::load),
                ID
        );
    }

    public void ensureSwitchExists(BlockPos switchPos) {
        switchMap.computeIfAbsent(switchPos, k -> new HashMap<>());
    }

    public void link(ServerLevel level, BlockPos switchPos, BlockPos blockPos) {
        this.unlink(blockPos);
        Map<ChunkPos, Set<BlockPos>> chunkMap = switchMap.computeIfAbsent(switchPos, k -> new HashMap<>());
        chunkMap.computeIfAbsent(new ChunkPos(blockPos), k -> new HashSet<>()).add(blockPos);
        this.setDirty();
    }

    public void unlink(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);

        for (Iterator<Map.Entry<BlockPos, Map<ChunkPos, Set<BlockPos>>>> iterator =
             switchMap.entrySet().iterator(); iterator.hasNext();) {

            var switchEntry = iterator.next();
            Map<ChunkPos, Set<BlockPos>> chunkMap = switchEntry.getValue();

            Set<BlockPos> set = chunkMap.get(chunkPos);

            if (set != null && set.remove(blockPos)) {

                if (set.isEmpty())
                    chunkMap.remove(chunkPos);

                if (chunkMap.isEmpty())
                    iterator.remove();

                this.setDirty();
                return;
            }
        }
    }

    public Set<BlockPos> getPositions(BlockPos switchPos, ChunkPos chunkPos) {
        Map<ChunkPos, Set<BlockPos>> chunkMap = switchMap.get(switchPos);
        if (chunkMap == null)
            return Set.of();
        return chunkMap.getOrDefault(chunkPos, Set.of());
    }

    public Collection<Set<BlockPos>> allPositions(BlockPos switchPos) {
        Map<ChunkPos, Set<BlockPos>> chunkMap = switchMap.get(switchPos);
        if (chunkMap == null)
            return List.of();
        return chunkMap.values();
    }

    public boolean hasSwitch(BlockPos switchPos) {
        return switchMap.containsKey(switchPos);
    }
}