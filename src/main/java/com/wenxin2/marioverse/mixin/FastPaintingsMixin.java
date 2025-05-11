package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpLinkableBlock;
import java.util.UUID;
import net.mehvahdjukaar.moonlight.api.client.model.IExtraModelDataProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.mehvahdjukaar.fastpaintings.PaintingBlockEntity")
public class FastPaintingsMixin extends BlockEntity implements WarpLinkableBlock {
    private BlockPos destinationPos;
    private String dimensionTag;
    private boolean isWaxed;
    private boolean preventWarp;
    private UUID uuid;
    private UUID warpUuid;

    public FastPaintingsMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void loadAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        this.isWaxed = tag.getBoolean(IS_WAXED);

        if (tag.contains(WARP_POS)) {
            this.destinationPos = NbtUtils.readBlockPos(tag, WARP_POS).orElse(null);
            this.setDestinationPos(this.destinationPos);
        }

        if (tag.contains(WARP_DIMENSION))
            this.dimensionTag = tag.getString(WARP_DIMENSION);

        if (tag.contains(PREVENT_WARP))
            this.preventWarp = tag.getBoolean(PREVENT_WARP);

        if (tag.contains(WarpLinkableBlock.UUID))
            this.uuid = tag.getUUID(UUID);

        if (tag.contains(WARP_UUID))
            this.warpUuid = tag.getUUID(WARP_UUID);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        tag.putBoolean(PREVENT_WARP, this.preventWarp);
        tag.putBoolean(IS_WAXED, this.isWaxed);

        if (this.hasDestinationPos() && this.destinationPos != null)
            tag.put(WARP_POS, NbtUtils.writeBlockPos(this.destinationPos));

        if (this.dimensionTag != null)
            tag.putString(WARP_DIMENSION, this.dimensionTag);

        if (this.uuid != null)
            tag.putUUID(UUID, this.getUUID());

        if (this.warpUuid != null)
            tag.putUUID(WARP_UUID, this.getWarpUuid());
    }

    @Override
    public void onLoad() {
        WarpLinkableBlock.super.onWarpLoad(this.getBlockPos());
        super.onLoad();
    }

    @Override
    public BaseWarpBlockEntity self() {
        return (BaseWarpBlockEntity) (Object) this;
    }

    @Override
    public BlockPos getDestinationPos() {
        return destinationPos;
    }

    @Override
    public void setDestinationPos(@Nullable BlockPos pos) {
        destinationPos = pos;
    }

    @Override
    public boolean hasDestinationPos() {
        return destinationPos != null;
    }

    @Override public String getDimensionTag() {
        return dimensionTag;
    }

    @Override public void setDimensionTag(String tag) {
        dimensionTag = tag;
    }

    @Override
    public boolean isPreventWarp() {
        return preventWarp;
    }

    @Override
    public void setPreventWarp(boolean val) {
        preventWarp = val;
    }

    @Override
    public boolean isWaxed() {
        return isWaxed;
    }

    @Override
    public void setWaxed(boolean waxed) {
        isWaxed = waxed;
    }

    @Override
    public UUID getWarpUuid() {
        return warpUuid;
    }

    @Override
    public void setWarpUuid(UUID uuid) {
        warpUuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
}
