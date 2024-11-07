package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GoalPoleBlockEntity extends BlockEntity implements GeoBlockEntity, Nameable {
    protected static final RawAnimation APPEAR_ANIM = RawAnimation.begin().thenPlayAndHold("animation.goal_pole.appear");
    protected static final RawAnimation DISAPPEAR_ANIM = RawAnimation.begin().thenPlayAndHold("animation.goal_pole.disappear");
    protected static final RawAnimation SWITCH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.goal_pole.switch");
    protected static final RawAnimation WAVE_ANIM = RawAnimation.begin().thenLoop("animation.goal_pole.wave");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.goal_pole");
    public static final String CUSTOM_NAME = "CustomName";
    @Nullable
    public Component name;

    public GoalPoleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.GOAL_POLE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "switch", 5, this::switchAnimController));
        controllers.add(new AnimationController<>(this, "wave", 5, this::waveAnimController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    protected <E extends GeoAnimatable> PlayState waveAnimController(final AnimationState<E> event) {
        event.setAndContinue(WAVE_ANIM);
        return PlayState.CONTINUE;
    }

    protected <E extends GeoAnimatable> PlayState switchAnimController(final AnimationState<E> event) {
        BlockState state = this.getBlockState();

        if (state.getValue(GoalPoleBlock.LOWERED)) {
            if (this.isAmericanFlag(this) && state.getValue(GoalPoleBlock.COLUMN) != ColumnBlockStates.MIDDLE)
                event.setAndContinue(DISAPPEAR_ANIM);
            else if (state.getValue(GoalPoleBlock.COLUMN) == ColumnBlockStates.MIDDLE)
                event.setAndContinue(APPEAR_ANIM);
            else event.setAndContinue(SWITCH_ANIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        if (this.name != null) {
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        if (tag.contains(CUSTOM_NAME, 8)) {
            this.name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME), provider);
        }
    }

    public void setCustomName(Component name) {
        this.name = name;
        this.setChanged();
        this.getUpdatePacket();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.name;
    }

    public Component getCustomName(Component name) {
        return this.name = name;
    }

    @Override
    public @NotNull Component getName() {
        return this.name != null ? this.name : DEFAULT_NAME;
    }

    public boolean isAmericanFlag(GoalPoleBlockEntity blockEntity) {
        return blockEntity.getCustomName() != null && (blockEntity.getCustomName().getString().equals("America")
                || blockEntity.getCustomName().getString().equals("America Flag")
                || blockEntity.getCustomName().getString().equals("america")
                || blockEntity.getCustomName().getString().equals("america flag")
                || blockEntity.getCustomName().getString().equals("USA")
                || blockEntity.getCustomName().getString().equals("USA Flag")
                || blockEntity.getCustomName().getString().equals("usa")
                || blockEntity.getCustomName().getString().equals("usa flag")
                || blockEntity.getCustomName().getString().equals("United States Of America")
                || blockEntity.getCustomName().getString().equals("United States of America")
                || blockEntity.getCustomName().getString().equals("united states of america")
                || blockEntity.getCustomName().getString().equals("United States")
                || blockEntity.getCustomName().getString().equals("United States Flag")
                || blockEntity.getCustomName().getString().equals("united states")
                || blockEntity.getCustomName().getString().equals("united states flag"));
    }
}
