package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.wenxin2.marioverse.entities.ai.PipeLinkNodeEvaluator;
import com.wenxin2.marioverse.utils.ClearPipeNetwork;
import com.wenxin2.marioverse.utils.WarpLinkPathing;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WalkNodeEvaluator.class)
public abstract class WalkNodeEvaluatorMixin extends NodeEvaluator implements PipeLinkNodeEvaluator {
    @Unique private static final int MAX_PIPE_LINKS = 8;
    @Unique private static final float PIPE_LINK_COST_PER_BLOCK = 0.25F;
    @Unique private final Map<Long, Float> mv$pipeLinkCosts = new HashMap<>();

    @Inject(method = "prepare", at = @At("HEAD"))
    private void mv$prepare(PathNavigationRegion region, Mob mob, CallbackInfo ci) {
        this.mv$pipeLinkCosts.clear();
    }

    @ModifyReturnValue(method = "getNeighbors", at = @At("RETURN"))
    private int mv$prepare(int count, Node[] neighbors, Node node) {
        Level level = this.mob.level();
        BlockPos approach = node.asBlockPos();
        int added = 0;

        if (WarpLinkPathing.canPathThroughWarps(this.mob)) {
            for (WarpLinkPathing.WarpLink link : WarpLinkPathing.getWarpLinks(level, approach, this.mob)) {
                if (count >= neighbors.length || added >= MAX_PIPE_LINKS)
                    break;

                Node exitNode = this.mv$getLinkNode(link.destinationStand());
                if (exitNode == null)
                    continue;

                this.mv$pipeLinkCosts.put(WalkNodeEvaluatorMixin.mv$linkKey(node, exitNode), WarpLinkPathing.WARP_LINK_COST);
                neighbors[count++] = exitNode;
                added++;
            }
        }

        if (!ClearPipeNetwork.canPathThroughClearPipes(this.mob))
            return count;

        ClearPipeNetwork.PipeEntry entry = ClearPipeNetwork.resolveEntrance(level, approach, this.mob);
        if (entry == null || !ClearPipeNetwork.fitsEntrance(level, entry.entrance(), level.getBlockState(entry.entrance()), this.mob))
            return count;

        for (ClearPipeNetwork.ExitLink link : ClearPipeNetwork.getExitLinks(level, entry.entrance())) {
            if (count >= neighbors.length || added >= MAX_PIPE_LINKS)
                break;

            Node exitNode = this.mv$getLinkNode(link.stand());
            if (exitNode == null)
                continue;

            this.mv$pipeLinkCosts.put(WalkNodeEvaluatorMixin.mv$linkKey(node, exitNode), 1.0F + link.distance() * PIPE_LINK_COST_PER_BLOCK);
            neighbors[count++] = exitNode;
            added++;
        }
        return count;
    }

    @Override
    public float mv$getPipeLinkCost(Node from, Node to) {
        Float cost = this.mv$pipeLinkCosts.get(WalkNodeEvaluatorMixin.mv$linkKey(from, to));
        return cost != null ? cost : -1.0F;
    }

    @Unique
    private Node mv$getLinkNode(BlockPos pos) {
        Node node = this.getNode(pos.getX(), pos.getY(), pos.getZ());
        if (node.closed)
            return null;

        if (node.type == null) {
            node.type = PathType.WALKABLE;
            node.costMalus = 0.0F;
        }
        return node.costMalus < 0.0F ? null : node;
    }

    @Unique
    private static long mv$linkKey(Node from, Node to) {
        return ((long) from.hashCode() << 32) ^ (to.hashCode() & 0xFFFFFFFFL);
    }
}
