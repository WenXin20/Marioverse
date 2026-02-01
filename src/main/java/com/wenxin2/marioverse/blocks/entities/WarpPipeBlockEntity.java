package com.wenxin2.marioverse.blocks.entities;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.PipeBubblesBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.WaterSpoutBlock;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
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
import com.wenxin2.marioverse.world.PipeSpawner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.item.component.ItemContainerContents;
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
import org.slf4j.Logger;

public class WarpPipeBlockEntity extends BaseWarpBlockEntity implements MenuProvider, Nameable, Spawner, ContainerSingleItem.BlockContainerSingleItem {
    // Store a map to track whether entities have teleported or not
    public static final Map<Integer, Boolean> teleportedEntities = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.warp_pipe");
    private static final int MAX_TEXT_LINE_WIDTH = 78;
    private static final int TEXT_LINE_HEIGHT = 10;

    public PipeText pipeText = this.createDefaultPipeText();
    public static final String SPOUT_HEIGHT = "SpoutHeight";
    public static final String BUBBLES_DISTANCE = "BubblesDistance";
    public static final String DISPLAY_TEXT_NORTH = "displayTextNorth";
    public static final String DISPLAY_TEXT_SOUTH = "displayTextSouth";
    public static final String DISPLAY_TEXT_EAST = "displayTextEast";
    public static final String DISPLAY_TEXT_WEST = "displayTextWest";
    public static final String DISPLAY_TEXT_ABOVE = "displayTextAbove";
    public static final String DISPLAY_TEXT_BELOW = "displayTextBelow";
    public static final String CUSTOM_NAME = "CustomName";
    public static final String PIPE_NAME = "PipeName";
    @Nullable public Component name;
    public Component pipeName;
    private LockCode lockKey = LockCode.NO_LOCK;
    private ItemStack spawnItemStack = ItemStack.EMPTY;
    public int spoutHeight = 4;
    public int bubblesDistance = 3;
    public int spawnItemDelay = 20;
    public boolean displayTextNorth;
    public boolean displayTextSouth;
    public boolean displayTextEast;
    public boolean displayTextWest;
    public boolean displayTextAbove;
    public boolean displayTextBelow;

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
        tag.putShort("SpawnItemDelay", (short) this.spawnItemDelay);
        tag.putInt(BUBBLES_DISTANCE, this.bubblesDistance);
        tag.putInt(SPOUT_HEIGHT, this.spoutHeight);
        tag.putBoolean(DISPLAY_TEXT_NORTH, this.displayTextNorth);
        tag.putBoolean(DISPLAY_TEXT_SOUTH, this.displayTextSouth);
        tag.putBoolean(DISPLAY_TEXT_EAST, this.displayTextEast);
        tag.putBoolean(DISPLAY_TEXT_WEST, this.displayTextWest);
        tag.putBoolean(DISPLAY_TEXT_ABOVE, this.displayTextAbove);
        tag.putBoolean(DISPLAY_TEXT_BELOW, this.displayTextBelow);

        if (this.name != null)
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
        if (this.pipeName != null)
            tag.putString(PIPE_NAME, Component.Serializer.toJson(this.pipeName, provider));
        if (!this.spawnItemStack.isEmpty())
            tag.put("SpawnItem", this.spawnItemStack.save(provider));

