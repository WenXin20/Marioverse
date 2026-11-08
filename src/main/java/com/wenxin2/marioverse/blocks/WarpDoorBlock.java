package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.utils.BlockWarpPlayerHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpDoorBlock extends DoorBlock implements EntityBlock, BlockWarpPlayerHandler {
    private final DoorBlock source;

    public WarpDoorBlock(DoorBlock source) {
        super(source.type(), BlockBehaviour.Properties.ofFullCopy(source));
        this.source = source;
    }

    public DoorBlock source() {
        return this.source;
    }

    @NotNull
    @Override
    public MutableComponent getName() {
        return Component.translatable("block.marioverse.warp_door", Component.translatable(this.source.getDescriptionId()));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpDoorBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity blockEntityAbove = world.getBlockEntity(pos.above());
        BlockEntity blockEntityBelow = world.getBlockEntity(pos.below());
        UUID uuid = UUID.randomUUID();

        if (blockEntity instanceof WarpDoorBlockEntity doorBE) {
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos, 16);
            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);

            if (data != null && data.copyTag().hasUUID("UUID")) {
                doorBE.setUUID(data.copyTag().getUUID("UUID"));
                doorBE.setChanged();

                if (blockEntityAbove instanceof WarpDoorBlockEntity doorBEAbove) {
                    if (world instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos.above(), 16);
                    doorBEAbove.setUUID(data.copyTag().getUUID("UUID"));
                    doorBEAbove.setChanged();
                } else if (blockEntityBelow instanceof WarpDoorBlockEntity doorBEBelow) {
                    if (world instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos.below(), 16);
                    doorBEBelow.setUUID(data.copyTag().getUUID("UUID"));
                    doorBEBelow.setChanged();
                }
            } else {
                doorBE.setUUID(uuid);
                doorBE.setChanged();

                if (blockEntityAbove instanceof WarpDoorBlockEntity doorBEAbove) {
                    if (world instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos.above(), 16);
                    doorBEAbove.setUUID(uuid);
                    doorBEAbove.setChanged();
                } else if (blockEntityBelow instanceof WarpDoorBlockEntity doorBEBelow) {
                    if (world instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos.below(), 16);
                    doorBEBelow.setUUID(uuid);
                    doorBEBelow.setChanged();
                }
            }
            doorBE.onLoad();
        }
        super.setPlacedBy(world, pos, state, entity, stack);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (WarpDoorBlock.canEnterWarp(state, level, pos, entity))
            this.enterWarp(entity, level, pos, pos, state, null);
        super.entityInside(state, level, pos, entity);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        WarpDoorBlock.closeAfterWarp(state, level, pos);
    }

    @Override
    public boolean mv$getBlockWarpTeleportConfig(Entity entity) {
        return WarpDoorBlock.canTeleport(entity);
    }

    public static boolean isDoor(BlockState state) {
        return state.getBlock() instanceof DoorBlock || state.getBlock() instanceof MultiPartWarpDoor;
    }

    public static List<BlockPos> getParts(BlockState state, BlockPos pos) {
        if (state.getBlock() instanceof MultiPartWarpDoor door)
            return door.mv$getParts(state, pos);
        if (state.getBlock() instanceof DoorBlock)
            return state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? List.of(pos, pos.above()) : List.of(pos.below(), pos);
        return List.of(pos);
    }

    public static BlockPos getLowerPart(BlockState state, BlockPos pos) {
        return WarpDoorBlock.getParts(state, pos).getFirst();
    }

    public static boolean isLowerPart(BlockState state, BlockPos pos) {
        return WarpDoorBlock.getLowerPart(state, pos).equals(pos);
    }

    public static boolean isDoorOpen(BlockState state) {
        if (state.getBlock() instanceof MultiPartWarpDoor door)
            return door.mv$isOpen(state);
        return state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN);
    }

    public static BlockState setDoorOpen(BlockState state, boolean open) {
        if (state.getBlock() instanceof MultiPartWarpDoor door)
            return door.mv$setOpen(state, open);
        return state.getBlock() instanceof DoorBlock ? state.setValue(DoorBlock.OPEN, open) : state;
    }

    public static Direction getFacing(BlockState state) {
        if (state.getBlock() instanceof MultiPartWarpDoor door)
            return door.mv$getFacing(state);
        return state.getValue(DoorBlock.FACING);
    }

    public static BlockSetType getType(BlockState state) {
        if (state.getBlock() instanceof MultiPartWarpDoor door)
            return door.mv$getType();
        return state.getBlock() instanceof DoorBlock doorBlock ? doorBlock.type() : BlockSetType.OAK;
    }

    public static String getFamily(Block block) {
        return block instanceof MultiPartWarpDoor door ? door.mv$getFamily() : "";
    }

    public static Map<String, String> getDropProperties(Block block) {
        return block instanceof MultiPartWarpDoor door ? door.mv$getDropProperties() : Map.of("half", "lower");
    }

    public static void assignUUIDs(Level level, BlockPos pos, BlockState state, ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        UUID uuid = data != null && data.copyTag().hasUUID("UUID") ? data.copyTag().getUUID("UUID") : UUID.randomUUID();

        for (BlockPos part : WarpDoorBlock.getParts(state, pos)) {
            if (!(level.getBlockEntity(part) instanceof WarpDoorBlockEntity doorBE))
                continue;

            if (level instanceof ServerLevel serverLevel)
                ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverLevel, null, part, 16);
            doorBE.setUUID(uuid);
            doorBE.setChanged();
            doorBE.onLoad();
        }
    }

    public static boolean canEnterWarp(BlockState state, Level level, BlockPos pos, Entity entity) {
        return !ConfigRegistry.DISABLE_WARP_DOORS.get()
                && level.getBlockEntity(pos) instanceof WarpDoorBlockEntity
                && WarpDoorBlock.isDoorOpen(state) && WarpDoorBlock.isLowerPart(state, pos)
                && !entity.getData(DataAttachmentRegistry.PREVENT_WARP);
    }

    public static void closeAfterWarp(BlockState state, ServerLevel level, BlockPos pos) {
        if (!WarpDoorBlock.isDoorOpen(state))
            return;

        level.setBlock(pos, WarpDoorBlock.setDoorOpen(state, false), 10);
        if (level.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE)
            warpBE.playDoorSounds(null, level, pos, false, WarpDoorBlock.getType(state));
    }

    public static boolean canTeleport(Entity entity) {
        if (entity instanceof Player)
            return ConfigRegistry.TELEPORT_PLAYERS.get();
        else if (entity instanceof LivingEntity)
            return ConfigRegistry.TELEPORT_MOBS.get();
        return ConfigRegistry.TELEPORT_NON_MOBS.get();
    }
}
