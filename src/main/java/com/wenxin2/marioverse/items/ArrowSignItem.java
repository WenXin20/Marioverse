package com.wenxin2.marioverse.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import javax.annotation.Nullable;
import java.util.Map;

public class ArrowSignItem extends BlockItem {
    private final Block wallSign;
    private final Block hangingSign;

    public ArrowSignItem(Properties properties, Block standingSign, Block wallSign, Block hangingSign) {
        super(standingSign, properties);
        this.wallSign = wallSign;
        this.hangingSign = hangingSign;
    }

    protected boolean canPlace(LevelReader level, BlockState state, BlockPos pos) {
        return state.canSurvive(level, pos);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState result = null;

        for (Direction direction : context.getNearestLookingDirections()) {
            Block candidate = switch (direction) {
                case UP -> this.getBlock();
                case DOWN -> this.hangingSign;
                default -> this.wallSign;
            };

            BlockState candidateState = candidate.getStateForPlacement(context);
            if (candidateState != null && this.canPlace(level, candidateState, pos)) {
                result = candidateState;
                break;
            }
        }

        return result != null && level.isUnobstructed(result, pos, CollisionContext.empty()) ? result : null;
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        super.registerBlocks(blockToItemMap, item);
        blockToItemMap.put(this.wallSign, item);
        blockToItemMap.put(this.hangingSign, item);
    }
}