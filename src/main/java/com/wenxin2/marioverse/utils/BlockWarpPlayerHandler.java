package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface BlockWarpPlayerHandler extends BlockWarpEntityHandler {
    @NotNull
    default Boolean getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

    @Override
    default void enterWarpDoor(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        if (entity instanceof Player player && (!getBlockWarpTeleportConfig() || player.getType().is(TagRegistry.CANNOT_WARP)
                || player.getPersistentData().getBoolean("marioverse:prevent_warp"))) {
            this.displayNoTeleportMessage(player);
        } else BlockWarpEntityHandler.super.enterWarpDoor(entity, world, pos, warpPos, warpBE);
    }

    @Override
    default void enterWarpPipe(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        BlockState state = world.getBlockState(pos);
        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (entity instanceof Player player && (!getBlockWarpTeleportConfig() || entity.getType().is(TagRegistry.CANNOT_WARP)
                || entity.getPersistentData().getBoolean("marioverse:prevent_warp"))) {
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && entity.isShiftKeyDown() && (entityY + entity.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage(player);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !entity.isShiftKeyDown() && entity.getMotionDirection() == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                this.displayNoTeleportMessage(player);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !entity.isShiftKeyDown() && entity.getMotionDirection() == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                this.displayNoTeleportMessage(player);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !entity.isShiftKeyDown() && entity.getMotionDirection() == Direction.WEST
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage(player);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !entity.isShiftKeyDown() && entity.getMotionDirection() == Direction.EAST
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage(player);
            }
        } else BlockWarpEntityHandler.super.enterWarpPipe(entity, world, pos, warpPos, warpBE);
    }

    @Override
    default void enterWarpPipeAbove(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(entity.getBbHeight())));

        double entityX = entity.getX();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (entity instanceof Player player && (!getBlockWarpTeleportConfig() || entity.getType().is(TagRegistry.CANNOT_WARP)
                || entity.getPersistentData().getBoolean("marioverse:prevent_warp"))) {
            if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN && (entity.getBlockY() < blockY)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage(player);
            }
        } else BlockWarpEntityHandler.super.enterWarpPipeAbove(entity, world, pos, warpPos, warpBE);
    }

    private void displayNoTeleportMessage(Player player) {
        if (!getBlockWarpTeleportConfig() || player.getType().is(TagRegistry.CANNOT_WARP))
            player.displayClientMessage(Component.translatable("display.marioverse.players_cannot_teleport"), true);
    }

    private void displayDestinationMissingMessage(Player player) {
        player.displayClientMessage(Component.translatable("display.marioverse.warp_destination_missing"), true);
    }
}
