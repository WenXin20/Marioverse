package com.wenxin2.marioverse.entities.ai;

import net.minecraft.world.level.pathfinder.Node;

public interface PipeLinkNodeEvaluator {
    float mv$getPipeLinkCost(Node from, Node to);
}
