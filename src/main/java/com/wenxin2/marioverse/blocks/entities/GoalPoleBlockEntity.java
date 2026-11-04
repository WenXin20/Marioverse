package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.network.client_bound.data.AmericaNamePayload;
import com.wenxin2.marioverse.network.client_bound.data.WonderNamePayload;
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
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
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
    public static final int FLAG_DISAPPEAR_TICKS = 10;
    public static final int FLAG_APPEAR_TICKS = 10;
    public static final float FLAG_RISE_TICKS_PER_BLOCK = 4.0F;
    private static final float[] DISAPPEAR_TIMES = {0.0F, 0.25F, 0.6667F, 1.0F};
    private static final float[] DISAPPEAR_SCALES = {1.0F, 1.1875F, 0.7F, 0.0F};
    private static final float[] APPEAR_TIMES = {0.0F, 0.3333F, 0.6667F, 0.9167F};
    private static final float[] APPEAR_SCALES = {0.0F, 0.7F, 1.1875F, 1.0F};

    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.goal_pole");
    public static final String CUSTOM_NAME = "CustomName";
    @Nullable
    public Component name;
    private boolean playedAppearAnim;
    private boolean playedDisappearAnim;
    private boolean renderWonderFlag;
    private boolean renderAmericanFlag;
    private long flagRaiseStart = Long.MIN_VALUE / 2;
    private float flagRaiseFrom;
    private float flagRaiseTo;

    public GoalPoleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.GOAL_POLE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "appear_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("appear", APPEAR));
        controllers.add(new AnimationController<>(this, "disappear_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("disappear", DISAPPEAR));
        controllers.add(new AnimationController<>(this, "switch_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("switch", SWITCH));
        controllers.add(new AnimationController<>(this, "windy_controller", 5, this::windyController));
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

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.writeSyncedData(tag);

        if (this.name != null) {
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
            if (this.isWonderFlag() && this.level != null && !this.level.isClientSide())
                PacketDistributor.sendToAllPlayers(new WonderNamePayload(this.getBlockPos(), this.hasWonderFlag()));
            else if (this.isAmericanFlag() && this.level != null && !this.level.isClientSide())
                PacketDistributor.sendToAllPlayers(new AmericaNamePayload(this.getBlockPos(), this.hasAmericanFlag()));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.playedAppearAnim = tag.getBoolean("playedAppearAnim");
        this.playedDisappearAnim = tag.getBoolean("playedDisappearAnim");
        this.renderWonderFlag = tag.getBoolean("renderWonderFlag");
        this.renderAmericanFlag = tag.getBoolean("renderAmericanFlag");
        if (tag.contains("flagRaiseStart"))
            this.flagRaiseStart = tag.getLong("flagRaiseStart");
        this.flagRaiseFrom = tag.getFloat("flagRaiseFrom");
        this.flagRaiseTo = tag.getFloat("flagRaiseTo");

        if (tag.contains(CUSTOM_NAME, 8)) {
            this.name = parseCustomNameSafe(tag.getString(CUSTOM_NAME), provider);
            
            if (this.level != null && !this.level.isClientSide()) {
                PacketDistributor.sendToAllPlayers(new AmericaNamePayload(this.getBlockPos(), this.hasAmericanFlag()));
                PacketDistributor.sendToAllPlayers(new WonderNamePayload(this.getBlockPos(), this.hasWonderFlag()));
            }
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

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        this.writeSyncedData(tag);
        return tag;
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

    @NotNull
    @Override
    public  Component getName() {
        return this.name != null ? this.name : DEFAULT_NAME;
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

    public void setPlayedAppearAnim(boolean playedAppearAnim) {
        this.playedAppearAnim = playedAppearAnim;
        this.markUpdated();
    }

    public void setPlayedDisappearAnim(boolean playedDisappearAnim) {
        this.playedDisappearAnim = playedDisappearAnim;
        this.markUpdated();
    }

    public void updateConnectedAppearFlags(Level world, BlockPos pos, boolean playedAnimation) {
        BlockPos posAbove = pos.above();
        while (world.getBlockState(posAbove).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posAbove).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedAppearAnim(playedAnimation);
            this.markUpdated();
            posAbove = posAbove.above();
        }

        BlockPos posBelow = pos.below();
        while (world.getBlockState(posBelow).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posBelow).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedAppearAnim(playedAnimation);
            this.markUpdated();
            posBelow = posBelow.below();
        }
    }

    public void updateConnectedDisappearFlags(Level world, BlockPos pos, boolean playedAnimation) {
        BlockPos posAbove = pos.above();
        while (world.getBlockState(posAbove).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posAbove).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedDisappearAnim(playedAnimation);
            this.markUpdated();
            posAbove = posAbove.above();
        }

        BlockPos posBelow = pos.below();
        while (world.getBlockState(posBelow).getBlock() instanceof GoalPoleBlock &&
                world.getBlockState(posBelow).getValue(GoalPoleBlock.LOWERED)) {
            this.setPlayedDisappearAnim(playedAnimation);
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
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("united states flag")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("250th")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("250th birthday");
    }

    public boolean isWonderFlag() {
        return this.getName().getString().toLowerCase(Locale.ROOT).equals("wonder")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("wonder flag")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("flower")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("flower flag");
    }

    public void setWonderFlag(boolean hasWonderFlag) {
        this.renderWonderFlag = hasWonderFlag;
    }

    public boolean hasWonderFlag() {
        return this.renderWonderFlag;
    }

    public void setAmericanFlag(boolean hasAmericanFlag) {
        this.renderAmericanFlag = hasAmericanFlag;
    }

    public boolean hasAmericanFlag() {
        return this.renderAmericanFlag;
    }

    public void startFlagRaise(long gameTime, float from, float to) {
        this.flagRaiseStart = gameTime;
        this.flagRaiseFrom = Math.max(from, 0.0F);
        this.flagRaiseTo = Mth.clamp(to, 0.0F, this.flagRaiseFrom);
        this.markUpdated();
    }

    public float getFlagRaiseFrom() {
        return this.flagRaiseFrom;
    }

    public float getFlagRaiseElapsed(float partialTick) {
        if (this.level == null)
            return Float.MAX_VALUE;
        return (float) (this.level.getGameTime() - this.flagRaiseStart) + partialTick;
    }

    public boolean isShowingOriginalFlag(float partialTick) {
        return this.getFlagRaiseElapsed(partialTick) < FLAG_DISAPPEAR_TICKS;
    }

    public float getFlagRaiseOffset(float partialTick) {
        float elapsed = this.getFlagRaiseElapsed(partialTick);
        if (elapsed < FLAG_DISAPPEAR_TICKS)
            return 0.0F;
        if (elapsed < FLAG_DISAPPEAR_TICKS + FLAG_APPEAR_TICKS)
            return this.flagRaiseFrom;

        float distance = this.flagRaiseFrom - this.flagRaiseTo;
        float riseTicks = Math.max(distance * FLAG_RISE_TICKS_PER_BLOCK, 1.0F);
        float progress = Mth.clamp((elapsed - FLAG_DISAPPEAR_TICKS - FLAG_APPEAR_TICKS) / riseTicks, 0.0F, 1.0F);
        float eased = 1.0F - (1.0F - progress) * (1.0F - progress);
        return this.flagRaiseFrom - distance * eased;
    }

    public float getFlagRaiseScale(float partialTick) {
        float elapsed = this.getFlagRaiseElapsed(partialTick);
        if (elapsed < FLAG_DISAPPEAR_TICKS)
            return GoalPoleBlockEntity.sampleKeyframes(DISAPPEAR_TIMES, DISAPPEAR_SCALES, elapsed / FLAG_DISAPPEAR_TICKS);
        if (elapsed < FLAG_DISAPPEAR_TICKS + FLAG_APPEAR_TICKS)
            return GoalPoleBlockEntity.sampleKeyframes(APPEAR_TIMES, APPEAR_SCALES, (elapsed - FLAG_DISAPPEAR_TICKS) / FLAG_APPEAR_TICKS);
        return 1.0F;
    }

    private void writeSyncedData(CompoundTag tag) {
        tag.putBoolean("playedAppearAnim", this.playedAppearAnim);
        tag.putBoolean("playedDisappearAnim", this.playedDisappearAnim);
        tag.putBoolean("renderWonderFlag", this.renderWonderFlag);
        tag.putBoolean("renderAmericanFlag", this.renderAmericanFlag);
        tag.putLong("flagRaiseStart", this.flagRaiseStart);
        tag.putFloat("flagRaiseFrom", this.flagRaiseFrom);
        tag.putFloat("flagRaiseTo", this.flagRaiseTo);
    }

    private static float sampleKeyframes(float[] times, float[] values, float time) {
        if (time <= times[0])
            return values[0];
        for (int i = 1; i < times.length; i++) {
            if (time <= times[i])
                return Mth.lerp((time - times[i - 1]) / (times[i] - times[i - 1]), values[i - 1], values[i]);
        }
        return values[values.length - 1];
    }
}
