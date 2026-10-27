package com.wenxin2.marioverse.world;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class PendingWarpLinkSavedData extends SavedData {
    public static final String ID = "marioverse_pending_warp_links";
    private static final String LINKS = "Links";
    private static final String ENTITY = "Entity";
    private static final String DESTINATION_POS = "DestinationPos";
    private static final String DESTINATION_DIM = "DestinationDim";
    private static final String WARP_UUID = "WarpUUID";
    private static final String WARP_TARGET_POS = "WarpTargetPos";
    private static final String WARP_TARGET_DIRECTION = "WarpTargetDirection";
    private static final String WARP_TARGET_WIDTH = "WarpTargetWidth";
    private final Map<UUID, PendingLink> links = new HashMap<>();

    public record PendingLink(BlockPos destinationPos, @Nullable ResourceKey<Level> destinationDim, UUID warpUuid,
                              @Nullable WarpLinkableEntity.WarpTarget warpTarget) {}

    public static PendingWarpLinkSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(PendingWarpLinkSavedData::new,
                PendingWarpLinkSavedData::load, null), ID);
    }

    public void addLink(UUID entity, PendingLink link) {
        this.links.put(entity, link);
        this.setDirty();
    }

    public void applyLink(Entity entity) {
        if (this.links.isEmpty() || !(entity instanceof WarpLinkableEntity warpEntity))
            return;

        PendingLink link = this.links.remove(entity.getUUID());
        if (link == null)
            return;

        warpEntity.mv$setDestinationPos(link.destinationPos());
        if (link.destinationDim() != null)
            warpEntity.mv$setDestinationDim(link.destinationDim());
        warpEntity.mv$setWarpUuid(link.warpUuid());
        if (link.warpTarget() != null)
            warpEntity.mv$setWarpTarget(link.warpTarget());
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();

        for (Map.Entry<UUID, PendingLink> entry : this.links.entrySet()) {
            CompoundTag linkTag = new CompoundTag();
            PendingLink link = entry.getValue();

            linkTag.putUUID(ENTITY, entry.getKey());
            linkTag.putLong(DESTINATION_POS, link.destinationPos().asLong());
            if (link.destinationDim() != null)
                linkTag.putString(DESTINATION_DIM, link.destinationDim().location().toString());
            linkTag.putUUID(WARP_UUID, link.warpUuid());
            if (link.warpTarget() != null) {
                linkTag.putLong(WARP_TARGET_POS, link.warpTarget().pos().asLong());
                linkTag.putInt(WARP_TARGET_DIRECTION, link.warpTarget().direction().get3DDataValue());
                linkTag.putInt(WARP_TARGET_WIDTH, link.warpTarget().width());
            }
            list.add(linkTag);
        }

        tag.put(LINKS, list);
        return tag;
    }

    private static PendingWarpLinkSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        PendingWarpLinkSavedData data = new PendingWarpLinkSavedData();
        ListTag list = tag.getList(LINKS, Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag linkTag = list.getCompound(i);
            ResourceLocation dimLocation = linkTag.contains(DESTINATION_DIM) ? ResourceLocation.tryParse(linkTag.getString(DESTINATION_DIM)) : null;
            ResourceKey<Level> dim = dimLocation != null ? ResourceKey.create(Registries.DIMENSION, dimLocation) : null;
            WarpLinkableEntity.WarpTarget target = linkTag.contains(WARP_TARGET_POS) ? new WarpLinkableEntity.WarpTarget(
                    BlockPos.of(linkTag.getLong(WARP_TARGET_POS)), Direction.from3DDataValue(linkTag.getInt(WARP_TARGET_DIRECTION)),
                    linkTag.getInt(WARP_TARGET_WIDTH)) : null;

            data.links.put(linkTag.getUUID(ENTITY), new PendingLink(BlockPos.of(linkTag.getLong(DESTINATION_POS)), dim,
                    linkTag.getUUID(WARP_UUID), target));
        }
        return data;
    }
}
