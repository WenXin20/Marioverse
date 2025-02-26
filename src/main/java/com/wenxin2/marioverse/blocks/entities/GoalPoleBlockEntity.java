package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.client_bound.data.AmericaGoalPolePayload;
import com.wenxin2.marioverse.network.client_bound.data.WonderGoalPolePayload;
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
    protected static final RawAnimation APPEAR = RawAnimation.begin().thenPlayAndHold("animation.goal_pole.appear");
    protected static final RawAnimation DISAPPEAR = RawAnimation.begin().thenPlayAndHold("animation.goal_pole.disappear");
    public static final RawAnimation SWITCH = RawAnimation.begin().thenPlayAndHold("animation.goal_pole.switch");
    protected static final RawAnimation WINDY_CALM = RawAnimation.begin().thenLoop("animation.goal_pole.windy_calm");
    protected static final RawAnimation WINDY_RAIN = RawAnimation.begin().thenLoop("animation.goal_pole.windy_rain");
    protected static final RawAnimation WINDY_THUNDER = RawAnimation.begin().thenLoop("animation.goal_pole.windy_thunder");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.goal_pole");
    public static final String CUSTOM_NAME = "CustomName";
    @Nullable
    public Component name;
    private boolean playedAppearAnim;
    private boolean playedDisappearAnim;
    private boolean playedSwitchAnim;
    private boolean renderWonderFlag;
    private boolean renderAmericanFlag;
    private boolean animationFinished = false;

    public GoalPoleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.GOAL_POLE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "appear_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("appear", APPEAR));
        controllers.add(new AnimationController<>(this, "disappear_controller", 5, this::disappearController)
                .triggerableAnim("disappear", DISAPPEAR));
        controllers.add(new AnimationController<>(this, "switch_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("switch", SWITCH));
        controllers.add(new AnimationController<>(this, "windy_controller", 20, this::windyController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    protected <E extends GeoAnimatable> PlayState windyController(final AnimationState<E> event) {

        if (this.getLevel() != null && this.getLevel().canSeeSky(this.getBlockPos())) {
            Level world = this.getLevel();

            if (world.isThundering())
                event.setAndContinue(WINDY_THUNDER);
            else if (world.isRaining())
                event.setAndContinue(WINDY_RAIN);
            else event.setAndContinue(WINDY_CALM);

        } else event.setAndContinue(WINDY_CALM);

        return PlayState.CONTINUE;
    }

    protected <E extends GeoAnimatable> PlayState disappearController(final AnimationState<E> event) {
        BlockState state = this.getBlockState();
        Block block = this.getBlockState().getBlock();

        if (state.getValue(GoalPoleBlock.LOWERED)) {
            if ((this.isAmericanFlag() || block == BlockRegistry.CLASSIC_GOAL_POLE.get()) && !this.playedDisappearAnim()
                    && state.getValue(GoalPoleBlock.COLUMN) != ColumnBlockStates.MIDDLE) {
                this.setPlayedDisappearAnim(Boolean.TRUE);
                if (this.getLevel() != null)
                    this.updateConnectedDisappearFlags(this.getLevel(), this.getBlockPos());
                event.setAndContinue(DISAPPEAR);
            }
        }
        return PlayState.CONTINUE;
    }

    public boolean isAnimationFinished() {
        return animationFinished;
    }

    public void setAnimationFinished(boolean isAnimationDone) {
        this.animationFinished = isAnimationDone;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("playedAppearAnim", this.playedAppearAnim);
        tag.putBoolean("playedDisappearAnim", this.playedDisappearAnim);
        tag.putBoolean("playedSwitchAnim", this.playedSwitchAnim);
        tag.putBoolean("renderWonderFlag", this.renderWonderFlag);
        tag.putBoolean("renderAmericanFlag", this.renderAmericanFlag);

        if (this.name != null) {
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
            if (this.isWonderFlag()) {
//                this.setWonderFlag(true);
                PacketHandler.sendToAllClients(new WonderGoalPolePayload(this.getBlockPos(), this.hasWonderFlag()));
            } else if (this.isAmericanFlag()) {
//                this.setAmericanFlag(true);
                PacketHandler.sendToAllClients(new AmericaGoalPolePayload(this.getBlockPos(), this.hasAmericanFlag()));
            }
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.playedAppearAnim = tag.getBoolean("playedAppearAnim");
        this.playedDisappearAnim = tag.getBoolean("playedDisappearAnim");
        this.playedSwitchAnim = tag.getBoolean("playedSwitchAnim");
        this.renderWonderFlag = tag.getBoolean("renderWonderFlag");
        this.renderAmericanFlag = tag.getBoolean("renderAmericanFlag");

        if (tag.contains(CUSTOM_NAME, 8)) {
            this.name = parseCustomNameSafe(tag.getString(CUSTOM_NAME), provider);
            this.markUpdated();
            this.markUpdatedClients();
            PacketHandler.sendToAllClients(new AmericaGoalPolePayload(this.getBlockPos(), this.hasAmericanFlag()));
            PacketHandler.sendToAllClients(new WonderGoalPolePayload(this.getBlockPos(), this.hasWonderFlag()));
//            if (this.isWonderFlag()) {
//                this.setWonderFlag(true);
//            } else if (this.isAmericanFlag()) {
//                this.setAmericanFlag(true);
//            }
        }
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }

    public void markUpdatedClients() {
        this.setChanged();
        if (this.level != null && this.level.isClientSide)
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
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

    @NotNull
    @Override
    public  Component getName() {
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

    public void updateConnectedAppearFlags(Level world, BlockPos pos) {
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

    public void updateConnectedDisappearFlags(Level world, BlockPos pos) {
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

    public void updateConnectedSwitchFlags(Level world, BlockPos pos) {
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
        return this.getName().getString().toLowerCase(Locale.ROOT).equals("america")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("america flag")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("usa")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("usa flag")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("united states of america")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("united states")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("united states flag");
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
        if (this.level != null)
            this.getUpdatePacket();
    }

    public boolean hasWonderFlag() {
        return this.renderWonderFlag;
    }

    public void setAmericanFlag(boolean hasAmericanFlag) {
        this.renderAmericanFlag = hasAmericanFlag;
        this.markUpdated();
        if (this.level != null)
            this.getUpdatePacket();
    }

    public boolean hasAmericanFlag() {
        return this.renderAmericanFlag;
    }
}
