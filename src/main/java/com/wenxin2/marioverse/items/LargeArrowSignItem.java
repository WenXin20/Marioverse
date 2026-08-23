package com.wenxin2.marioverse.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import javax.annotation.Nullable;

public class LargeArrowSignItem extends ArrowSignItem {
    private final Block largeWallSign;

    public LargeArrowSignItem(Properties properties, Block standingSign, Block wallSign) {
        super(properties, standingSign, wallSign, wallSign);
        this.largeWallSign = wallSign;
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        if (context.getClickedFace() == Direction.DOWN)
            return null;

        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Block candidate = context.getClickedFace() == Direction.UP ? this.getBlock() : this.largeWallSign;

        BlockState result = candidate.getStateForPlacement(context);
        if (result == null)
            return null;

        if (result.hasProperty(BlockStateProperties.ROTATION_16))
            result = result.setValue(BlockStateProperties.ROTATION_16,
                    RotationSegment.convertToSegment(context.getRotation()));

        return this.canPlace(level, result, pos)
                && level.isUnobstructed(result, pos, CollisionContext.empty()) ? result : null;
    }
}
