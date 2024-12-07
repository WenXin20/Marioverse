package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.client_bound.data.RenamedBlockPayload;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
    private boolean playedAppearAnim;
    private boolean playedDisappearAnim;
    private boolean playedSwitchAnim;
    public boolean renderWonderFlag;

    public GoalPoleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.GOAL_POLE_BLOCK_ENTITY.get(), pos, state);
        this.renderWonderFlag = Boolean.FALSE;
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
        Block block = this.getBlockState().getBlock();

        if (state.getValue(GoalPoleBlock.LOWERED)) {
            if ((this.isAmericanFlag(this) || block == BlockRegistry.CLASSIC_GOAL_POLE.get()) && !this.playedDisappearAnim()
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
            } else if (!this.playedSwitchAnim() && !this.isAmericanFlag(this) && block != BlockRegistry.CLASSIC_GOAL_POLE.get()) {
                this.setPlayedSwitchAnim(Boolean.TRUE);
                if (this.getLevel() != null)
                    this.updateConnectedSwitchFlags(this.getLevel(), this.getBlockPos());
                event.setAndContinue(SWITCH_ANIM);
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("playedAppearAnim", this.playedAppearAnim);
        tag.putBoolean("playedDisappearAnim", this.playedDisappearAnim);
        tag.putBoolean("playedSwitchAnim", this.playedSwitchAnim);
        tag.putBoolean("renderWonderFlag", this.renderWonderFlag);

        if (this.name != null) {
            // System.out.println("Saved CustomName: " + this.name);
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.playedAppearAnim = tag.getBoolean("playedAppearAnim");
        this.playedDisappearAnim = tag.getBoolean("playedDisappearAnim");
        this.playedSwitchAnim = tag.getBoolean("playedSwitchAnim");
        this.renderWonderFlag = tag.getBoolean("renderWonderFlag");

//        System.out.println("Render Wonder Flag: " + this.hasWonderFlag());
        if (tag.contains(CUSTOM_NAME, 8)) {
            this.name = parseCustomNameSafe(tag.getString(CUSTOM_NAME), provider);
            // System.out.println("Loaded CustomName: " + this.name);
            this.markUpdated();
        }
        
        PacketHandler.sendToAllClients(new RenamedBlockPayload(this.getBlockPos()));
        if (this.level != null)
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }

    public void markUpdated() {

        this.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setCustomName(Component name) {
        this.name = name;
        this.markUpdated();
        this.getUpdatePacket();
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
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.name = input.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CUSTOM_NAME, this.name);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("CustomName");
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

    public boolean isAmericanFlag(GoalPoleBlockEntity blockEntity) {
        return blockEntity.getName().getString().toLowerCase(Locale.ROOT).equals("america")
                || blockEntity.getName().getString().toLowerCase(Locale.ROOT).equals("america flag")
                || blockEntity.getName().getString().toLowerCase(Locale.ROOT).equals("usa")
                || blockEntity.getName().getString().toLowerCase(Locale.ROOT).equals("usa flag")
                || blockEntity.getName().getString().toLowerCase(Locale.ROOT).equals("united states of america")
                || blockEntity.getName().getString().toLowerCase(Locale.ROOT).equals("united states")
                || blockEntity.getName().getString().toLowerCase(Locale.ROOT).equals("united states flag");
    }

    public boolean isWonderFlag() {
        return this.getName().getString().toLowerCase(Locale.ROOT).equals("wonder")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("wonder flag")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("flower")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("flower flag");
    }

    public void setWonderFlag(boolean hasWonderFlag) {
        this.renderWonderFlag = hasWonderFlag;
        this.markUpdated();
        this.getUpdatePacket();
    }

    public boolean hasWonderFlag() {
        return this.renderWonderFlag;
    }
}
