package com.wenxin2.marioverse.blocks.entities;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.wenxin2.marioverse.blocks.PipeBubblesBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.WaterSpoutBlock;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import java.util.UUID;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class WarpPipeBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.warp_pipe");
    private static final int MAX_TEXT_LINE_WIDTH = 78;
    private static final int TEXT_LINE_HEIGHT = 10;

    public PipeText pipeName = this.createDefaultPipeText();
    public static final String WARP_POS = "WarpPos";
    public static final String WARP_DIMENSION = "Dimension";
    public static final String WARP_UUID = "WarpUUID";
    public static final String UUID = "UUID";
    public static final String SPOUT_HEIGHT = "SpoutHeight";
    public static final String BUBBLES_DISTANCE = "BubblesDistance";
    public static final String PREVENT_WARP = "PreventWarp";
    public static final String IS_WAXED = "IsWaxed";
    public static final String DISPLAY_TEXT_NORTH = "displayTextNorth";
    public static final String DISPLAY_TEXT_SOUTH = "displayTextSouth";
    public static final String DISPLAY_TEXT_EAST = "displayTextEast";
    public static final String DISPLAY_TEXT_WEST = "displayTextWest";
    public static final String DISPLAY_TEXT_ABOVE = "displayTextAbove";
    public static final String DISPLAY_TEXT_BELOW = "displayTextBelow";
    public static final String CUSTOM_NAME = "CustomName";
    public static final String PIPE_NAME = "PipeName";
    @Nullable
    public Component name;
    private LockCode lockKey = LockCode.NO_LOCK;
    @Nullable
    public BlockPos destinationPos;
    public String dimensionTag;
    public int spoutHeight = 4;
    public int bubblesDistance = 3;
    public boolean preventWarp = Boolean.FALSE;
    public boolean isWaxed;
    public boolean displayTextNorth;
    public boolean displayTextSouth;
    public boolean displayTextEast;
    public boolean displayTextWest;
    public boolean displayTextAbove;
    public boolean displayTextBelow;
    public UUID uuid;
    public UUID warpUuid;

    public WarpPipeBlockEntity(final BlockPos pos, final BlockState state)
    {
        this(BlockEntityRegistry.WARP_PIPE_BLOCK_ENTITY.get(), pos, state);
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
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WarpPipeMenu(id, inventory, ContainerLevelAccess.create(this.level, this.getBlockPos()));
    }

    public void setCustomName(Component name) {
        this.name = name;
        this.markUpdated();
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
        return !this.pipeName.getMessage(0, false).contains(Component.empty())
                ? this.pipeName.getMessage(0, false) : this.name != null ? this.name : DEFAULT_NAME;
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
        return this.pipeName;
    }

    public Component getPipeNameComponent() {
        return !this.pipeName.getMessage(0, false).contains(Component.empty())
                ? this.pipeName.getMessage(0, false) : this.name != null ? this.name : DEFAULT_NAME;
    }

    public boolean updateText(UnaryOperator<PipeText> text) {
        PipeText pipeText = this.getPipeText();
        this.markUpdated();
        this.getUpdatePacket();
        return this.setText(text.apply(pipeText));
    }

    public boolean setText(PipeText text) {
        if (text != this.pipeName) {
            this.pipeName = text;
            this.markUpdated();
            this.getUpdatePacket();
            return true;
        } else return false;
    }

    public boolean isWaxed() {
        return this.isWaxed;
    }

    public void setWaxed(boolean isWaxed) {
        if (this.isWaxed != isWaxed) {
            this.isWaxed = isWaxed;
            this.markUpdated();
            this.getUpdatePacket();
        }
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

    public boolean hasDestinationPos() {
        return this.destinationPos != null;
    }

    public void setDestinationPos(@Nullable BlockPos pos) {
        this.destinationPos = pos;
        this.setChanged();
        if (this.level != null && pos != null) {
            BlockState state = this.getBlockState();
            this.level.setBlock(this.getBlockPos(), state, 4);
        }
    }

    @Nullable
    public BlockPos getDestinationPos() {
        if (this.destinationPos != null) {
            return this.destinationPos;
        }
        return null;
    }

    @Nullable
    public ResourceKey<Level> getDestinationDim() {
        if (dimensionTag != null) {
            ResourceLocation location = ResourceLocation.tryParse(dimensionTag);
            if (location != null) {
                return ResourceKey.create(Registries.DIMENSION, location);
            }
        }
        return null;
    }


    public void setDestinationDim(@Nullable ResourceKey<Level> dimension) {
        if (dimension != null) {
            this.dimensionTag = dimension.location().toString();
        }

        if (this.level != null) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState(), 4);
        }
        this.setChanged();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setPreventWarp(boolean preventWarp) {
        this.preventWarp = preventWarp;
    }

    public UUID getWarpUuid() {
        return this.warpUuid;
    }

    public void setWarpUuid(UUID uuid) {
        this.warpUuid = uuid;
        this.setChanged();
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

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.lockKey = LockCode.fromTag(tag);
        this.spoutHeight = tag.getInt(SPOUT_HEIGHT);
        this.bubblesDistance = tag.getInt(BUBBLES_DISTANCE);
        this.isWaxed = tag.getBoolean(IS_WAXED);
        this.displayTextNorth = tag.getBoolean(DISPLAY_TEXT_NORTH);
        this.displayTextSouth = tag.getBoolean(DISPLAY_TEXT_SOUTH);
        this.displayTextEast = tag.getBoolean(DISPLAY_TEXT_EAST);
        this.displayTextWest = tag.getBoolean(DISPLAY_TEXT_WEST);
        this.displayTextAbove = tag.getBoolean(DISPLAY_TEXT_ABOVE);
        this.displayTextBelow = tag.getBoolean(DISPLAY_TEXT_BELOW);

        if (tag.contains(CUSTOM_NAME, 8)) {
            this.name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME), provider);
        }

        if (tag.contains(PIPE_NAME)) {
            PipeText.DIRECT_CODEC.parse(NbtOps.INSTANCE, tag.getCompound(PIPE_NAME)).resultOrPartial(LOGGER::error).ifPresent(text -> {
                this.pipeName = this.loadLines(text);
            });
        }

        if (tag.contains(WARP_POS)) {
            this.destinationPos = NbtUtils.readBlockPos(tag, WARP_POS).orElse(null);
            this.setDestinationPos(this.destinationPos);
            System.out.println("Loaded: " + NbtUtils.readBlockPos(tag, WARP_POS).orElse(null));
        }

        if (tag.contains(WARP_DIMENSION))
            this.dimensionTag = tag.getString(WARP_DIMENSION);

        if (tag.contains(PREVENT_WARP))
            this.preventWarp = tag.getBoolean(PREVENT_WARP);

        if (tag.contains(UUID))
            this.uuid = tag.getUUID(UUID);

        if (tag.contains(WARP_UUID))
            this.warpUuid = tag.getUUID(WARP_UUID);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.lockKey.addToTag(tag);
        tag.putInt(BUBBLES_DISTANCE, this.bubblesDistance);
        tag.putInt(SPOUT_HEIGHT, this.spoutHeight);
        tag.putBoolean(PREVENT_WARP, this.preventWarp);
        tag.putBoolean(IS_WAXED, this.isWaxed);
        tag.putBoolean(DISPLAY_TEXT_NORTH, this.displayTextNorth);
        tag.putBoolean(DISPLAY_TEXT_SOUTH, this.displayTextSouth);
        tag.putBoolean(DISPLAY_TEXT_EAST, this.displayTextEast);
        tag.putBoolean(DISPLAY_TEXT_WEST, this.displayTextWest);
        tag.putBoolean(DISPLAY_TEXT_ABOVE, this.displayTextAbove);
        tag.putBoolean(DISPLAY_TEXT_BELOW, this.displayTextBelow);

        if (this.name != null) {
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
        }

        PipeText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.pipeName).resultOrPartial(LOGGER::error).ifPresent(pipeName -> {
            tag.put(PIPE_NAME, pipeName);
        });

        if (this.hasDestinationPos() && this.destinationPos != null) {
            tag.put(WARP_POS, NbtUtils.writeBlockPos(this.destinationPos));
            System.out.println("Saved: " + NbtUtils.writeBlockPos(this.destinationPos));
        }

        if (this.dimensionTag != null)
            tag.putString(WARP_DIMENSION, this.dimensionTag);

        if (this.uuid != null)
            tag.putUUID(UUID, this.getUuid());

        if (this.warpUuid != null)
            tag.putUUID(WARP_UUID, this.getWarpUuid());
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);

        this.saveAdditional(tag, provider);
        return tag;
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

    public void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    public void sendData() {
        if (level instanceof ServerLevel serverWorld)
            serverWorld.getChunkSource().blockChanged(getBlockPos());
    }
}
