package com.wenxin2.marioverse.integration.fast_paintings_compat;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;

public class PaintingBlockWarp implements WarpLinkableEntity {
    private final Level level;
    private final BlockEntity blockEntity;
    private final BlockPos basePos;
    private final Direction direction;
    private final int width;
    private final int height;
    private final Holder<PaintingVariant> variant;

    public PaintingBlockWarp(Level level, BlockEntity blockEntity, BlockPos basePos, Direction direction, Holder<PaintingVariant> variant) {
        this.level = level;
        this.blockEntity = blockEntity;
        this.basePos = basePos;
        this.direction = direction;
        this.width = variant.value().width();
        this.height = variant.value().height();
        this.variant = variant;
    }

    @Override
    public boolean mv$hasDestinationPos() {
        return this.getWarpData().destinationPos() != null;
    }

    @Override
    public void mv$setDestinationPos(@Nullable BlockPos pos) {
        this.setWarpData(this.getWarpData().withDestinationPos(pos));
    }

    @Override
    public BlockPos mv$getDestinationPos() {
        return this.getWarpData().destinationPos();
    }

    @Override
    public ResourceKey<Level> mv$getDestinationDim() {
        String dimension = this.getWarpData().dimension();
        if (dimension == null)
            return null;

        ResourceLocation location = ResourceLocation.tryParse(dimension);
        return location != null ? ResourceKey.create(Registries.DIMENSION, location) : null;
    }

    @Override
    public void mv$setDestinationDim(@Nullable ResourceKey<Level> dimension) {
        if (dimension != null)
            this.setWarpData(this.getWarpData().withDimension(dimension.location().toString()));
    }

    @Override
    public UUID mv$getWarpUUID() {
        return this.getWarpData().warpUuid();
    }

    @Override
    public void mv$setWarpUuid(UUID uuid) {
        this.setWarpData(this.getWarpData().withWarpUuid(uuid));
    }

    @Override
    public Entity mv$getWarpEntity() {
        return null;
    }

    @Override
    public void mv$setWarpEntity(Entity entity) {
    }

    @Override
    public @Nullable WarpTarget mv$getWarpTarget() {
        return this.getWarpData().warpTarget();
    }

    @Override
    public void mv$setWarpTarget(@Nullable WarpTarget target) {
        this.setWarpData(this.getWarpData().withWarpTarget(target));
    }

    @Override
    public @Nullable UUID mv$getWarpSubLevel() {
        return this.getWarpData().subLevel();
    }

    @Override
    public @Nullable BlockPos mv$getWarpSubLevelPos() {
        return this.getWarpData().subLevelPos();
    }

    @Override
    public void mv$setWarpSubLevel(@Nullable UUID subLevel, @Nullable BlockPos localPos) {
        this.setWarpData(this.getWarpData().withSubLevel(subLevel, localPos));
    }

    public Level getLevel() {
        return this.level;
    }

    public BlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public BlockPos getMasterPos() {
        return this.blockEntity.getBlockPos();
    }

    public BlockPos getBasePos() {
        return this.basePos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Holder<PaintingVariant> getVariant() {
        return this.variant;
    }

    public WarpTarget getTarget() {
        return new WarpTarget(this.basePos, this.direction, this.width);
    }

    @Nullable
    public UUID getUUID() {
        return this.getWarpData().uuid();
    }

    public UUID getOrCreateUUID() {
        UUID uuid = this.getUUID();

        if (uuid == null) {
            uuid = UUID.randomUUID();
            this.setWarpData(this.getWarpData().withUuid(uuid));
        }
        return uuid;
    }

    public void setUUID(@Nullable UUID uuid) {
        this.setWarpData(this.getWarpData().withUuid(uuid));
    }

    public boolean isPreventWarp() {
        return this.readData(DataAttachmentRegistry.PREVENT_WARP, false);
    }

    public boolean isBreak() {
        return this.readData(DataAttachmentRegistry.BREAK, false);
    }

    public boolean isWaxed() {
        return this.readData(DataAttachmentRegistry.IS_WAXED, false);
    }

    public int getFuelCount() {
        return this.readData(DataAttachmentRegistry.WARP_FUEL_COUNT, 0);
    }

    public void setPreventWarp(boolean preventWarp) {
        this.blockEntity.setData(DataAttachmentRegistry.PREVENT_WARP, preventWarp);
        this.blockEntity.setChanged();
    }

    public void setBreak(boolean breakPainting) {
        this.blockEntity.setData(DataAttachmentRegistry.BREAK, breakPainting);
        this.blockEntity.setChanged();
    }

    public void setWaxed(boolean isWaxed) {
        this.blockEntity.setData(DataAttachmentRegistry.IS_WAXED, isWaxed);
        this.blockEntity.setChanged();
    }

    public void setFuelCount(int fuelCount) {
        this.blockEntity.setData(DataAttachmentRegistry.WARP_FUEL_COUNT, fuelCount);
        this.blockEntity.setChanged();
    }

    public Component getName() {
        return Component.translatable(EntityType.PAINTING.getDescriptionId());
    }

    @Nullable
    public Component getTitle() {
        return this.variant.unwrapKey().map(key -> (Component) Component.translatable(key.location().toLanguageKey("painting", "title")))
                .orElse(null);
    }

    public Vec3 getCenter() {
        Direction alongPainting = this.direction.getCounterClockWise();
        double alongOffset = this.width % 2 == 0 ? 0.5 : 0.0;
        double verticalOffset = this.height % 2 == 0 ? 0.5 : 0.0;

        return Vec3.atCenterOf(this.basePos)
                .add(this.direction.getStepX() * -0.46875, verticalOffset, this.direction.getStepZ() * -0.46875)
                .add(alongPainting.getStepX() * alongOffset, 0.0, alongPainting.getStepZ() * alongOffset);
    }

    public PaintingWarpData getWarpData() {
        return this.readData(DataAttachmentRegistry.PAINTING_WARP, PaintingWarpData.EMPTY);
    }

    public void setWarpData(PaintingWarpData data) {
        this.blockEntity.setData(DataAttachmentRegistry.PAINTING_WARP, data);
        this.blockEntity.setChanged();
    }

    private <T> T readData(Supplier<AttachmentType<T>> type, T fallback) {
        return this.blockEntity.hasData(type) ? this.blockEntity.getData(type) : fallback;
    }
}