        PipeText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.pipeText).resultOrPartial(LOGGER::error)
                .ifPresent(pipeName -> tag.put(PIPE_NAME, pipeName));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.lockKey = LockCode.fromTag(tag);
        this.pipeSpawner.load(this.level, this.worldPosition, tag);
        this.spawnItemDelay = tag.getShort("SpawnItemDelay");
        this.spoutHeight = tag.getInt(SPOUT_HEIGHT);
        this.bubblesDistance = tag.getInt(BUBBLES_DISTANCE);
        this.displayTextNorth = tag.getBoolean(DISPLAY_TEXT_NORTH);
        this.displayTextSouth = tag.getBoolean(DISPLAY_TEXT_SOUTH);
        this.displayTextEast = tag.getBoolean(DISPLAY_TEXT_EAST);
        this.displayTextWest = tag.getBoolean(DISPLAY_TEXT_WEST);
        this.displayTextAbove = tag.getBoolean(DISPLAY_TEXT_ABOVE);
        this.displayTextBelow = tag.getBoolean(DISPLAY_TEXT_BELOW);

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
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        if (this.level != null)
            return new WarpPipeMenu(id, inventory, ContainerLevelAccess.create(this.level, this.getBlockPos()));
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
        if (world instanceof ServerLevel serverWorld)
            warpPipeBE.pipeSpawner.serverTick(serverWorld, pos);

        if (state.hasProperty(WarpPipeBlock.CLOSED) && !state.getValue(WarpPipeBlock.CLOSED)) {
            if (warpPipeBE.spawnItemDelay > 0 && !warpPipeBE.getTheItem().isEmpty()) {
                warpPipeBE.spawnItemDelay--;
            } else if (!warpPipeBE.getTheItem().isEmpty()) {
                ItemStack stack = warpPipeBE.getTheItem().copyWithCount(1);

                warpPipeBE.spawnFromWarpPipe(world, pos, stack);
                MarioverseSoundTypes.playSounds(world, pos, stack);
                warpPipeBE.spawnItemDelay = 180;
            }
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        return this.level != null ? this.pipeSpawner.onEventTriggered(this.level, id) : super.triggerEvent(id, type);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
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

    public void closePipe(ServerPlayer player) {
        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (state.getValue(WarpPipeBlock.CLOSED)) {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.CLOSED, Boolean.FALSE), 3);
                this.playSound(this.level, menuPos, SoundRegistry.PIPE_OPENS.get(), SoundSource.BLOCKS, 1.0F, 0.15F);
            } else {
                if (this.level.getBlockState(menuPos.above()).getBlock() instanceof WaterSpoutBlock)
                    this.level.destroyBlock(menuPos.above(), false);
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.CLOSED, Boolean.TRUE), 0);
                this.playSound(this.level, menuPos, SoundRegistry.PIPE_CLOSES.get(), SoundSource.BLOCKS, 1.0F, 0.5F);
            }
        }
    }

    public void toggleWaterSpout(ServerPlayer player) {
        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (state.getValue(WarpPipeBlock.WATER_SPOUT)) {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.WATER_SPOUT, Boolean.FALSE), 3);
                this.level.scheduleTick(menuPos, state.getBlock(), 3);
                this.playSound(this.level, menuPos, SoundRegistry.WATER_SPOUT_BREAK.get(), SoundSource.BLOCKS, 1.0F, 0.15F);
            } else {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.WATER_SPOUT, Boolean.TRUE), 3);
                this.level.scheduleTick(menuPos, state.getBlock(), 3);
                this.playSound(this.level, menuPos, SoundRegistry.WATER_SPOUT_PLACE.get(), SoundSource.BLOCKS, 1.0F, 0.5F);
            }
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
        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (state.getValue(WarpPipeBlock.BUBBLES)) {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.BUBBLES, Boolean.FALSE), 3);
                this.level.scheduleTick(menuPos, state.getBlock(), 3);
                this.playSound(this.level, menuPos, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 1.0F, 0.15F);
            } else {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.BUBBLES, Boolean.TRUE), 3);
                this.level.scheduleTick(menuPos, state.getBlock(), 3);
                this.playSound(this.level, menuPos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 1.0F, 0.5F);
            }
        }
    }

    // Method to mark an entity as teleported
    public static void markEntityTeleported(Entity entity) {
        if (entity != null)
            teleportedEntities.put(entity.getId(), true);
    }

    public static void warp(Entity entity, BlockPos warpPos, Level world, BlockState state) {
        Entity passengerEntity = entity.getControllingPassenger();

        if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            if (state.getBlock() instanceof ClearWarpPipeBlock && !state.getValue(WarpPipeBlock.ENTRANCE)) {
                entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                if (entity instanceof Player player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() - 1.0, warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120); // Enchant teleport particles
                    if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 1, 0));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() - 1.0, warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (passengerEntity instanceof Player player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 1, 0));
                        entity.unRide();
                    }
                }
            }

            if (world.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.UP && state.getValue(WarpPipeBlock.ENTRANCE)) {
                entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                if (entity instanceof Player player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() + 1.0, warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() + 1.0, warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (passengerEntity instanceof Player player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.DOWN && state.getValue(WarpPipeBlock.ENTRANCE)) {
                entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                if (entity instanceof Player player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() - entity.getBbHeight(), warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() - entity.getBbHeight(), warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (passengerEntity instanceof Player player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.NORTH && state.getValue(WarpPipeBlock.ENTRANCE)) {
                entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                if (entity instanceof Player player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() - entity.getBbWidth());
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() - entity.getBbWidth());
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (passengerEntity instanceof Player player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.SOUTH && state.getValue(WarpPipeBlock.ENTRANCE)) {
                entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                if (entity instanceof Player player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + entity.getBbWidth() + 1.0);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + entity.getBbWidth() + 1.0);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (passengerEntity instanceof Player player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.EAST && state.getValue(WarpPipeBlock.ENTRANCE)) {
                entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                if (entity instanceof Player player) {
                    entity.teleportTo(warpPos.getX() + entity.getBbWidth() + 1.0, warpPos.getY(), warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + entity.getBbWidth() + 1.0, warpPos.getY(), warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (passengerEntity instanceof Player player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(DirectionalBlock.FACING) == Direction.WEST && state.getValue(WarpPipeBlock.ENTRANCE)) {
                entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                if (entity instanceof Player player) {
                    entity.teleportTo(warpPos.getX() - entity.getBbWidth(), warpPos.getY(), warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() - entity.getBbWidth(), warpPos.getY(), warpPos.getZ() + 0.5);
                    world.broadcastEntityEvent(entity, (byte) 120);
                    if (passengerEntity instanceof Player player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
        }
        markEntityTeleported(entity);
        world.gameEvent(GameEvent.TELEPORT, warpPos, GameEvent.Context.of(entity));
        world.playSound(null, warpPos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
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
}