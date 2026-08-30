package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ArrowSignBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ArrowDirection arrowDirection = ArrowDirection.UP;
    private boolean waxed = false;
    @Nullable
    private DyeColor arrowDyeColor = DyeColor.RED;

    public ArrowSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("waxed", this.waxed);
        tag.putString("arrow_direction", this.arrowDirection.getSerializedName());
        if (this.arrowDyeColor != null)
            tag.putString("arrow_dye_color", this.arrowDyeColor.getSerializedName());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.waxed = tag.getBoolean("waxed");

        if (tag.contains("arrow_direction")) {
            for (ArrowDirection direction : ArrowDirection.values()) {
                if (direction.getSerializedName().equals(tag.getString("arrow_direction"))) {
                    this.arrowDirection = direction;
                    break;
                }
            }
        }

        this.arrowDyeColor = tag.contains("arrow_dye_color") ? DyeColor.byName(tag.getString("arrow_dye_color"), null) : null;
    }

    @Override
    public void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.waxed = input.getOrDefault(DataComponentRegistry.WAXED.get(), false);
        this.arrowDirection = input.getOrDefault(DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
        this.arrowDyeColor = input.getOrDefault(DataComponentRegistry.ARROW_SIGN_DYE_COLOR.get(), DyeColor.RED);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponentRegistry.WAXED.get(), this.waxed);
        builder.set(DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), this.arrowDirection);
        if (this.arrowDyeColor != null)
            builder.set(DataComponentRegistry.ARROW_SIGN_DYE_COLOR.get(), this.arrowDyeColor);
    }

    public boolean isWaxed() {
        return this.waxed;
    }

    public void setWaxed(boolean waxed) {
        this.waxed = waxed;
        this.syncToClient();
    }

    public ArrowDirection getArrowDirection() {
        return this.arrowDirection;
    }

    public void setArrowDirection(ArrowDirection arrowDirection) {
        this.arrowDirection = arrowDirection;
        this.syncToClient();
    }

    @Nullable
    public DyeColor getArrowDyeColor() {
        return this.arrowDyeColor;
    }

    public void setArrowDyeColor(@Nullable DyeColor arrowDyeColor) {
        this.arrowDyeColor = arrowDyeColor;
        this.syncToClient();
    }

    // setChanged() alone only marks the chunk dirty for saving; this pushes the update to clients.
    private void syncToClient() {
        this.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
    }
}