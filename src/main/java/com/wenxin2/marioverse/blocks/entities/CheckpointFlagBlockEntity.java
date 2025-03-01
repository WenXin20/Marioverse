package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.network.PacketHandler;
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

public class CheckpointFlagBlockEntity extends BlockEntity implements GeoBlockEntity, Nameable {
    public static final RawAnimation CLAIM = RawAnimation.begin().thenPlayAndHold("animation.checkpoint_flag.claim");
    public static final RawAnimation SWITCH = RawAnimation.begin().thenPlayAndHold("animation.checkpoint_flag.switch");
    protected static final RawAnimation WINDY_CALM = RawAnimation.begin().thenLoop("animation.checkpoint_flag.windy_calm");
    protected static final RawAnimation WINDY_RAIN = RawAnimation.begin().thenLoop("animation.checkpoint_flag.windy_rain");
    protected static final RawAnimation WINDY_THUNDER = RawAnimation.begin().thenLoop("animation.checkpoint_flag.windy_thunder");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.checkpoint_flag");
    public static final String CUSTOM_NAME = "CustomName";
    @Nullable
    public Component name;
    private boolean renderWonderFlag;
    private boolean renderAmericanFlag;

    public CheckpointFlagBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.CHECKPOINT_FLAG_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "claim_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("claim", CLAIM));
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

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("renderWonderFlag", this.renderWonderFlag);
        tag.putBoolean("renderAmericanFlag", this.renderAmericanFlag);

        if (this.name != null) {
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
            if (this.isWonderFlag())
                PacketHandler.sendToAllClients(new WonderNamePayload(this.getBlockPos(), this.hasWonderFlag()));
            else if (this.isAmericanFlag())
                PacketHandler.sendToAllClients(new AmericaNamePayload(this.getBlockPos(), this.hasAmericanFlag()));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.renderWonderFlag = tag.getBoolean("renderWonderFlag");
        this.renderAmericanFlag = tag.getBoolean("renderAmericanFlag");

        if (tag.contains(CUSTOM_NAME, 8)) {
            this.name = parseCustomNameSafe(tag.getString(CUSTOM_NAME), provider);
            this.markUpdated();
            this.markUpdatedClients();
            PacketHandler.sendToAllClients(new AmericaNamePayload(this.getBlockPos(), this.hasAmericanFlag()));
            PacketHandler.sendToAllClients(new WonderNamePayload(this.getBlockPos(), this.hasWonderFlag()));
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
    protected void applyImplicitComponents(DataComponentInput input) {
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
