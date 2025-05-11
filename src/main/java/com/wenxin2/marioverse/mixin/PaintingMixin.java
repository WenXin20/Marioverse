package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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
    @Unique public BlockPos marioverse$destinationPos;
    @Unique public Entity marioverse$warpEntity;
    @Unique public String marioverse$dimensionTag;
    @Unique public UUID marioverse$UUID;
    @Unique public UUID marioverse$warpUUID;
    @Unique public boolean marioverse$breakPainting = Boolean.FALSE;
    @Unique public boolean marioverse$isWaxed;
    @Unique public boolean marioverse$preventWarp = Boolean.FALSE;

    public PaintingMixin(EntityType<? extends HangingEntity> type, Level world) {
        super(type, world);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        WARP_ENTITY_LOCATIONS.put(this.blockPosition(), this.marioverse$getWarpEntity());
        tag.putBoolean(BREAK_PAINTING, this.marioverse$breakPainting);
        tag.putBoolean(IS_WAXED, this.marioverse$isWaxed);
        tag.putBoolean(PREVENT_WARP, this.marioverse$preventWarp);

        if (this.marioverse$hasDestinationPos() && this.marioverse$destinationPos != null)
            tag.put(WARP_POS, NbtUtils.writeBlockPos(this.marioverse$destinationPos));

        if (this.marioverse$dimensionTag != null)
            tag.putString(WARP_DIMENSION, this.marioverse$dimensionTag);

        if (this.marioverse$UUID != null)
            tag.putUUID(UUID, this.getUUID());

        if (this.marioverse$warpUUID != null)
            tag.putUUID(WARP_UUID, this.marioverse$getWarpUUID());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        WARP_ENTITY_LOCATIONS.put(this.blockPosition(), this.marioverse$getWarpEntity());
        this.marioverse$isWaxed = tag.getBoolean(IS_WAXED);

        if (tag.contains(WARP_POS)) {
            this.marioverse$destinationPos = NbtUtils.readBlockPos(tag, WARP_POS).orElse(null);
            this.marioverse$setDestinationPos(this.marioverse$destinationPos);
        }

        if (tag.contains(BREAK_PAINTING))
            this.marioverse$breakPainting = tag.getBoolean(BREAK_PAINTING);

        if (tag.contains(PREVENT_WARP))
            this.marioverse$preventWarp = tag.getBoolean(PREVENT_WARP);

        if (tag.contains(UUID))
            this.marioverse$UUID = tag.getUUID(UUID);

        if (tag.contains(WARP_DIMENSION))
            this.marioverse$dimensionTag = tag.getString(WARP_DIMENSION);

        if (tag.contains(WARP_UUID))
            this.marioverse$warpUUID = tag.getUUID(WARP_UUID);
    }

    @Override
    public boolean marioverse$isWaxed() {
        return this.marioverse$isWaxed;
    }

    @Override
    public void marioverse$setWaxed(boolean isWaxed) {
        if (this.marioverse$isWaxed != isWaxed) {
            this.marioverse$isWaxed = isWaxed;
        }
    }

    @Override
    public boolean marioverse$hasDestinationPos() {
        return this.marioverse$destinationPos != null;
    }

    @Override
    public void marioverse$setDestinationPos(@Nullable BlockPos pos) {
        this.marioverse$destinationPos = pos;
    }

    @Override
    public BlockPos marioverse$getDestinationPos() {
        if (this.marioverse$destinationPos != null) {
            return this.marioverse$destinationPos;
        }
        return null;
    }

    @Override
    public ResourceKey<Level> marioverse$getDestinationDim() {
        if (this.marioverse$dimensionTag != null) {
            ResourceLocation location = ResourceLocation.tryParse(this.marioverse$dimensionTag);
            if (location != null) {
                return ResourceKey.create(Registries.DIMENSION, location);
            }
        }
        return null;
    }

    @Override
    public void marioverse$setDestinationDim(@Nullable ResourceKey<Level> dimension) {
        if (dimension != null)
            this.marioverse$dimensionTag = dimension.location().toString();
    }

    @Override
    public boolean marioverse$isBreakPainting() {
        return this.marioverse$breakPainting;
    }

    @Override
    public void marioverse$setBreakPainting(boolean breakPainting) {
        this.marioverse$breakPainting = breakPainting;
    }

    @Override
    public boolean marioverse$getPreventWarp() {
        return this.marioverse$preventWarp;
    }

    @Override
    public void marioverse$setPreventWarp(boolean preventWarp) {
        this.marioverse$preventWarp = preventWarp;
    }

    @Override
    public UUID marioverse$getWarpUUID() {
        return this.marioverse$warpUUID;
    }

    @Override
    public void marioverse$setWarpUuid(UUID uuid) {
        this.marioverse$warpUUID = uuid;
    }

    @Override
    public Entity marioverse$getWarpEntity() {
        return this.marioverse$warpEntity;
    }

    @Override
    public void marioverse$setWarpEntity(Entity entity) {
        this.marioverse$warpEntity = entity;
    }
}
