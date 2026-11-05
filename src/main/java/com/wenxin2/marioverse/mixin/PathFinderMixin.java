package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.wenxin2.marioverse.entities.ai.PipeLinkNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PathFinder.class)
public abstract class PathFinderMixin {
    @Shadow @Final private NodeEvaluator nodeEvaluator;

    @ModifyReturnValue(method = "distance", at = @At("RETURN"))
    private float mv$usePipeLinkCost(float distance, Node from, Node to) {
        if (this.nodeEvaluator instanceof PipeLinkNodeEvaluator pipeLinks) {
            float cost = pipeLinks.mv$getPipeLinkCost(from, to);
            if (cost >= 0.0F)
                return cost;
        }
        return distance;
    }
}
