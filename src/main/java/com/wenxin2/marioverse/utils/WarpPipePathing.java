package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

public class WarpPipePathing {
    public static final float WARP_LINK_COST = 2.0F;

    public record WarpLink(BlockPos pipe, BlockPos trigger, BlockPos destinationStand, boolean jump) {}

    public static boolean canPathThroughWarpPipes(Mob mob) {
        return (mob instanceof AbstractVillager || mob instanceof AbstractPiglin) && ConfigRegistry.TELEPORT_MOBS.get();
    }

    public static List<WarpLink> getWarpLinks(Level level, BlockPos trigger, Entity entity) {
        List<WarpLink> links = new ArrayList<>();
        WarpPipePathing.addWarpLink(level, trigger, trigger.below(), Direction.UP, false, links);

        for (Direction direction : Direction.Plane.HORIZONTAL)
            WarpPipePathing.addWarpLink(level, trigger, trigger.relative(direction), direction.getOpposite(), false, links);

        BlockPos downPipe = ClearPipeNetwork.findReachableDownPipe(level, trigger, entity, WarpPipePathing::isOpenDownWarpPipe);
        if (downPipe != null)
            WarpPipePathing.addWarpLink(level, trigger, downPipe, Direction.DOWN, true, links);
        return links;
    }

    @Nullable
    public static WarpLink findWarpStep(Level level, @Nullable Path path, Entity entity) {
        if (path == null || path.isDone())
            return null;

        int next = path.getNextNodeIndex();
        for (int index = Math.max(0, next - 1); index <= next && index + 1 < path.getNodeCount(); index++) {
            BlockPos trigger = path.getNode(index).asBlockPos();
            BlockPos stand = path.getNode(index + 1).asBlockPos();

            for (WarpLink link : WarpPipePathing.getWarpLinks(level, trigger, entity)) {
                if (link.destinationStand().equals(stand))
                    return link;
            }
        }
        return null;
    }

    private static boolean isOpenDownWarpPipe(BlockState state) {
        return state.getBlock() instanceof WarpPipeBlock && !(state.getBlock() instanceof ClearWarpPipeBlock)
                && state.getValue(WarpPipeBlock.ENTRANCE) && !state.getValue(WarpPipeBlock.CLOSED)
                && state.getValue(WarpPipeBlock.FACING) == Direction.DOWN;
    }

    private static void addWarpLink(Level level, BlockPos trigger, BlockPos pipe, Direction requiredFacing, boolean jump, List<WarpLink> links) {
        BlockState state = level.getBlockState(pipe);
        if (!(state.getBlock() instanceof WarpPipeBlock) || state.getBlock() instanceof ClearWarpPipeBlock
                || !state.getValue(WarpPipeBlock.ENTRANCE) || state.getValue(WarpPipeBlock.CLOSED)
                || state.getValue(WarpPipeBlock.FACING) != requiredFacing)
            return;

        if (!(level.getBlockEntity(pipe) instanceof BaseWarpBlockEntity warpBE) || warpBE.preventWarp
                || !warpBE.hasDestinationPos() || warpBE.destinationPos == null || !level.isLoaded(warpBE.destinationPos))
            return;

        BlockState destinationState = level.getBlockState(warpBE.destinationPos);
        if (!(destinationState.getBlock() instanceof WarpPipeBlock) || !destinationState.getValue(WarpPipeBlock.ENTRANCE))
            return;

        links.add(new WarpLink(pipe.immutable(), trigger.immutable(),
                warpBE.destinationPos.relative(destinationState.getValue(WarpPipeBlock.FACING)).immutable(), jump));
    }
}
