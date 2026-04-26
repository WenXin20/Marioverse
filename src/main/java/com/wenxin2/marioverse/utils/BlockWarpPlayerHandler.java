package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public interface BlockWarpPlayerHandler extends BlockWarpEntityHandler {
    @Override
    default void enterWarpDoor(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        if (entity instanceof Player player && (!this.mv$getBlockWarpTeleportConfig(entity)
                || player.getType().is(TagRegistry.CANNOT_WARP) || entity.getData(DataAttachmentRegistry.PREVENT_WARP))) {
            this.displayNoTeleportMessage(player, world.getBlockState(pos));
        } else BlockWarpEntityHandler.super.enterWarpDoor(entity, world, pos, warpPos, warpBE);
    }

    @Override
    default void enterWarpPipe(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE,
                               @Nullable SableProvider.SableContext context) {
        BlockState state = world.getBlockState(pos);
        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (entity instanceof Player player && (!this.mv$getBlockWarpTeleportConfig(entity)
                || entity.getType().is(TagRegistry.CANNOT_WARP) || entity.getData(DataAttachmentRegistry.PREVENT_WARP))) {
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && entity.isShiftKeyDown() && (entityY + entity.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage(player, state);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !entity.isShiftKeyDown() && entity.getMotionDirection() == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                this.displayNoTeleportMessage(player, state);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !entity.isShiftKeyDown() && entity.getMotionDirection() == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                this.displayNoTeleportMessage(player, state);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !entity.isShiftKeyDown() && entity.getMotionDirection() == Direction.WEST
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage(player, state);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !entity.isShiftKeyDown() && entity.getMotionDirection() == Direction.EAST
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage(player, state);
            }
        } else BlockWarpEntityHandler.super.enterWarpPipe(entity, world, pos, warpPos, warpBE, context);
    }

    @Override
    default void enterWarpPipeAbove(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE,
                                    @Nullable SableProvider.SableContext context) {
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(entity.getBbHeight())));

        double entityX = entity.getX();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (entity instanceof Player player && (!this.mv$getBlockWarpTeleportConfig(entity)
                || entity.getType().is(TagRegistry.CANNOT_WARP) || entity.getData(DataAttachmentRegistry.PREVENT_WARP))) {
            if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN && (entity.getBlockY() < blockY)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage(player, stateAboveEntity);
            }
        } else BlockWarpEntityHandler.super.enterWarpPipeAbove(entity, world, pos, warpPos, warpBE, context);
    }

    private void displayNoTeleportMessage(Player player, BlockState state) {
        if (!this.mv$getBlockWarpTeleportConfig(player) || player.getType().is(TagRegistry.CANNOT_WARP)) {
            if (state.getBlock() instanceof WarpPipeBlock) {
                player.displayClientMessage(Component.translatable("display.marioverse.pipes_cannot_teleport_players"), true);
            } else if (state.getBlock() instanceof DoorBlock) {
                player.displayClientMessage(Component.translatable("display.marioverse.doors_cannot_teleport_players"), true);
            } else if (state.getBlock() instanceof TrapDoorBlock) {
                player.displayClientMessage(Component.translatable("display.marioverse.trapdoors_cannot_teleport_players"), true);
            }
        }

        if (player.getData(DataAttachmentRegistry.PREVENT_WARP))
            player.displayClientMessage(Component.translatable("display.marioverse.warp_disrupted_player"), true);
    }
}
