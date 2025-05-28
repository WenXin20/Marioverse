package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Painting.class)
public abstract class PaintingMixin extends HangingEntity implements WarpLinkableEntity {
    @Unique private static final String BREAK_PAINTING = "BreakPainting";
    @Unique private static final String IS_WAXED = "IsWaxed";
    @Unique private static final String PREVENT_WARP = "PreventWarp";
    @Unique private static final String UUID = "UUID";
    @Unique private static final String WARP_DIMENSION = "Dimension";
    @Unique private static final String WARP_POS = "WarpPos";
    @Unique private static final String WARP_UUID = "WarpUUID";
    @Unique public BlockPos mv$destinationPos;
    @Unique public Entity mv$warpEntity;
    @Unique public String mv$dimensionTag;
    @Unique public UUID mv$UUID;
    @Unique public UUID mv$warpUUID;
    @Unique public boolean mv$breakPainting = Boolean.FALSE;
    @Unique public boolean mv$isWaxed;
    @Unique public boolean mv$preventWarp = Boolean.FALSE;

    public PaintingMixin(EntityType<? extends HangingEntity> type, Level world) {
        super(type, world);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        WARP_ENTITY_LOCATIONS.put(this.blockPosition(), this.mv$getWarpEntity());
        tag.putBoolean(BREAK_PAINTING, this.mv$breakPainting);
        tag.putBoolean(IS_WAXED, this.mv$isWaxed);
        tag.putBoolean(PREVENT_WARP, this.mv$preventWarp);

        if (this.mv$hasDestinationPos() && this.mv$destinationPos != null)
            tag.put(WARP_POS, NbtUtils.writeBlockPos(this.mv$destinationPos));

        if (this.mv$dimensionTag != null)
            tag.putString(WARP_DIMENSION, this.mv$dimensionTag);

        if (this.mv$UUID != null)
            tag.putUUID(UUID, this.getUUID());

        if (this.mv$warpUUID != null)
            tag.putUUID(WARP_UUID, this.mv$getWarpUUID());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        WARP_ENTITY_LOCATIONS.put(this.blockPosition(), this.mv$getWarpEntity());
        this.mv$isWaxed = tag.getBoolean(IS_WAXED);

        if (tag.contains(WARP_POS)) {
            this.mv$destinationPos = NbtUtils.readBlockPos(tag, WARP_POS).orElse(null);
            this.mv$setDestinationPos(this.mv$destinationPos);
        }

        if (tag.contains(BREAK_PAINTING))
            this.mv$breakPainting = tag.getBoolean(BREAK_PAINTING);

        if (tag.contains(PREVENT_WARP))
            this.mv$preventWarp = tag.getBoolean(PREVENT_WARP);

        if (tag.contains(UUID))
            this.mv$UUID = tag.getUUID(UUID);

        if (tag.contains(WARP_DIMENSION))
            this.mv$dimensionTag = tag.getString(WARP_DIMENSION);

        if (tag.contains(WARP_UUID))
            this.mv$warpUUID = tag.getUUID(WARP_UUID);
    }

    @Override
    public boolean mv$isWaxed() {
        return this.mv$isWaxed;
    }

    @Override
    public void mv$setWaxed(boolean isWaxed) {
        if (this.mv$isWaxed != isWaxed) {
            this.mv$isWaxed = isWaxed;
        }
    }

    @Override
    public boolean mv$hasDestinationPos() {
        return this.mv$destinationPos != null;
    }

    @Override
    public void mv$setDestinationPos(@Nullable BlockPos pos) {
        this.mv$destinationPos = pos;
    }

    @Override
    public BlockPos mv$getDestinationPos() {
        if (this.mv$destinationPos != null) {
            return this.mv$destinationPos;
        }
        return null;
    }

    @Override
    public ResourceKey<Level> mv$getDestinationDim() {
        if (this.mv$dimensionTag != null) {
            ResourceLocation location = ResourceLocation.tryParse(this.mv$dimensionTag);
            if (location != null) {
                return ResourceKey.create(Registries.DIMENSION, location);
            }
        }
        return null;
    }

    @Override
    public void mv$setDestinationDim(@Nullable ResourceKey<Level> dimension) {
        if (dimension != null)
            this.mv$dimensionTag = dimension.location().toString();
    }

    @Override
    public boolean mv$isBreakPainting() {
        return this.mv$breakPainting;
    }

    @Override
    public void mv$setBreakPainting(boolean breakPainting) {
        this.mv$breakPainting = breakPainting;
    }

    @Override
    public boolean mv$getPreventWarp() {
        return this.mv$preventWarp;
    }

    @Override
    public void mv$setPreventWarp(boolean preventWarp) {
        this.mv$preventWarp = preventWarp;
    }

    @Override
    public UUID mv$getWarpUUID() {
        return this.mv$warpUUID;
    }

    @Override
    public void mv$setWarpUuid(UUID uuid) {
        this.mv$warpUUID = uuid;
    }

    @Override
    public Entity mv$getWarpEntity() {
        return this.mv$warpEntity;
    }

    @Override
    public void mv$setWarpEntity(Entity entity) {
        this.mv$warpEntity = entity;
    }
}
