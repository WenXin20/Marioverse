package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ArrowSignBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ArrowDirection arrowDirection = ArrowDirection.UP;
    private boolean waxed = false;

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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("waxed", this.waxed);
        tag.putString("arrow_direction", this.arrowDirection.getSerializedName());
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
    }

    @Override
    public void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.waxed = input.getOrDefault(DataComponentRegistry.WAXED.get(), false);
        this.arrowDirection = input.getOrDefault(DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponentRegistry.WAXED.get(), this.waxed);
        builder.set(DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), this.arrowDirection);
    }

    public boolean isWaxed() {
        return this.waxed;
    }

    public void setWaxed(boolean waxed) {
        this.waxed = waxed;
        this.setChanged();
    }

    public ArrowDirection getArrowDirection() {
        return this.arrowDirection;
    }

    public void setArrowDirection(ArrowDirection arrowDirection) {
        this.arrowDirection = arrowDirection;
        this.setChanged();
    }
}