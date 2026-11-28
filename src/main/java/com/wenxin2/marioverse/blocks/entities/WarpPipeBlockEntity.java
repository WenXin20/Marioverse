package com.wenxin2.marioverse.blocks.entities;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.PipeBubblesBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.WaterSpoutBlock;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.items.LargeSnowballItem;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.sounds.MarioverseSoundTypes;
import com.wenxin2.marioverse.utils.WarpPrice;
import com.wenxin2.marioverse.world.PipeSpawner;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.slf4j.Logger;

public class WarpPipeBlockEntity extends BaseWarpBlockEntity implements MenuProvider, Nameable, Spawner,
        ContainerSingleItem.BlockContainerSingleItem {
    public static final Map<Integer, Boolean> teleportedEntities = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    public PipeText pipeText = this.createDefaultPipeText();

    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.warp_pipe");
    private static final int MAX_TEXT_LINE_WIDTH = 78;
    private static final int PAYMENT_SIGNAL_STACKS = 27;
    private static final int SPOUT_RETRACT_TICKS = 5;
    private static final int SPOUT_WATER_CHECK_TICKS = 20;
    private static final int TEXT_LINE_HEIGHT = 10;
    public static final String ALLOW_AUTOMATION = "AllowAutomation";
    public static final String BUBBLES = "Bubbles";
    public static final String BUBBLES_DISTANCE = "BubblesDistance";
    public static final String PRICE_ITEM = "PriceItem";
    public static final String PRICE_ITEM_COUNT = "PriceItemCount";
    public static final String CUSTOM_NAME = "CustomName";
    public static final String DISPLAY_TEXT_ABOVE = "displayTextAbove";
    public static final String DISPLAY_TEXT_BELOW = "displayTextBelow";
    public static final String DISPLAY_TEXT_EAST = "displayTextEast";
    public static final String DISPLAY_TEXT_NORTH = "displayTextNorth";
    public static final String DISPLAY_TEXT_SOUTH = "displayTextSouth";
    public static final String DISPLAY_TEXT_WEST = "displayTextWest";
    public static final String LIFETIME_ITEMS_EARNED = "LifetimeItemsEarned";
    public static final String LIFETIME_WARPS = "LifetimeWarps";
    public static final String LIFETIME_XP_EARNED = "LifetimeXpEarned";
    public static final String OWNER = "Owner";
    public static final String PIPE_NAME = "PipeName";
    public static final String REQUIRES_CONNECTION = "RequiresConnection";
    public static final String REQUIRES_WATER_SOURCE = "RequiresWaterSource";
    public static final String SPOUT_BUCKETED = "SpoutBucketed";
    public static final String SPOUT_HEIGHT = "SpoutHeight";
    public static final String SPOUT_REGROW_TIME = "SpoutRegrowTime";
    public static final String SPOUT_RETRACTING = "SpoutRetracting";
    public static final String SPOUT_SIZZLED = "SpoutSizzled";
    public static final String DEPOSIT = "Deposit";
    public static final String DEPOSIT_COUNT = "DepositCount";
    public static final String STASHED_XP = "StashedXp";
    public static final String VOID_PAYMENTS = "VoidPayments";
    public static final String WATER_SPOUT = "WaterSpout";
    public static final String XP_PRICE = "XpPrice";
    public static final int MAX_PRICE_ITEM_COUNT = Item.ABSOLUTE_MAX_STACK_SIZE;
    public static final int MAX_XP_PRICE = 100000;
    @Nullable public Component name;
    public Component pipeName;
    private LockCode lockKey = LockCode.NO_LOCK;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            BlockState state = WarpPipeBlockEntity.this.getBlockState();
            boolean isPipe = state.getBlock() instanceof WarpPipeBlock;

            return switch (index) {
                case 0 -> isPipe && state.getValue(WarpPipeBlock.CLOSED) ? 1 : 0;
                case 1 -> isPipe && WarpPipeBlockEntity.this.hasWaterSpout() ? 1 : 0;
                case 2 -> isPipe && WarpPipeBlockEntity.this.hasBubbles() ? 1 : 0;
                case 3 -> WarpPipeBlockEntity.this.isWaxed() ? 1 : 0;
                case 4 -> WarpPipeBlockEntity.this.getSpoutHeight();
                case 5 -> WarpPipeBlockEntity.this.getBubblesDistance();
                case 6 -> state.getBlock() instanceof ClearWarpPipeBlock && !state.getValue(ClearWarpPipeBlock.WATERLOGGED) ? 1 : 0;
                case 7 -> WarpPipeBlockEntity.this.requiresWaterSource() ? 1 : 0;
                case 8 -> WarpPipeBlockEntity.this.requiresConnection() ? 1 : 0;
                case 9 -> state.getBlock() instanceof ClearWarpPipeBlock ? 1 : 0;
                case 10 -> WarpPipeBlockEntity.this.getMenuType();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return WarpPipeMenu.DATA_COUNT;
        }
    };

    @Nullable private ResolvableProfile owner;
    private ItemStack priceItem = ItemStack.EMPTY;
    private ItemStack spawnItemStack = ItemStack.EMPTY;
    private ItemStack deposit = ItemStack.EMPTY;
    private boolean allowAutomation;
    private boolean bubbles = true;
    public boolean displayTextAbove;
    public boolean displayTextBelow;
    public boolean displayTextEast;
    public boolean displayTextNorth;
    public boolean displayTextSouth;
    public boolean displayTextWest;
    private boolean hasSpoutSizzled;
    private boolean hasSpoutWater;
    private boolean isSpoutBucketed;
    private boolean isSpoutRetracting;
    private boolean requiresConnection;
    private boolean requiresWaterSource;
    private boolean voidPayments;
    private boolean waterSpout = true;
    public int bubblesDistance = 3;
    private int priceItemCount = 1;
    private int lifetimeWarps;
    public int spawnItemDelay = 20;
    public int spoutHeight = 4;
    private int depositCount;
    private int stashedXp;
    private int xpPrice;
    private long lifetimeItemsEarned;
    private long lifetimeXpEarned;
    private long nextSpoutWaterCheckTime;
    private long spoutRegrowTime;
    private long spoutRetractTime;

    private final PipeSpawner pipeSpawner = new PipeSpawner() {
        @Override
        public void broadcastEvent(Level world, BlockPos pos, int eventId) {
            if (world.getBlockState(pos).getBlock() instanceof WarpPipeBlock block) {
                DeferredBlock<Block> deferredBlock = BlockRegistry.WARP_PIPES.get(block.getColor());
                if (deferredBlock != null)
                    world.blockEvent(pos, deferredBlock.get(), eventId, 0);
            }
        }

        @Override
        public void setNextSpawnData(@Nullable Level world, BlockPos pos, SpawnData data) {
            super.setNextSpawnData(world, pos, data);
            if (world != null) {
                BlockState blockstate = world.getBlockState(pos);
                world.sendBlockUpdated(pos, blockstate, blockstate, 4);
            }
        }

        @Override
        public Either<BlockEntity, Entity> getOwner() {
            return Either.left(WarpPipeBlockEntity.this);
        }
    };

    public WarpPipeBlockEntity(final BlockPos pos, final BlockState state) {
        this(BlockEntityRegistry.WARP_PIPE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public boolean isValidBlockState(BlockState state) {
        return this.getType().isValid(state) || state.getBlock() instanceof WarpPipeBlock;
    }

    public WarpPipeBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
        this.displayTextNorth = Boolean.TRUE;
        this.displayTextSouth = Boolean.TRUE;
        this.displayTextEast = Boolean.TRUE;
        this.displayTextWest = Boolean.TRUE;
        this.displayTextAbove = Boolean.TRUE;
        this.displayTextBelow = Boolean.TRUE;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.lockKey.addToTag(tag);
        this.pipeSpawner.save(tag);
        tag.putBoolean(ALLOW_AUTOMATION, this.allowAutomation);
        tag.putBoolean(BUBBLES, this.bubbles);
        tag.putBoolean(DISPLAY_TEXT_ABOVE, this.displayTextAbove);
        tag.putBoolean(DISPLAY_TEXT_BELOW, this.displayTextBelow);
        tag.putBoolean(DISPLAY_TEXT_EAST, this.displayTextEast);
        tag.putBoolean(DISPLAY_TEXT_NORTH, this.displayTextNorth);
        tag.putBoolean(DISPLAY_TEXT_SOUTH, this.displayTextSouth);
        tag.putBoolean(DISPLAY_TEXT_WEST, this.displayTextWest);
        tag.putBoolean(REQUIRES_CONNECTION, this.requiresConnection);
        tag.putBoolean(REQUIRES_WATER_SOURCE, this.requiresWaterSource);
        tag.putBoolean(SPOUT_BUCKETED, this.isSpoutBucketed);
        tag.putBoolean(SPOUT_RETRACTING, this.isSpoutRetracting);
        tag.putBoolean(SPOUT_SIZZLED, this.hasSpoutSizzled);
        tag.putBoolean(VOID_PAYMENTS, this.voidPayments);
        tag.putBoolean(WATER_SPOUT, this.waterSpout);
        tag.putInt(BUBBLES_DISTANCE, this.bubblesDistance);
        tag.putInt(PRICE_ITEM_COUNT, this.priceItemCount);
        tag.putInt(LIFETIME_WARPS, this.lifetimeWarps);
        tag.putInt(SPOUT_HEIGHT, this.spoutHeight);
        tag.putInt(DEPOSIT_COUNT, this.depositCount);
        tag.putInt(STASHED_XP, this.stashedXp);
        tag.putInt(XP_PRICE, this.xpPrice);
        tag.putLong(LIFETIME_ITEMS_EARNED, this.lifetimeItemsEarned);
        tag.putLong(LIFETIME_XP_EARNED, this.lifetimeXpEarned);
        tag.putLong(SPOUT_REGROW_TIME, this.spoutRegrowTime);
        tag.putShort("SpawnItemDelay", (short) this.spawnItemDelay);

        if (this.name != null)
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
        if (this.pipeName != null)
            tag.putString(PIPE_NAME, Component.Serializer.toJson(this.pipeName, provider));
        if (!this.spawnItemStack.isEmpty())
            tag.put("SpawnItem", this.spawnItemStack.save(provider));
        if (this.owner != null)
            ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, this.owner).resultOrPartial(LOGGER::error)
                    .ifPresent(owner -> tag.put(OWNER, owner));
        if (!this.priceItem.isEmpty())
            tag.put(PRICE_ITEM, this.priceItem.save(provider));
        if (!this.deposit.isEmpty())
            tag.put(DEPOSIT, this.deposit.save(provider));


        PipeText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.pipeText).resultOrPartial(LOGGER::error)
                .ifPresent(pipeName -> tag.put(PIPE_NAME, pipeName));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.allowAutomation = tag.getBoolean(ALLOW_AUTOMATION);
        this.bubbles = !tag.contains(BUBBLES) || tag.getBoolean(BUBBLES);
        this.bubblesDistance = tag.getInt(BUBBLES_DISTANCE);
        this.priceItem = tag.contains(PRICE_ITEM, 10)
                ? ItemStack.parse(provider, tag.getCompound(PRICE_ITEM)).map(stack -> new ItemStack(stack.getItem())).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        this.priceItemCount = Math.max(1, tag.getInt(PRICE_ITEM_COUNT));
        this.displayTextAbove = tag.getBoolean(DISPLAY_TEXT_ABOVE);
        this.displayTextBelow = tag.getBoolean(DISPLAY_TEXT_BELOW);
        this.displayTextEast = tag.getBoolean(DISPLAY_TEXT_EAST);
        this.displayTextNorth = tag.getBoolean(DISPLAY_TEXT_NORTH);
        this.displayTextSouth = tag.getBoolean(DISPLAY_TEXT_SOUTH);
        this.displayTextWest = tag.getBoolean(DISPLAY_TEXT_WEST);
        this.hasSpoutSizzled = tag.getBoolean(SPOUT_SIZZLED);
        this.isSpoutBucketed = tag.getBoolean(SPOUT_BUCKETED);
        this.isSpoutRetracting = tag.getBoolean(SPOUT_RETRACTING);
        this.lifetimeItemsEarned = tag.getLong(LIFETIME_ITEMS_EARNED);
        this.lifetimeWarps = tag.getInt(LIFETIME_WARPS);
        this.lifetimeXpEarned = tag.getLong(LIFETIME_XP_EARNED);
        this.lockKey = LockCode.fromTag(tag);
        this.owner = tag.contains(OWNER) ? ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, tag.get(OWNER))
                .resultOrPartial(LOGGER::error).orElse(null) : null;
        this.pipeSpawner.load(this.level, this.worldPosition, tag);
        this.requiresConnection = tag.getBoolean(REQUIRES_CONNECTION);
        this.requiresWaterSource = tag.getBoolean(REQUIRES_WATER_SOURCE);
        this.spawnItemDelay = tag.getShort("SpawnItemDelay");
        this.spoutHeight = tag.getInt(SPOUT_HEIGHT);
        this.spoutRegrowTime = tag.getLong(SPOUT_REGROW_TIME);
        this.deposit = tag.contains(DEPOSIT, 10)
                ? ItemStack.parse(provider, tag.getCompound(DEPOSIT)).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        this.depositCount = tag.getInt(DEPOSIT_COUNT);
        this.stashedXp = tag.getInt(STASHED_XP);
        this.voidPayments = tag.getBoolean(VOID_PAYMENTS);
        this.waterSpout = tag.getBoolean(WATER_SPOUT);
        this.xpPrice = tag.getInt(XP_PRICE);

        if (tag.contains(CUSTOM_NAME, 8))
            this.name = parseCustomNameSafe(tag.getString(CUSTOM_NAME), provider);

        if (tag.contains(PIPE_NAME, 8))
            this.pipeName = parseCustomNameSafe(tag.getString(PIPE_NAME), provider);

        if (tag.contains("SpawnItem", 10))
            this.spawnItemStack = ItemStack.parse(provider, tag.getCompound("SpawnItem")).orElse(ItemStack.EMPTY);
        else this.spawnItemStack = ItemStack.EMPTY;

        if (tag.contains(PIPE_NAME)) {
            PipeText.DIRECT_CODEC.parse(NbtOps.INSTANCE, tag.getCompound(PIPE_NAME)).resultOrPartial(LOGGER::error)
                    .ifPresent(text -> this.pipeText = this.loadLines(text));
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        ItemContainerContents contents = input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        this.name = input.get(DataComponents.CUSTOM_NAME);
        this.pipeName = input.get(DataComponentRegistry.PIPE_NAME);
        this.spawnItemStack = contents.copyOne();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CUSTOM_NAME, this.name);
        builder.set(DataComponentRegistry.PIPE_NAME, this.pipeName);
        if (!this.spawnItemStack.isEmpty())
            builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.spawnItemStack)));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = this.saveCustomOnly(provider);
        tag.remove("SpawnPotentials");
        return tag;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        if (this.level != null)
            return new WarpPipeMenu(id, inventory, this.getDataAccess(), ContainerLevelAccess.create(this.level, this.getBlockPos()),
                    this.getBlockPos(), this.getDestinationPos(), this.createPriceContainer(),
                    this.createDepositContainer(), this.canManagePayments(player));
        else return null;
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

    @Override
    public void setTheItem(ItemStack stack) {
        this.spawnItemStack = stack;
    }

    @NotNull
    @Override
    public ItemStack getTheItem() {
        return this.spawnItemStack;
    }

    @NotNull
    @Override
    public ItemStack splitTheItem(int splitAmt) {
        ItemStack itemstack = this.spawnItemStack.split(splitAmt);

        if (this.spawnItemStack.isEmpty())
            this.spawnItemStack = ItemStack.EMPTY;

        return itemstack;
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, WarpPipeBlockEntity warpPipeBE) {
        warpPipeBE.pipeSpawner.clientTick(world, pos);
    }

    public static void serverTick(Level world, BlockPos pos, BlockState state, WarpPipeBlockEntity warpPipeBE) {
        if (world instanceof ServerLevel serverWorld) {
            warpPipeBE.pipeSpawner.serverTick(serverWorld, pos);
            BaseWarpBlockEntity.LOADED_WARP_BLOCKS.computeIfAbsent(serverWorld, level -> new HashSet<>()).add(pos);
        }

        if (state.hasProperty(WarpPipeBlock.CLOSED) && !state.getValue(WarpPipeBlock.CLOSED)
                && state.hasProperty(WarpPipeBlock.ENTRANCE) && state.getValue(WarpPipeBlock.ENTRANCE)) {
            if (warpPipeBE.spawnItemDelay > 0 && !warpPipeBE.getTheItem().isEmpty()) {
                warpPipeBE.spawnItemDelay--;
            } else if (!warpPipeBE.getTheItem().isEmpty()) {
                ItemStack stack = warpPipeBE.getTheItem().copyWithCount(1);

                warpPipeBE.spawnFromWarpPipe(world, pos, stack);
                MarioverseSoundTypes.playSounds(world, pos, stack, warpPipeBE);
                warpPipeBE.spawnItemDelay = 180;
            }
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        return this.level != null ? this.pipeSpawner.onEventTriggered(this.level, id) : super.triggerEvent(id, type);
    }

    @Override
    public void setEntityId(@NotNull EntityType<?> entityType, RandomSource random) {
        this.pipeSpawner.setEntityId(entityType, this.level, random, this.worldPosition);
        this.setChanged();
    }

    public BaseSpawner getPipeSpawner() {
        return this.pipeSpawner;
    }

    public void setCustomName(Component name) {
        this.name = name;
        this.markUpdated();
        this.getUpdatePacket();
    }

    public void setPipeName(Component name) {
        this.pipeName = name;
        this.pipeText.setMessage(0, name);
        this.markUpdated();
        this.getUpdatePacket();
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    public Component getCustomName(Component name) {
        return this.name = name;
    }

    @NotNull
    @Override
    public Component getName() {
        return !this.pipeText.getMessage(0, false).contains(Component.empty())
                ? this.pipeText.getMessage(0, false) : this.name != null ? this.name : DEFAULT_NAME;
    }

    protected PipeText createDefaultPipeText() {
        return new PipeText();
    }

    public int getTextLineHeight() {
        return TEXT_LINE_HEIGHT;
    }

    public int getMaxTextLineWidth() {
        return MAX_TEXT_LINE_WIDTH;
    }

    public PipeText getPipeText() {
        return this.pipeText;
    }

    public Component getPipeNameComponent() {
        return !this.pipeText.getMessage(0, false).contains(Component.empty())
                ? this.pipeText.getMessage(0, false) : this.name != null ? this.name : DEFAULT_NAME;
    }

    public boolean updateText(UnaryOperator<PipeText> text) {
        PipeText pipeText = this.getPipeText();
        this.markUpdated();
        this.getUpdatePacket();
        return this.setText(text.apply(pipeText));
    }

    public boolean setText(PipeText text) {
        if (text != this.pipeText) {
            this.pipeText = text;
            this.markUpdated();
            this.getUpdatePacket();
            return true;
        } else return false;
    }

    public boolean hasTextNorth() {
        return this.displayTextNorth;
    }

    public boolean hasTextSouth() {
        return this.displayTextSouth;
    }

    public boolean hasTextEast() {
        return this.displayTextEast;
    }

    public boolean hasTextWest() {
        return this.displayTextWest;
    }

    public boolean hasTextAbove() {
        return this.displayTextAbove;
    }

    public boolean hasTextBelow() {
        return this.displayTextBelow;
    }

    public void setTextNorth(boolean displayTextNorth) {
        if (this.displayTextNorth != displayTextNorth) {
            this.displayTextNorth = displayTextNorth;
            this.markUpdated();
            this.getUpdatePacket();
        }
    }

    public void setTextSouth(boolean displayTextSouth) {
        if (this.displayTextSouth != displayTextSouth) {
            this.displayTextSouth = displayTextSouth;
            this.markUpdated();
            this.getUpdatePacket();
        }
    }

    public void setTextEast(boolean displayTextEast) {
        if (this.displayTextEast != displayTextEast) {
            this.displayTextEast = displayTextEast;
            this.markUpdated();
            this.getUpdatePacket();
        }
    }

    public void setTextWest(boolean displayTextWest) {
        if (this.displayTextWest != displayTextWest) {
            this.displayTextWest = displayTextWest;
            this.markUpdated();
            this.getUpdatePacket();
        }
    }

    public void setTextAbove(boolean displayTextAbove) {
        if (this.displayTextAbove != displayTextAbove) {
            this.displayTextAbove = displayTextAbove;
            this.markUpdated();
            this.getUpdatePacket();
        }
    }

    public void setTextBelow(boolean displayTextBelow) {
        if (this.displayTextBelow != displayTextBelow) {
            this.displayTextBelow = displayTextBelow;
            this.markUpdated();
            this.getUpdatePacket();
        }
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    private static CommandSourceStack createCommandSourceStack(@Nullable Player player, Level world, BlockPos pos) {
        String s = player == null ? "Pipe" : player.getName().getString();
        Component component = player == null ? Component.literal("Pipe") : player.getDisplayName();
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO,
                (ServerLevel) world, 2, s, component, world.getServer(), player);
    }

    private PipeText loadLines(PipeText text) {
        for(int i = 0; i < 1; ++i) {
            Component message = this.loadLine(text.getMessage(i, false));
            Component filteredMessage = this.loadLine(text.getMessage(i, true));
            text = text.setMessage(i, message, filteredMessage);
        }

        return text;
    }

    private Component loadLine(Component text) {
        Level world = this.level;
        if (world instanceof ServerLevel serverWorld) {
            try {
                return ComponentUtils
                        .updateForEntity(createCommandSourceStack(null, serverWorld, this.worldPosition), text, null, 0);
            } catch (CommandSyntaxException commandsyntaxexception) {
            }
        }

        return text;
    }

    public int getMenuType() {
        return this.getData(DataAttachmentRegistry.MENU_TYPE);
    }

    public void setMenuType(int menuType) {
        this.setData(DataAttachmentRegistry.MENU_TYPE, menuType);
        this.setChanged();
    }


    public boolean hasOwner() {
        return this.owner != null;
    }

    @Nullable
    public ResolvableProfile getPipeOwner() {
        return this.owner;
    }

    public void setPipeOwner(Player player) {
        this.setPipeOwner(player.getGameProfile());
    }

    public void setPipeOwner(GameProfile profile) {
        this.owner = new ResolvableProfile(profile);
        this.setChanged();
        this.sendData();
    }

    public boolean isPipeOwner(Player player) {
        return this.isPipeOwner(player.getUUID());
    }

    public boolean isPipeOwner(UUID id) {
        return this.owner != null && this.owner.id().map(id::equals).orElse(false);
    }

    public boolean canManagePayments(Player player) {
        return this.isPipeOwner(player) || player.hasPermissions(2);
    }

    public ItemStack getDeposit() {
        return this.deposit;
    }

    public int getDepositCount() {
        return this.depositCount;
    }

    public int getStashedXp() {
        return this.stashedXp;
    }

    public void collectXp(Player player) {
        if (this.stashedXp <= 0 || !(player instanceof ServerPlayer serverPlayer))
            return;

        int xp = WarpPipeBlockEntity.repairWithXp(serverPlayer, this.stashedXp);
        if (xp > 0)
            serverPlayer.giveExperiencePoints(xp);

        this.stashedXp = 0;
        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                0.1F, 0.9F + serverPlayer.getRandom().nextFloat() * 0.2F);
        this.setChanged();
        this.sendData();
    }

    private static int repairWithXp(ServerPlayer player, int xp) {
        while (xp > 0) {
            Optional<EnchantedItemInUse> item = EnchantmentHelper.getRandomItemWith(EnchantmentEffectComponents.REPAIR_WITH_XP, player, ItemStack::isDamaged);
            if (item.isEmpty())
                return xp;

            ItemStack stack = item.get().itemStack();
            int repairable = EnchantmentHelper.modifyDurabilityToRepairFromXp(player.serverLevel(), stack, (int) (xp * stack.getXpRepairRatio()));
            int repaired = Math.min(repairable, stack.getDamageValue());
            if (repaired <= 0)
                return xp;

            stack.setDamageValue(stack.getDamageValue() - repaired);
            xp -= (int) ((long) repaired * xp / repairable);
        }
        return 0;
    }

    public boolean isVoidPayments() {
        return this.voidPayments;
    }

    public void setVoidPayments(boolean voidPayments) {
        this.voidPayments = voidPayments;
        this.setChanged();
        this.sendData();
    }

    public boolean isAllowAutomation() {
        return this.allowAutomation;
    }

    public void setAllowAutomation(boolean allowAutomation) {
        this.allowAutomation = allowAutomation;
        this.setChanged();
        this.sendData();
    }

    public int getPaymentSignal() {
        if (this.depositCount <= 0)
            return 0;

        float fullness = Math.min(1.0F, this.depositCount / (float) (PAYMENT_SIGNAL_STACKS * this.deposit.getMaxStackSize()));
        return Mth.floor(fullness * 14.0F) + 1;
    }

    public int getLifetimeWarps() {
        return this.lifetimeWarps;
    }

    public long getLifetimeItemsEarned() {
        return this.lifetimeItemsEarned;
    }

    public long getLifetimeXpEarned() {
        return this.lifetimeXpEarned;
    }

    public void resetLifetimeEarnings() {
        this.lifetimeWarps = 0;
        this.lifetimeItemsEarned = 0;
        this.lifetimeXpEarned = 0;
        this.setChanged();
        this.sendData();
    }

    public int getXpPrice() {
        return this.xpPrice;
    }

    public void setXpPrice(int xpPrice) {
        this.xpPrice = Mth.clamp(xpPrice, 0, MAX_XP_PRICE);
        this.setChanged();
        this.sendData();
    }

    public ItemStack getPriceItem() {
        return this.priceItem;
    }

    public int getPriceItemCount() {
        return this.priceItemCount;
    }

    public boolean canChangePriceItem(ItemStack item) {
        return this.depositCount <= 0 || item.isEmpty()
                || ItemStack.isSameItemSameComponents(this.getPaymentItem(item), this.deposit);
    }

    public boolean setPriceItem(ItemStack stack) {
        if (!this.canChangePriceItem(stack))
            return false;

        this.priceItem = stack.isEmpty() ? ItemStack.EMPTY : new ItemStack(stack.getItem());
        if (!stack.isEmpty())
            this.priceItemCount = Mth.clamp(stack.getCount(), 1, MAX_PRICE_ITEM_COUNT);
        this.setChanged();
        this.sendData();
        return true;
    }

    public void setPriceItemCount(int count) {
        this.priceItemCount = Mth.clamp(count, 1, MAX_PRICE_ITEM_COUNT);
        this.setChanged();
        this.sendData();
    }

    public WarpPrice.Price getWarpPrice() {
        if (this.priceItem.isEmpty() || this.priceItemCount <= 0)
            return new WarpPrice.Price(this.xpPrice, 0);

        int coinValue = WarpPrice.getCoinValue(this.priceItem);
        if (coinValue > 0)
            return new WarpPrice.Price(this.xpPrice, coinValue * this.priceItemCount);
        return new WarpPrice.Price(this.xpPrice, 0, this.priceItem, this.priceItemCount);
    }

    public void receivePayment(WarpPrice.Price price, int payers) {
        ItemStack paymentItem = price.coins() > 0 ? new ItemStack(BlockRegistry.COIN.asItem()) : price.item();
        int itemCount = (price.coins() > 0 ? price.coins() : price.itemCount()) * payers;
        int xp = price.xp() * payers;

        this.lifetimeWarps++;
        this.lifetimeItemsEarned += itemCount;
        this.lifetimeXpEarned += xp;

        if (!this.voidPayments) {
            this.stashedXp += xp;
            if (itemCount > 0 && (this.depositCount <= 0 || ItemStack.isSameItemSameComponents(this.deposit, paymentItem))) {
                this.deposit = paymentItem.copyWithCount(1);
                this.depositCount += itemCount;
            }
        }
        this.setChanged();
        this.sendData();
    }

    private ItemStack getPaymentItem(ItemStack priceItem) {
        return WarpPrice.getCoinValue(priceItem) > 0 ? new ItemStack(BlockRegistry.COIN.asItem()) : priceItem;
    }

    public Container createPriceContainer() {
        return new SimpleContainer(1) {
            @NotNull
            @Override
            public ItemStack getItem(int slot) {
                if (WarpPrice.hasConfigPipePrice()) {
                    int coins = WarpPipeBlockEntity.this.getConfigPrice().coins();
                    return coins > 0 ? new ItemStack(BlockRegistry.COIN.asItem(), coins) : ItemStack.EMPTY;
                }
                return WarpPipeBlockEntity.this.priceItem.isEmpty() ? ItemStack.EMPTY
                        : WarpPipeBlockEntity.this.priceItem.copyWithCount(WarpPipeBlockEntity.this.priceItemCount);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                WarpPipeBlockEntity.this.setPriceItem(stack);
            }
        };
    }

    public WarpPrice.Price getConfigPrice() {
        return this.level == null ? WarpPrice.FREE : WarpPrice.getConfigPipePrice(this.level, this.worldPosition, this.getDestinationPos());
    }

    public Container createDepositContainer() {
        return new SimpleContainer(1) {
            @NotNull
            @Override
            public ItemStack getItem(int slot) {
                return WarpPipeBlockEntity.this.depositCount <= 0 ? ItemStack.EMPTY
                        : WarpPipeBlockEntity.this.deposit.copyWithCount(WarpPipeBlockEntity.this.depositCount);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {}
        };
    }

    public ItemStack takeDeposit(int amount) {
        int count = Math.min(Math.min(amount, this.depositCount), this.deposit.getMaxStackSize());
        if (count <= 0)
            return ItemStack.EMPTY;

        this.depositCount -= count;
        this.setChanged();
        this.sendData();
        return this.deposit.copyWithCount(count);
    }

    public void collectDeposit(Player player, int amount) {
        while (amount > 0 && this.depositCount > 0) {
            ItemStack stack = this.takeDeposit(amount);
            int taken = stack.getCount();

            player.getInventory().add(stack);
            this.depositCount += stack.getCount();
            amount -= taken - stack.getCount();

            if (!stack.isEmpty())
                break;
        }
        this.setChanged();
        this.sendData();
    }

    public boolean hasPayment() {
        if (this.depositCount > 0)
            return true;
        if (this.voidPayments)
            return false;

        WarpPrice.Price price = WarpPrice.hasConfigPipePrice() ? this.getConfigPrice() : this.getWarpPrice();
        return price.coins() > 0 || price.itemCount() > 0;
    }

    public void closePipe(ServerPlayer player) {
        float pitchLow = 0.15F + level.random.nextFloat() * 0.2F;
        float pitchHigh = 0.5F + level.random.nextFloat() * 0.2F;

        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (state.getValue(WarpPipeBlock.CLOSED)) {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.CLOSED, Boolean.FALSE), 3);
                this.playSound(this.level, menuPos, SoundRegistry.PIPE_OPENS.get(), SoundSource.BLOCKS, 1.0F, pitchLow);
            } else {
                BlockPos spoutPos = menuPos.relative(state.getValue(WarpPipeBlock.FACING));
                if (this.level.getBlockState(spoutPos).getBlock() instanceof WaterSpoutBlock)
                    this.level.destroyBlock(spoutPos, false);
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.CLOSED, Boolean.TRUE), 0);
                this.playSound(this.level, menuPos, SoundRegistry.PIPE_CLOSES.get(), SoundSource.BLOCKS, 1.0F, pitchHigh);
            }
        }
    }

    public void toggleWaterSpout(ServerPlayer player) {
        float pitchLow = 0.15F + level.random.nextFloat() * 0.2F;
        float pitchHigh = 0.5F + level.random.nextFloat() * 0.2F;

        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (this.level.getBlockEntity(menuPos) instanceof WarpPipeBlockEntity pipeBlockEntity) {
                if (pipeBlockEntity.hasWaterSpout()) {
                    pipeBlockEntity.setSpoutRetracting(true);
                    pipeBlockEntity.setWaterSpout(false);
                    this.level.scheduleTick(menuPos, state.getBlock(), 3);
                    this.playSound(this.level, menuPos, SoundRegistry.WATER_SPOUT_BREAK.get(), SoundSource.BLOCKS, 1.0F, pitchLow);
                } else {
                    pipeBlockEntity.setWaterSpout(true);
                    this.level.scheduleTick(menuPos, state.getBlock(), 3);
                    this.playSound(this.level, menuPos, SoundRegistry.WATER_SPOUT_PLACE.get(), SoundSource.BLOCKS, 1.0F, pitchHigh);
                }
            }
        }
    }

    public boolean hasWaterSpout() {
        return this.waterSpout;
    }

    public void setWaterSpout(boolean waterSpout) {
        this.waterSpout = waterSpout;
        if (waterSpout) {
            this.hasSpoutSizzled = false;
            this.isSpoutBucketed = false;
            this.isSpoutRetracting = false;
        }
        this.updateColumns();
    }

    public int getSpoutGrowHeight(BlockPos spoutPos, Direction facing) {
        if (this.level == null)
            return 0;

        int length = WaterSpoutBlock.getColumnLength(this.level, spoutPos, facing, this.spoutHeight);
        boolean isBlocked = !WaterSpoutBlock.canExistIn(this.level, spoutPos.relative(facing, length), facing);

        if (length >= this.spoutHeight || isBlocked) {
            this.setSpoutRegrowTime(0L);
            this.setSpoutBucketed(false);
            return length;
        }

        int delay = ConfigRegistry.WATER_SPOUT_REGROW_DELAY.get();
        long gameTime = this.level.getGameTime();

        if (length == 0 && !this.isSpoutBucketed && delay > 0) {
            this.setSpoutRegrowTime(1 < this.spoutHeight ? gameTime + delay : 0L);
            return 1;
        }

        if (delay <= 0) {
            this.setSpoutRegrowTime(0L);
            return this.spoutHeight;
        }
        if (this.spoutRegrowTime <= 0L || this.spoutRegrowTime > gameTime + delay) {
            this.setSpoutRegrowTime(gameTime + delay);
            return length;
        }
        if (gameTime < this.spoutRegrowTime)
            return length;

        this.setSpoutRegrowTime(length + 1 < this.spoutHeight ? gameTime + delay : 0L);
        this.setSpoutBucketed(false);
        return length + 1;
    }

    public boolean isSpoutRetracting() {
        return this.isSpoutRetracting;
    }

    public void setSpoutRetracting(boolean isSpoutRetracting) {
        if (this.isSpoutRetracting != isSpoutRetracting) {
            this.isSpoutRetracting = isSpoutRetracting;
            this.spoutRetractTime = 0L;
            this.setChanged();
        }
    }

    public void retractWaterSpout(ServerLevel level, BlockPos spoutPos, Direction facing) {
        int length = WaterSpoutBlock.getColumnLength(level, spoutPos, facing, this.spoutHeight);
        if (length == 0) {
            this.spoutRetractTime = 0L;
            this.setSpoutRetracting(false);
            return;
        }

        long gameTime = level.getGameTime();
        if (this.spoutRetractTime <= 0L || this.spoutRetractTime > gameTime + SPOUT_RETRACT_TICKS) {
            this.spoutRetractTime = gameTime + SPOUT_RETRACT_TICKS;
            return;
        }
        if (gameTime < this.spoutRetractTime)
            return;

        level.destroyBlock(spoutPos.relative(facing, length - 1), false);
        if (length > 1)
            this.spoutRetractTime = gameTime + SPOUT_RETRACT_TICKS;
        else {
            this.spoutRetractTime = 0L;
            this.setSpoutRetracting(false);
        }
    }

    public void setSpoutBucketed(boolean isSpoutBucketed) {
        if (this.isSpoutBucketed != isSpoutBucketed) {
            this.isSpoutBucketed = isSpoutBucketed;
            this.setChanged();
        }
    }

    private void setSpoutRegrowTime(long spoutRegrowTime) {
        if (this.spoutRegrowTime != spoutRegrowTime) {
            this.spoutRegrowTime = spoutRegrowTime;
            this.setChanged();
        }
    }

    public int getWaterSpoutHeight(BlockPos spoutPos, Direction facing) {
        return this.waterSpout && this.hasSpoutWaterSource() ? this.getSpoutGrowHeight(spoutPos, facing) : 0;
    }

    public boolean hasSpoutWaterSource() {
        if (this.level == null)
            return false;

        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof WarpPipeBlock) || state.getValue(WarpPipeBlock.CLOSED))
            return false;
        if (state.getBlock() instanceof ClearWarpPipeBlock)
            return state.getValue(ClearWarpPipeBlock.WATERLOGGED);
        if (!ConfigRegistry.WATER_SPOUTS_REQUIRE_WATER.get() && !this.requiresWaterSource)
            return true;

        long gameTime = this.level.getGameTime();
        if (gameTime >= this.nextSpoutWaterCheckTime || this.nextSpoutWaterCheckTime - gameTime > SPOUT_WATER_CHECK_TICKS) {
            this.hasSpoutWater = WarpPipeBlock.isConnectedToWater(this.level, this.worldPosition);
            this.nextSpoutWaterCheckTime = gameTime + SPOUT_WATER_CHECK_TICKS;
        }
        return this.hasSpoutWater;
    }

    public void sizzleWaterSpout(ServerLevel level) {
        BlockState state = this.getBlockState();
        if (this.hasSpoutSizzled || !this.waterSpout || !(state.getBlock() instanceof WarpPipeBlock) || state.getValue(WarpPipeBlock.CLOSED))
            return;

        BlockPos spoutPos = this.worldPosition.relative(state.getValue(WarpPipeBlock.FACING));
        if (!level.getBlockState(spoutPos).isAir())
            return;

        this.hasSpoutSizzled = true;
        this.setChanged();
        level.playSound(null, spoutPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
                2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, spoutPos.getX() + 0.5, spoutPos.getY() + 0.5, spoutPos.getZ() + 0.5,
                8, 0.25, 0.25, 0.25, 0.0);
    }

    public boolean requiresWaterSource() {
        return this.requiresWaterSource;
    }

    public void setRequiresWaterSource(boolean requiresWaterSource) {
        if (this.requiresWaterSource != requiresWaterSource) {
            this.requiresWaterSource = requiresWaterSource;
            this.nextSpoutWaterCheckTime = 0;
            this.setChanged();
        }
    }

    public boolean requiresConnection() {
        return this.requiresConnection;
    }

    public void setRequiresConnection(boolean requiresConnection) {
        if (this.requiresConnection != requiresConnection) {
            this.requiresConnection = requiresConnection;
            this.setChanged();
        }
    }

    public void updateRequiresConnection(boolean requiresConnection) {
        this.setRequiresConnection(requiresConnection);
        this.sendData();

        BlockPos destinationPos = this.getDestinationPos();
        ResourceKey<Level> destinationDim = this.getDestinationDim();
        if (this.level == null || destinationPos == null || !this.level.isLoaded(destinationPos)
                || (destinationDim != null && !destinationDim.equals(this.level.dimension())))
            return;

        if (this.level.getBlockEntity(destinationPos) instanceof WarpPipeBlockEntity destinationBE && !destinationBE.isWaxed()) {
            destinationBE.setRequiresConnection(requiresConnection);
            destinationBE.sendData();
        }
    }

    public boolean hasBubbles() {
        return this.bubbles;
    }

    public void setBubbles(boolean bubbles) {
        this.bubbles = bubbles;
        this.updateColumns();
    }

    private void updateColumns() {
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
            state.updateNeighbourShapes(this.level, this.worldPosition, Block.UPDATE_ALL);
            this.level.updateNeighbourForOutputSignal(this.worldPosition, state.getBlock());
        }
    }

    public void waterSpoutHeight(ServerPlayer player, int spoutHeight) {
        if (this.level != null && this.getUpdatePacket() != null && player.containerMenu instanceof WarpPipeMenu) {
            if ( this.level.getBlockState(this.getBlockPos()).getBlock() instanceof WarpPipeBlock) {
                this.setSpoutHeight(spoutHeight);
            }
        }
    }

    public void setSpoutHeight(int spoutHeight) {
        Level world = this.level;
        if (world != null) {
            BlockPos pos = this.getBlockPos();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                if (world.getBlockState(pos.above()).getBlock() instanceof WaterSpoutBlock)
                    world.destroyBlock(pos.above(), false);
                this.spoutHeight = spoutHeight;
                pipeBlockEntity.setChanged();
            }
        }
    }

    // Only returning default of 4
    public int getSpoutHeight() {
        return this.spoutHeight;
    }

    public void bubblesDistance(ServerPlayer player, int bubblesDistance) {
        if (this.level != null && this.getUpdatePacket() != null && player.containerMenu instanceof WarpPipeMenu) {
            if ( this.level.getBlockState(this.getBlockPos()).getBlock() instanceof WarpPipeBlock) {
                this.setBubblesDistance(bubblesDistance);
            }
        }
    }

    public void setBubblesDistance(int bubblesDistance) {
        Level world = this.level;

        if (world != null) {
            BlockPos pos = this.getBlockPos();
            BlockState state = world.getBlockState(pos);
            BlockState stateAbove = world.getBlockState(pos.above());
            BlockState stateBelow = world.getBlockState(pos.below());
            BlockState stateNorth = world.getBlockState(pos.north());
            BlockState stateSouth = world.getBlockState(pos.south());
            BlockState stateEast = world.getBlockState(pos.east());
            BlockState stateWest = world.getBlockState(pos.west());

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                if (stateAbove.getBlock() instanceof PipeBubblesBlock && state.getValue(WarpPipeBlock.FACING) == Direction.UP)
                    world.destroyBlock(pos.above(), false);
                if (stateBelow.getBlock() instanceof PipeBubblesBlock && state.getValue(WarpPipeBlock.FACING) == Direction.DOWN)
                    world.destroyBlock(pos.below(), false);
                if (stateNorth.getBlock() instanceof PipeBubblesBlock && state.getValue(WarpPipeBlock.FACING) == Direction.NORTH)
                    world.destroyBlock(pos.north(), false);
                if (stateSouth.getBlock() instanceof PipeBubblesBlock && state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH)
                    world.destroyBlock(pos.south(), false);
                if (stateEast.getBlock() instanceof PipeBubblesBlock && state.getValue(WarpPipeBlock.FACING) == Direction.EAST)
                    world.destroyBlock(pos.east(), false);
                if (stateWest.getBlock() instanceof PipeBubblesBlock && state.getValue(WarpPipeBlock.FACING) == Direction.WEST)
                    world.destroyBlock(pos.west(), false);

                this.bubblesDistance = bubblesDistance;
                pipeBlockEntity.setChanged();
            }
        }
    }

    // Only returning default of 3
    public int getBubblesDistance() {
        return this.bubblesDistance;
    }

    public void togglePipeBubbles(ServerPlayer player) {
        float pitchLow = 0.15F + level.random.nextFloat() * 0.2F;
        float pitchHigh = 0.5F + level.random.nextFloat() * 0.2F;

        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (this.level.getBlockEntity(menuPos) instanceof WarpPipeBlockEntity pipeBlockEntity) {
                if (pipeBlockEntity.hasBubbles()) {
                    pipeBlockEntity.setBubbles(false);
                    this.level.scheduleTick(menuPos, state.getBlock(), 3);
                    this.playSound(this.level, menuPos, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 1.0F, pitchLow);
                } else {
                    pipeBlockEntity.setBubbles(true);
                    this.level.scheduleTick(menuPos, state.getBlock(), 3);
                    this.playSound(this.level, menuPos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 1.0F, pitchHigh);
                }
            }
        }
    }

    // Method to mark an entity as teleported
    public static void markEntityTeleported(Entity entity) {
        if (entity != null)
            teleportedEntities.put(entity.getId(), true);
    }

    private static float getWarpYaw(Level level, Entity entity, Direction facing) {
        Vec3 vec3 = Vec3.atLowerCornerOf(facing.getNormal());

        Object object = null;
        if (SableProvider.isLoaded())
            object = SableProvider.getContext(level, entity);

        if (object instanceof SableProvider.SableContext context) {
            Quaterniondc rotation = context.subLevel.logicalPose().orientation();
            Vector3d rotated = rotation.transform(new Vector3d(vec3.x, vec3.y, vec3.z));
            vec3 = new Vec3(rotated.x, rotated.y, rotated.z);
        }
        return (float) Math.toDegrees(Math.atan2(-vec3.x, vec3.z));
    }

    public static void warp(Level level, BlockPos warpPos, BlockState state, Entity entity) {
        float warpYaw = WarpPipeBlockEntity.getWarpYaw(level, entity, level.getBlockState(warpPos).getValue(DirectionalBlock.FACING));
        float pitch = 1.0F + level.random.nextFloat() * 0.2F;
        Entity passengerEntity = entity.getControllingPassenger();
        Entity vehicle = entity.getVehicle();
        List<BlockPos> entranceGroup = WarpPipeBlock.getWarpFaceGroup(level, warpPos, level.getBlockState(warpPos));
        Vec3 groupCorner = WarpPipeBlock.getEntranceGroupCorner(warpPos, entranceGroup);
        double x = groupCorner.x;
        double y = warpPos.getY();
        double z = groupCorner.z;

        if (!level.isClientSide && !entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            if (state.getBlock() instanceof ClearWarpPipeBlock && !state.getValue(WarpPipeBlock.ENTRANCE)) {
                Set<RelativeMovement> flags = EnumSet.noneOf(RelativeMovement.class);

                if (entity instanceof Player player) {
                    entity.unRide();

                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y - 1.0, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                    else entity.teleportTo(x + 0.5, y - 1.0, z + 0.5);

                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));

                    if (vehicle != null) {
                        if (vehicle instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y - 1.0, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else vehicle.teleportTo(x + 0.5, y - 1.0, z + 0.5);
                        vehicle.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        entity.setData(DataAttachmentRegistry.VEHICLE_UUID, vehicle.getUUID());
                        entity.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);
                    }
                } else {

                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y - 1.0, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                    else entity.teleportTo(x + 0.5, y - 1.0, z + 0.5);

                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (passengerEntity instanceof Player player) {
                        entity.unRide();

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y - 1.0, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else entity.teleportTo(x + 0.5, y - 1.0, z + 0.5);

                        player.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        player.setData(DataAttachmentRegistry.VEHICLE_UUID, entity.getUUID());
                        player.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);

                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    }
                }
            }

            if (level.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.UP && state.getValue(WarpPipeBlock.ENTRANCE)) {
                Set<RelativeMovement> flags = EnumSet.noneOf(RelativeMovement.class);

                if (entity instanceof Player player) {
                    entity.unRide();

                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y + 1.0, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                    else entity.teleportTo(x + 0.5, y + 1.0, z + 0.5);

                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));

                    if (vehicle != null) {
                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y + 1.0, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else vehicle.teleportTo(x + 0.5, y + 1.0, z + 0.5);
                        vehicle.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        entity.setData(DataAttachmentRegistry.VEHICLE_UUID, vehicle.getUUID());
                        entity.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);
                    }
                } else {

                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y + 1.0, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                    else entity.teleportTo(x + 0.5, y + 1.0, z + 0.5);

                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (passengerEntity instanceof Player player) {
                        entity.unRide();

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y + 1.0, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else entity.teleportTo(x + 0.5, y + 1.0, z + 0.5);

                        player.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        player.setData(DataAttachmentRegistry.VEHICLE_UUID, entity.getUUID());
                        player.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);

                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    }
                }
            }
            if (level.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.DOWN && state.getValue(WarpPipeBlock.ENTRANCE)) {
                Set<RelativeMovement> flags = EnumSet.noneOf(RelativeMovement.class);

                if (entity instanceof Player player) {
                    entity.unRide();

                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y - entity.getBbHeight(), z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                    else entity.teleportTo(x + 0.5, y - entity.getBbHeight(), z + 0.5);

                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));

                    if (vehicle != null) {
                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y - entity.getBbHeight(), z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else vehicle.teleportTo(x + 0.5, y - entity.getBbHeight(), z + 0.5);

                        vehicle.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        entity.setData(DataAttachmentRegistry.VEHICLE_UUID, vehicle.getUUID());
                        entity.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);
                    }
                } else {
                    entity.teleportTo(x + 0.5, y - entity.getBbHeight(), z + 0.5);
                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (passengerEntity instanceof Player player) {
                        entity.unRide();

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y - entity.getBbHeight(), z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else entity.teleportTo(x + 0.5, y - entity.getBbHeight(), z + 0.5);

                        player.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        player.setData(DataAttachmentRegistry.VEHICLE_UUID, entity.getUUID());
                        player.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);

                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    }
                }
            }
            if (level.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.NORTH && state.getValue(WarpPipeBlock.ENTRANCE)) {
                Set<RelativeMovement> flags = EnumSet.noneOf(RelativeMovement.class);

                if (entity instanceof Player player) {
                    entity.unRide();
                    entity.setYRot(warpYaw);
                    entity.setYHeadRot(warpYaw);
                    entity.setYBodyRot(warpYaw);

                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y, z - entity.getBbWidth(), flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                    else entity.teleportTo(x + 0.5, y, z - entity.getBbWidth());

                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));

                    if (vehicle != null) {
                        vehicle.setYRot(warpYaw);
                        vehicle.setYHeadRot(warpYaw);
                        vehicle.setYBodyRot(warpYaw);

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y, z - entity.getBbWidth(), flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else vehicle.teleportTo(x + 0.5, y, z - entity.getBbWidth());

                        vehicle.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        entity.setData(DataAttachmentRegistry.VEHICLE_UUID, vehicle.getUUID());
                        entity.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);
                    }
                } else {
                    entity.setYRot(warpYaw);
                    entity.setYHeadRot(warpYaw);
                    entity.setYBodyRot(warpYaw);
                    entity.teleportTo(x + 0.5, y, z - entity.getBbWidth());
                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (passengerEntity instanceof Player player) {
                        entity.unRide();
                        player.setYRot(warpYaw);
                        player.setYHeadRot(warpYaw);
                        player.setYBodyRot(warpYaw);

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y, z - entity.getBbWidth(), flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else entity.teleportTo(x + 0.5, y, z - entity.getBbWidth());

                        player.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        player.setData(DataAttachmentRegistry.VEHICLE_UUID, entity.getUUID());
                        player.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);

                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    }
                }
            }
            if (level.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.SOUTH && state.getValue(WarpPipeBlock.ENTRANCE)) {
                Set<RelativeMovement> flags = EnumSet.noneOf(RelativeMovement.class);

                if (entity instanceof Player player) {
                    entity.unRide();
                    entity.setYRot(warpYaw);
                    entity.setYHeadRot(warpYaw);
                    entity.setYBodyRot(warpYaw);

                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y, z + entity.getBbWidth() + 1.0, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                    else entity.teleportTo(x + 0.5, y, z + entity.getBbWidth() + 1.0);

                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));

                    if (vehicle != null) {
                        vehicle.setYRot(warpYaw);
                        vehicle.setYHeadRot(warpYaw);
                        vehicle.setYBodyRot(warpYaw);

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y, z + entity.getBbWidth() + 1.0, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else vehicle.teleportTo(x + 0.5, y, z + entity.getBbWidth() + 1.0);

                        vehicle.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        entity.setData(DataAttachmentRegistry.VEHICLE_UUID, vehicle.getUUID());
                        entity.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);
                    }
                } else {
                    entity.setYRot(warpYaw);
                    entity.setYHeadRot(warpYaw);
                    entity.setYBodyRot(warpYaw);
                    entity.teleportTo(x + 0.5, y, z + entity.getBbWidth() + 1.0);
                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (passengerEntity instanceof Player player) {
                        entity.unRide();
                        player.setYRot(warpYaw);
                        player.setYHeadRot(warpYaw);
                        player.setYBodyRot(warpYaw);

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + 0.5, y, z + entity.getBbWidth() + 1.0, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else entity.teleportTo(x + 0.5, y, z + entity.getBbWidth() + 1.0);

                        player.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        player.setData(DataAttachmentRegistry.VEHICLE_UUID, entity.getUUID());
                        player.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);

                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    }
                }
            }
            if (level.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.EAST && state.getValue(WarpPipeBlock.ENTRANCE)) {
                Set<RelativeMovement> flags = EnumSet.noneOf(RelativeMovement.class);

                if (entity instanceof Player player) {
                    entity.unRide();
                    entity.setYRot(warpYaw);
                    entity.setYHeadRot(warpYaw);
                    entity.setYBodyRot(warpYaw);

                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.teleportTo((ServerLevel) level, x + entity.getBbWidth() + 1.0, y, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                    else entity.teleportTo(x + entity.getBbWidth() + 1.0, y, z + 0.5);

                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));

                    if (vehicle != null) {
                        vehicle.setYRot(warpYaw);
                        vehicle.setYHeadRot(warpYaw);
                        vehicle.setYBodyRot(warpYaw);

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + entity.getBbWidth() + 1.0, y, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else vehicle.teleportTo(x + entity.getBbWidth() + 1.0, y, z + 0.5);

                        vehicle.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        entity.setData(DataAttachmentRegistry.VEHICLE_UUID, vehicle.getUUID());
                        entity.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);
                    }
                } else {
                    entity.setYRot(warpYaw);
                    entity.setYHeadRot(warpYaw);
                    entity.setYBodyRot(warpYaw);
                    entity.teleportTo(x + entity.getBbWidth() + 1.0, y, z + 0.5);
                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (passengerEntity instanceof Player player) {
                        entity.unRide();
                        player.setYRot(warpYaw);
                        player.setYHeadRot(warpYaw);
                        player.setYBodyRot(warpYaw);

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x + entity.getBbWidth() + 1.0, y, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else entity.teleportTo(x + entity.getBbWidth() + 1.0, y, z + 0.5);

                        player.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        player.setData(DataAttachmentRegistry.VEHICLE_UUID, entity.getUUID());
                        player.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);

                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    }
                }
            }
            if (level.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.WEST && state.getValue(WarpPipeBlock.ENTRANCE)) {
                Set<RelativeMovement> flags = EnumSet.noneOf(RelativeMovement.class);

                if (entity instanceof Player player) {
                    entity.unRide();
                    entity.setYRot(warpYaw);
                    entity.setYHeadRot(warpYaw);
                    entity.setYBodyRot(warpYaw);

                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.teleportTo((ServerLevel) level, x - entity.getBbWidth(), y, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                    else entity.teleportTo(x - entity.getBbWidth(), y, z + 0.5);

                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));

                    if (vehicle != null) {
                        vehicle.setYRot(warpYaw);
                        vehicle.setYHeadRot(warpYaw);
                        vehicle.setYBodyRot(warpYaw);

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x - entity.getBbWidth(), y, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else vehicle.teleportTo(x - entity.getBbWidth(), y, z + 0.5);

                        vehicle.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        entity.setData(DataAttachmentRegistry.VEHICLE_UUID, vehicle.getUUID());
                        entity.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);
                    }
                } else {
                    entity.setYRot(warpYaw);
                    entity.setYHeadRot(warpYaw);
                    entity.setYBodyRot(warpYaw);
                    entity.teleportTo(x - entity.getBbWidth(), y, z + 0.5);
                    entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());

                    if (passengerEntity instanceof Player player) {
                        entity.unRide();
                        player.setYRot(warpYaw);
                        player.setYHeadRot(warpYaw);
                        player.setYBodyRot(warpYaw);

                        if (entity instanceof ServerPlayer serverPlayer)
                            serverPlayer.teleportTo((ServerLevel) level, x - entity.getBbWidth(), y, z + 0.5, flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                        else entity.teleportTo(x - entity.getBbWidth(), y, z + 0.5);

                        player.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                        player.setData(DataAttachmentRegistry.VEHICLE_UUID, entity.getUUID());
                        player.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, 10);

                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    }
                }
            }
        }
        markEntityTeleported(entity);
        level.gameEvent(GameEvent.TELEPORT, warpPos, GameEvent.Context.of(entity));
        level.playSound(null, warpPos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS, 1.0F, pitch);
    }

    public void sendData() {
        if (level instanceof ServerLevel serverWorld)
            serverWorld.getChunkSource().blockChanged(getBlockPos());
    }

    public void spawnFromWarpPipe(Level world, BlockPos pos, ItemStack stack) {
        BlockPos spawnPos;
        Direction facing = world.getBlockState(pos).getOptionalValue(BlockStateProperties.FACING).orElse(Direction.UP);
        if (world.getBlockState(pos).hasProperty(BlockStateProperties.FACING))
            spawnPos = pos.relative(facing);
        else spawnPos = pos.above();

        if (world instanceof ServerLevel serverWorld)
            this.spawnItemEntity(world, stack, serverWorld, spawnPos);
    }

    private void spawnItemEntity(Level world, ItemStack stack, ServerLevel serverWorld, BlockPos spawnPos) {
         if (stack.getItem() instanceof LargeSnowballItem) {
            LargeSnowballProjectile snowball = new LargeSnowballProjectile(serverWorld, spawnPos.getX() + 0.5D,
                    spawnPos.getY(), spawnPos.getZ() + 0.5D);
             int nearbyEntities = serverWorld.getEntities(snowball, new AABB(spawnPos).inflate(1), EntitySelector.NO_SPECTATORS).size();

            if (!snowball.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN) && nearbyEntities < pipeSpawner.maxNearbyEntities) {
                snowball.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(snowball);
                stack.copyWithCount(1);
            } else if (nearbyEntities < pipeSpawner.maxNearbyEntities) this.spawnItem(world, spawnPos, stack);
        } else if (stack.getItem() instanceof ArmorStandItem) {
            Consumer<ArmorStand> consumer = EntityType.createDefaultStackConfig(serverWorld, stack, null);
            ArmorStand armorStand = EntityType.ARMOR_STAND.create(serverWorld, consumer, spawnPos, MobSpawnType.SPAWN_EGG, true, true);
             int nearbyEntities = serverWorld.getEntities(armorStand, new AABB(spawnPos).inflate(1), EntitySelector.NO_SPECTATORS).size();

            if (armorStand != null && !armorStand.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)
                    && nearbyEntities < pipeSpawner.maxNearbyEntities) {
                if (!world.getEntitiesOfClass(ArmorStand.class, new AABB(spawnPos)).isEmpty())
                    return;
                armorStand.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(armorStand);
                stack.copyWithCount(1);
            } else if (nearbyEntities < pipeSpawner.maxNearbyEntities) this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof MinecartItem cart) {
            AbstractMinecart abstractMinecart =
                    AbstractMinecart.createMinecart(serverWorld, spawnPos.getX() + 0.5D, spawnPos.getY() + 1.0D,
                            spawnPos.getZ() + 0.5D, cart.type, stack, null);
             int nearbyEntities = serverWorld.getEntities(abstractMinecart, new AABB(spawnPos).inflate(1), EntitySelector.NO_SPECTATORS).size();

            if (!abstractMinecart.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN) && nearbyEntities < pipeSpawner.maxNearbyEntities) {
                if (!world.getEntitiesOfClass(AbstractMinecart.class, new AABB(spawnPos)).isEmpty())
                    return;
                abstractMinecart.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(abstractMinecart);
                stack.copyWithCount(1);
            } else if (nearbyEntities < pipeSpawner.maxNearbyEntities) this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof BoatItem boatItem) {
            Boat boat = boatItem.hasChest ? new ChestBoat(serverWorld, spawnPos.getX() + 0.5D, spawnPos.getY() + 1.0D, spawnPos.getZ() + 0.5D)
                    : new Boat(serverWorld, spawnPos.getX() + 0.5D, spawnPos.getY() + 1.0D, spawnPos.getZ() + 0.5D);
             int nearbyEntities = serverWorld.getEntities(boat, new AABB(spawnPos).inflate(1), EntitySelector.NO_SPECTATORS).size();

            if (!boat.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN) && nearbyEntities < pipeSpawner.maxNearbyEntities) {
                if (!world.getEntitiesOfClass(Boat.class, new AABB(spawnPos)).isEmpty())
                    return;
                boat.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                boat.setVariant(boatItem.type);
                world.addFreshEntity(boat);
                stack.copyWithCount(1);
            } else if (nearbyEntities < pipeSpawner.maxNearbyEntities) this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock) {
            PrimedTnt primedtnt = new PrimedTnt(serverWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, null);
             int nearbyEntities = serverWorld.getEntities(primedtnt, new AABB(spawnPos).inflate(1), EntitySelector.NO_SPECTATORS).size();

            if (!primedtnt.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN) && nearbyEntities < pipeSpawner.maxNearbyEntities) {
                primedtnt.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(primedtnt);
                stack.copyWithCount(1);
                serverWorld.gameEvent(null, GameEvent.PRIME_FUSE, spawnPos);
            } else if (nearbyEntities < pipeSpawner.maxNearbyEntities) this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof WindChargeItem) {
            WindCharge windCharge = new WindCharge(serverWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                    new Vec3(0, -1.0, 0));

            if (!windCharge.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                windCharge.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(windCharge);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof FireChargeItem) {
            SmallFireball fireball = new SmallFireball(serverWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                    new Vec3(0, -0.5, 0));

            if (!fireball.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                fireball.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(fireball);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof ThrowablePotionItem) {
            ThrownPotion potion = new ThrownPotion(serverWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

            if (!potion.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                potion.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                potion.setItem(stack);
                world.addFreshEntity(potion);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof ExperienceBottleItem) {
            ThrownExperienceBottle xpBottle = new ThrownExperienceBottle(serverWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

            if (!xpBottle.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                xpBottle.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                xpBottle.setItem(stack);
                world.addFreshEntity(xpBottle);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof EndCrystalItem) {
            EndCrystal endCrystal = new EndCrystal(serverWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
             int nearbyEntities = serverWorld.getEntities(endCrystal, new AABB(spawnPos).inflate(1), EntitySelector.NO_SPECTATORS).size();

            if (!endCrystal.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN) && nearbyEntities < pipeSpawner.maxNearbyEntities) {
                endCrystal.setPos(spawnPos.getX() + 0.5D, spawnPos.getY() - endCrystal.getBbHeight(), spawnPos.getZ() + 0.5D);
                endCrystal.setDeltaMovement(new Vec3(0, -1.0, 0));
                endCrystal.setShowBottom(false);
                world.addFreshEntity(endCrystal);
                world.gameEvent(null, GameEvent.ENTITY_PLACE, spawnPos);
                stack.copyWithCount(1);
            } else if (nearbyEntities < pipeSpawner.maxNearbyEntities) this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof FireworkRocketItem) {
            FireworkRocketEntity firework = new FireworkRocketEntity(serverWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, stack);

            if (!firework.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                firework.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(firework);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() instanceof EggItem) {
            ThrownEgg egg = new ThrownEgg(serverWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

            if (!egg.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                egg.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                egg.setItem(stack);
                world.addFreshEntity(egg);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() == CompatRegistry.HAT_STAND_ITEM.get()) {
            Entity entity = CompatRegistry.HAT_STAND.get().create(serverWorld);
             int nearbyEntities = serverWorld.getEntities(entity, new AABB(spawnPos).inflate(1), EntitySelector.NO_SPECTATORS).size();

            if (entity != null && !entity.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN) && nearbyEntities < pipeSpawner.maxNearbyEntities) {
                entity.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(entity);
                stack.copyWithCount(1);
            } else if (nearbyEntities < pipeSpawner.maxNearbyEntities) this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() == CompatRegistry.CANNONBALL_ITEM.get()) {
            Entity entity = CompatRegistry.CANNONBALL.get().create(serverWorld);

            if (entity != null && !entity.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                entity.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(
                        world.random.triangle(0.0, 0.3),
                        world.random.triangle(0.5, 0.3),
                        world.random.triangle(0.0, 0.3)));
                world.addFreshEntity(entity);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() == CompatRegistry.BOMB_ITEM.get()) {
            Entity entity = CompatRegistry.BOMB.get().create(serverWorld);

            if (entity != null && !entity.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                entity.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(
                        world.random.triangle(0.0, 0.2),
                        world.random.triangle(0.5, 0.2),
                        world.random.triangle(0.0, 0.2)));
                world.addFreshEntity(entity);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() == CompatRegistry.BOMB_BLUE_ITEM.get()) {
            Entity entity = CompatRegistry.BOMB.get().create(serverWorld);

            if (entity != null && !entity.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                CompoundTag nbt = new CompoundTag();
                entity.save(nbt);
                nbt.putInt("Type", 1);
                entity.load(nbt);

                entity.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(0, -0.5, 0));
                world.addFreshEntity(entity);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() == CompatRegistry.BOMB_SPIKY_ITEM.get()) {
            Entity entity = CompatRegistry.BOMB.get().create(serverWorld);

            if (entity != null && !entity.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                CompoundTag nbt = new CompoundTag();
                entity.save(nbt);
                nbt.putInt("Type", 2);
                entity.load(nbt);

                entity.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(0, -0.5, 0));
                world.addFreshEntity(entity);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else if (stack.getItem() == CompatRegistry.CONFETTI_POPPER_ITEM.get() && world.getDifficulty() != Difficulty.PEACEFUL) {
            Creeper entity = EntityType.CREEPER.create(serverWorld);

            if (entity != null) {
                CompoundTag nbt = new CompoundTag();
                entity.save(nbt);
                nbt.putBoolean("Party", true);
                nbt.putInt("Fuse", 0);

                entity.setNoAi(true);
                entity.ignite();
                entity.setInvisible(true);
                entity.setSilent(true);
                entity.load(nbt);

                entity.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(entity);
            }
            world.gameEvent(null, GameEvent.EXPLODE, spawnPos);
        } else if (stack.getItem() == CompatRegistry.ICE_BOMB_ITEM.get()) {
            Entity entity = CompatRegistry.ICE_BOMB.get().create(serverWorld);

            if (entity != null && !entity.getType().is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)) {
                entity.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                world.addFreshEntity(entity);
                stack.copyWithCount(1);
            } else this.spawnItem(world, spawnPos, stack);

        } else this.spawnItem(world, spawnPos, stack);
    }

    public void spawnItem(Level world, BlockPos pos, ItemStack stack) {
        Direction facing = world.getBlockState(pos).getOptionalValue(BlockStateProperties.FACING).orElse(Direction.UP);
        double x;
        double y;
        double z;
        double entityWidth = 0.25 / 2.0;
        double entityHeight = 0.25;
        BlockPos spawnPos = pos.relative(facing);

        switch (facing) {
            default:
                x = spawnPos.getX() + 0.5;
                y = spawnPos.getY() - 1.0;
                z = spawnPos.getZ() + 0.5;
                break;
            case DOWN:
                x = spawnPos.getX() + 0.5;
                y = spawnPos.getY() - 1.0 - entityHeight - 0.1;
                z = spawnPos.getZ() + 0.5;
                break;
            case NORTH:
                x = spawnPos.getX() + 0.5;
                y = spawnPos.getY() - 1.0;
                z = spawnPos.getZ() - entityWidth - 0.1;
                break;
            case SOUTH:
                x = spawnPos.getX() + 0.5;
                y = spawnPos.getY() - 1.0;
                z = spawnPos.getZ() + entityWidth + 0.1;
                break;
            case WEST:
                x = spawnPos.getX() - entityWidth - 0.1;
                y = spawnPos.getY() - 1.0;
                z = spawnPos.getZ() + 0.5;
                break;
            case EAST:
                x = spawnPos.getX() + entityWidth + 0.1;
                y = spawnPos.getY() - 1.0;
                z = spawnPos.getZ() + 0.5;
                break;
        }

        double baseSpeed  = 0.2;
        Vec3 velocity = switch (facing) {
            case NORTH -> new Vec3(
                    world.random.triangle(0.0, 0.2),
                    world.random.triangle(0.5, 0.2),
                    -baseSpeed
            );
            case SOUTH -> new Vec3(
                    world.random.triangle(0.0, 0.2),
                    world.random.triangle(0.5, 0.2),
                    baseSpeed
            );
            case EAST -> new Vec3(
                    baseSpeed,
                    world.random.triangle(0.5, 0.2),
                    world.random.triangle(0.0, 0.2)
            );
            case WEST -> new Vec3(
                    -baseSpeed,
                    world.random.triangle(0.5, 0.2),
                    world.random.triangle(0.0, 0.2)
            );
            case UP -> new Vec3(
                    world.random.triangle(0.0, 0.2),
                    baseSpeed,
                    world.random.triangle(0.0, 0.2)
            );
            case DOWN -> new Vec3(0, -baseSpeed, 0);
        };

        ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack);
        itemEntity.setDeltaMovement(velocity);
        world.addFreshEntity(itemEntity);
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }
}