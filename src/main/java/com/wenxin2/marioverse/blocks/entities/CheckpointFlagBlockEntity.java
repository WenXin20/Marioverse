package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.client_bound.data.AmericaNamePayload;
import com.wenxin2.marioverse.network.client_bound.data.WonderNamePayload;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;
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

public class CheckpointFlagBlockEntity extends BlockEntity implements GeoBlockEntity, Nameable, RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem {
    public static final RawAnimation CLAIM = RawAnimation.begin().thenPlayAndHold("animation.checkpoint_flag.claim");
    public static final RawAnimation SWITCH = RawAnimation.begin().thenPlayAndHold("animation.checkpoint_flag.switch");
    protected static final RawAnimation WINDY_CALM = RawAnimation.begin().thenLoop("animation.checkpoint_flag.windy_calm");
    protected static final RawAnimation WINDY_RAIN = RawAnimation.begin().thenLoop("animation.checkpoint_flag.windy_rain");
    protected static final RawAnimation WINDY_THUNDER = RawAnimation.begin().thenLoop("animation.checkpoint_flag.windy_thunder");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Nullable protected ResourceKey<LootTable> lootTable;
    private ItemStack item = ItemStack.EMPTY;
    protected long lootTableSeed;

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
        tag.putBoolean("renderWonderFlag", this.renderWonderFlag);
        tag.putBoolean("renderAmericanFlag", this.renderAmericanFlag);

        if (!this.trySaveLootTable(tag) && !this.item.isEmpty())
            tag.put("item", this.item.save(provider));

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

        if (!this.tryLoadLootTable(tag)) {
            if (tag.contains("item", 10))
                this.item = ItemStack.parse(provider, tag.getCompound("item")).orElse(ItemStack.EMPTY);
            else this.item = ItemStack.EMPTY;
        }

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

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.name = input.get(DataComponents.CUSTOM_NAME);
        ItemContainerContents contents = input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        this.item = contents.copyOne();
//        input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(NonNullList.of((this.item)));
    }

    @Override
    public void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CUSTOM_NAME, this.name);
        if (!this.item.isEmpty())
            builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.item)));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("CustomName");
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @NotNull
    @Override
    public BlockEntity getContainerBlockEntity() {
        return this;
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

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveCustomOnly(provider);
    }

    public void setFromItem(ItemStack stack) {
        this.applyComponentsFromItemStack(stack);
    }

    @Override
    public void setTheItem(ItemStack stack) {
        this.unpackLootTable(null);
        this.item = stack;
    }

    @NotNull
    @Override
    public ItemStack getTheItem() {
        this.unpackLootTable(null);
        return this.item;
    }

    @NotNull
    @Override
    public ItemStack splitTheItem(int splitAmt) {
        this.unpackLootTable(null);
        ItemStack itemstack = this.item.split(splitAmt);

        if (this.item.isEmpty())
            this.item = ItemStack.EMPTY;

        return itemstack;
    }

    public boolean hasItems() {
        return !this.item.isEmpty();
    }

    @Nullable
    @Override
    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
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
