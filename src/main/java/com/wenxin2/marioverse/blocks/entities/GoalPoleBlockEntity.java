package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
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
    public boolean playedAppearAnim;
    public boolean playedDisappearAnim;
    public boolean playedSwitchAnim;

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
            if (this.isAmericanFlag() && !this.playedDisappearAnim()
                    && state.getValue(GoalPoleBlock.COLUMN) != ColumnBlockStates.MIDDLE) {
                this.setPlayedDisappearAnim(Boolean.TRUE);
                if (this.getLevel() != null)
                    this.updateConnectedDisappearFlags(this.getLevel(), this.getBlockPos());
                event.setAndContinue(DISAPPEAR_ANIM);
            } else if (!this.playedAppearAnim()
                    && state.getValue(GoalPoleBlock.COLUMN) == ColumnBlockStates.MIDDLE) {
                this.setPlayedAppearAnim(Boolean.TRUE);
                if (this.getLevel() != null)
                    this.updateConnectedAppearFlags(this.getLevel(), this.getBlockPos());
                event.setAndContinue(APPEAR_ANIM);
            } else if (!this.playedSwitchAnim()) {
                this.setPlayedSwitchAnim(Boolean.TRUE);
                if (this.getLevel() != null)
                    this.updateConnectedSwitchFlags(this.getLevel(), this.getBlockPos());
                event.setAndContinue(SWITCH_ANIM);
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("PlayedAppearAnim", this.playedAppearAnim);
        tag.putBoolean("PlayedDisappearAnim", this.playedDisappearAnim);
        tag.putBoolean("PlayedSwitchAnim", this.playedSwitchAnim);

        if (this.name != null)
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.playedAppearAnim = tag.getBoolean("PlayedAppearAnim");
        this.playedDisappearAnim = tag.getBoolean("PlayedDisappearAnim");
        this.playedSwitchAnim = tag.getBoolean("PlayedSwitchAnim");

        if (tag.contains(CUSTOM_NAME, 8)) {
            this.name = parseCustomNameSafe(tag.getString(CUSTOM_NAME), provider);
            this.markUpdated();
        }
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void setCustomName(Component name) {
        this.name = name;
        this.markUpdated();
    }

    @NotNull
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

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean playedAppearAnim() {
        return this.playedAppearAnim;
    }

    public boolean playedDisappearAnim() {
        return this.playedDisappearAnim;
    }

    public boolean playedSwitchAnim() {
        return this.playedSwitchAnim;
    }

    public void setPlayedAppearAnim(boolean playedAppearAnim) {
        this.playedAppearAnim = playedAppearAnim;
        this.markUpdated();
    }

    public void setPlayedDisappearAnim(boolean playedDisappearAnim) {
        this.playedDisappearAnim = playedDisappearAnim;
        this.markUpdated();
    }

    public void setPlayedSwitchAnim(boolean playedSwitchAnim) {
        this.playedSwitchAnim = playedSwitchAnim;
        this.markUpdated();
    }

    private void updateConnectedAppearFlags(Level world, BlockPos pos) {
        BlockPos posAbove = pos.above();
        while (world.getBlockState(posAbove).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posAbove).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedAppearAnim(Boolean.TRUE);
            this.markUpdated();
            posAbove = posAbove.above();
        }

        BlockPos posBelow = pos.below();
        while (world.getBlockState(posBelow).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posBelow).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedAppearAnim(Boolean.TRUE);
            this.markUpdated();
            posBelow = posBelow.below();
        }
    }

    private void updateConnectedDisappearFlags(Level world, BlockPos pos) {
        BlockPos posAbove = pos.above();
        while (world.getBlockState(posAbove).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posAbove).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedDisappearAnim(Boolean.TRUE);
            this.markUpdated();
            posAbove = posAbove.above();
        }

        BlockPos posBelow = pos.below();
        while (world.getBlockState(posBelow).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posBelow).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedDisappearAnim(Boolean.TRUE);
            this.markUpdated();
            posBelow = posBelow.below();
        }
    }

    private void updateConnectedSwitchFlags(Level world, BlockPos pos) {
        BlockPos posAbove = pos.above();
        while (world.getBlockState(posAbove).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posAbove).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedSwitchAnim(Boolean.TRUE);
            this.markUpdated();
            posAbove = posAbove.above();
        }

        BlockPos posBelow = pos.below();
        while (world.getBlockState(posBelow).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posBelow).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedSwitchAnim(Boolean.TRUE);
            this.markUpdated();
            posBelow = posBelow.below();
        }
    }

    public boolean isAmericanFlag() {
        return this.getName().getString().equals("America")
                || this.getName().getString().equals("America Flag")
                || this.getName().getString().equals("america")
                || this.getName().getString().equals("america flag")
                || this.getName().getString().equals("USA")
                || this.getName().getString().equals("USA Flag")
                || this.getName().getString().equals("usa")
                || this.getName().getString().equals("usa flag")
                || this.getName().getString().equals("United States Of America")
                || this.getName().getString().equals("United States of America")
                || this.getName().getString().equals("united states of america")
                || this.getName().getString().equals("United States")
                || this.getName().getString().equals("United States Flag")
                || this.getName().getString().equals("united states")
                || this.getName().getString().equals("united states flag");
    }
}
