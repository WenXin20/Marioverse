package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpDoorBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WarpLinkPathing {
    public static final float WARP_LINK_COST = 2.0F;
    private static final double PAINTING_EXIT_DISTANCE = 0.6;
    private static final double PAINTING_SEARCH_INFLATE = 0.1;

    public record WarpLink(BlockPos trigger, BlockPos destinationStand, boolean jump, @Nullable BlockPos door) {}

    public static boolean canPathThroughWarps(Mob mob) {
        return ConfigRegistry.TELEPORT_MOBS.get() && mob.getNavigation().getNodeEvaluator().canOpenDoors();
    }

    public static List<WarpLink> getWarpLinks(Level level, BlockPos trigger, Entity entity) {
        List<WarpLink> links = new ArrayList<>();
        WarpLinkPathing.addPipeLink(level, trigger, trigger.below(), Direction.UP, false, links);

        for (Direction direction : Direction.Plane.HORIZONTAL)
            WarpLinkPathing.addPipeLink(level, trigger, trigger.relative(direction), direction.getOpposite(), false, links);

        BlockPos downPipe = ClearPipeNetwork.findReachableDownPipe(level, trigger, entity, WarpLinkPathing::isOpenDownWarpPipe);
        if (downPipe != null)
            WarpLinkPathing.addPipeLink(level, trigger, downPipe, Direction.DOWN, true, links);

        WarpLinkPathing.addDoorLink(level, trigger, links);
        WarpLinkPathing.addTrapdoorLink(level, trigger, links);
        WarpLinkPathing.addPaintingLinks(level, trigger, links);
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

            for (WarpLink link : WarpLinkPathing.getWarpLinks(level, trigger, entity)) {
                if (link.destinationStand().equals(stand))
                    return link;
            }
        }
        return null;
    }

    public static void openDoor(Level level, Mob mob, WarpLink link) {
        if (link.door() == null)
            return;

        BlockState state = level.getBlockState(link.door());
        if (state.getBlock() instanceof DoorBlock doorBlock && !state.getValue(DoorBlock.OPEN) && doorBlock.type().canOpenByHand())
            doorBlock.setOpen(mob, level, state, link.door(), true);
    }

    private static boolean isOpenDownWarpPipe(BlockState state) {
        return state.getBlock() instanceof WarpPipeBlock && !(state.getBlock() instanceof ClearWarpPipeBlock)
                && state.getValue(WarpPipeBlock.ENTRANCE) && !state.getValue(WarpPipeBlock.CLOSED)
                && state.getValue(WarpPipeBlock.FACING) == Direction.DOWN;
    }

    private static void addPipeLink(Level level, BlockPos trigger, BlockPos pipe, Direction requiredFacing, boolean jump, List<WarpLink> links) {
        BlockState state = level.getBlockState(pipe);
        if (!(state.getBlock() instanceof WarpPipeBlock) || state.getBlock() instanceof ClearWarpPipeBlock
                || !state.getValue(WarpPipeBlock.ENTRANCE) || state.getValue(WarpPipeBlock.CLOSED)
                || state.getValue(WarpPipeBlock.FACING) != requiredFacing)
            return;

        BaseWarpBlockEntity warpBE = WarpLinkPathing.getUsableWarpBlockEntity(level, pipe);
        if (warpBE == null)
            return;

        BlockState destinationState = level.getBlockState(warpBE.destinationPos);
        if (!(destinationState.getBlock() instanceof WarpPipeBlock) || !destinationState.getValue(WarpPipeBlock.ENTRANCE))
            return;

        links.add(new WarpLink(trigger.immutable(),
                warpBE.destinationPos.relative(destinationState.getValue(WarpPipeBlock.FACING)).immutable(), jump, null));
    }

    private static void addDoorLink(Level level, BlockPos trigger, List<WarpLink> links) {
        if (ConfigRegistry.DISABLE_WARP_DOORS.get())
            return;

        BlockState state = level.getBlockState(trigger);
        if (!WarpDoorBlock.isDoor(state) || !WarpDoorBlock.isLowerPart(state, trigger)
                || (!WarpDoorBlock.isDoorOpen(state) && !WarpDoorBlock.getType(state).canOpenByHand())
                || !(level.getBlockEntity(trigger) instanceof WarpDoorBlockEntity))
            return;

        BaseWarpBlockEntity warpBE = WarpLinkPathing.getUsableWarpBlockEntity(level, trigger);
        if (warpBE != null)
            links.add(new WarpLink(trigger.immutable(), warpBE.destinationPos.immutable(), false, trigger.immutable()));
    }

    private static void addTrapdoorLink(Level level, BlockPos trigger, List<WarpLink> links) {
        if (ConfigRegistry.DISABLE_WARP_TRAPDOORS.get())
            return;

        BlockState state = level.getBlockState(trigger);
        if (!(state.getBlock() instanceof TrapDoorBlock) || !state.getValue(TrapDoorBlock.OPEN)
                || !(level.getBlockEntity(trigger) instanceof WarpTrapDoorBlockEntity))
            return;

        BaseWarpBlockEntity warpBE = WarpLinkPathing.getUsableWarpBlockEntity(level, trigger);
        if (warpBE != null)
            links.add(new WarpLink(trigger.immutable(), warpBE.destinationPos.immutable(), false, null));
    }

    private static void addPaintingLinks(Level level, BlockPos trigger, List<WarpLink> links) {
        if (ConfigRegistry.DISABLE_WARP_PAINTINGS.get() || !(level instanceof ServerLevel serverLevel))
            return;

        List<Entity> paintings = level.getEntities((Entity) null, new AABB(trigger).inflate(PAINTING_SEARCH_INFLATE),
                entity -> PaintingRipples.isRipplePainting(entity) && entity instanceof WarpLinkableEntity linkable
                        && linkable.mv$getWarpUUID() != null);

        for (Entity painting : paintings) {
            Entity destination = serverLevel.getEntity(((WarpLinkableEntity) painting).mv$getWarpUUID());
            if (destination == null || !destination.isAlive())
                continue;

            Direction facing = destination.getDirection();
            AABB bounds = destination.getBoundingBox();
            Vec3 exit = bounds.getCenter().add(facing.getStepX() * PAINTING_EXIT_DISTANCE, 0.0, facing.getStepZ() * PAINTING_EXIT_DISTANCE);
            links.add(new WarpLink(trigger.immutable(), BlockPos.containing(exit.x, bounds.minY, exit.z), false, null));
        }
    }

    @Nullable
    private static BaseWarpBlockEntity getUsableWarpBlockEntity(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE) || warpBE.preventWarp
                || !warpBE.hasDestinationPos() || warpBE.destinationPos == null || !level.isLoaded(warpBE.destinationPos)
                || !(level.getBlockEntity(warpBE.destinationPos) instanceof BaseWarpBlockEntity))
            return null;
        return warpBE;
    }
}
