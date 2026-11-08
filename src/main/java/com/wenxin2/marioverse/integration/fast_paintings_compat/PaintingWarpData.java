package com.wenxin2.marioverse.integration.fast_paintings_compat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;

public record PaintingWarpData(@Nullable UUID uuid, @Nullable UUID warpUuid, @Nullable BlockPos destinationPos,
                               @Nullable String dimension, @Nullable WarpLinkableEntity.WarpTarget warpTarget,
                               @Nullable UUID subLevel, @Nullable BlockPos subLevelPos) {
    public static final PaintingWarpData EMPTY = new PaintingWarpData(null, null, null, null, null, null, null);

    private static final Codec<WarpLinkableEntity.WarpTarget> TARGET_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(WarpLinkableEntity.WarpTarget::pos),
            Direction.CODEC.fieldOf("direction").forGetter(WarpLinkableEntity.WarpTarget::direction),
            Codec.INT.fieldOf("width").forGetter(WarpLinkableEntity.WarpTarget::width)
    ).apply(instance, WarpLinkableEntity.WarpTarget::new));

    public static final Codec<PaintingWarpData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.optionalFieldOf("uuid").forGetter(data -> Optional.ofNullable(data.uuid())),
            UUIDUtil.CODEC.optionalFieldOf("warp_uuid").forGetter(data -> Optional.ofNullable(data.warpUuid())),
            BlockPos.CODEC.optionalFieldOf("destination_pos").forGetter(data -> Optional.ofNullable(data.destinationPos())),
            Codec.STRING.optionalFieldOf("dimension").forGetter(data -> Optional.ofNullable(data.dimension())),
            TARGET_CODEC.optionalFieldOf("warp_target").forGetter(data -> Optional.ofNullable(data.warpTarget())),
            UUIDUtil.CODEC.optionalFieldOf("sub_level").forGetter(data -> Optional.ofNullable(data.subLevel())),
            BlockPos.CODEC.optionalFieldOf("sub_level_pos").forGetter(data -> Optional.ofNullable(data.subLevelPos()))
    ).apply(instance, (uuid, warpUuid, destinationPos, dimension, warpTarget, subLevel, subLevelPos) -> new PaintingWarpData(
            uuid.orElse(null), warpUuid.orElse(null), destinationPos.orElse(null), dimension.orElse(null),
            warpTarget.orElse(null), subLevel.orElse(null), subLevelPos.orElse(null))));

    public PaintingWarpData withUuid(@Nullable UUID uuid) {
        return new PaintingWarpData(uuid, this.warpUuid, this.destinationPos, this.dimension, this.warpTarget, this.subLevel, this.subLevelPos);
    }

    public PaintingWarpData withWarpUuid(@Nullable UUID warpUuid) {
        return new PaintingWarpData(this.uuid, warpUuid, this.destinationPos, this.dimension, this.warpTarget, this.subLevel, this.subLevelPos);
    }

    public PaintingWarpData withDestinationPos(@Nullable BlockPos destinationPos) {
        return new PaintingWarpData(this.uuid, this.warpUuid, destinationPos, this.dimension, this.warpTarget, this.subLevel, this.subLevelPos);
    }

    public PaintingWarpData withDimension(@Nullable String dimension) {
        return new PaintingWarpData(this.uuid, this.warpUuid, this.destinationPos, dimension, this.warpTarget, this.subLevel, this.subLevelPos);
    }

    public PaintingWarpData withWarpTarget(@Nullable WarpLinkableEntity.WarpTarget warpTarget) {
        return new PaintingWarpData(this.uuid, this.warpUuid, this.destinationPos, this.dimension, warpTarget, this.subLevel, this.subLevelPos);
    }

    public PaintingWarpData withSubLevel(@Nullable UUID subLevel, @Nullable BlockPos subLevelPos) {
        return new PaintingWarpData(this.uuid, this.warpUuid, this.destinationPos, this.dimension, this.warpTarget, subLevel, subLevelPos);
    }
}
