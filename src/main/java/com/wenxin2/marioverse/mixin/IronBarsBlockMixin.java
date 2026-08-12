package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.PicketFenceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronBarsBlock.class)
public abstract class IronBarsBlockMixin {

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void mv$connectFencesOnPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state == null)
            return;

        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty property = this.mv$propertyFor(direction);
            if (state.getValue(property))
                continue;

            BlockState neighbor = level.getBlockState(pos.relative(direction));
            if (neighbor.getBlock() instanceof PicketFenceBlock fence
                    && fence.connectsToEdge(neighbor, direction.getOpposite())) {
                state = state.setValue(property, true);
            }
        }

        cir.setReturnValue(state);
    }

    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void mv$connectFencesOnUpdate(BlockState state, Direction direction, BlockState neighborState,
                                          LevelAccessor level, BlockPos pos, BlockPos neighborPos,
                                          CallbackInfoReturnable<BlockState> cir) {
        if (!direction.getAxis().isHorizontal())
            return;
        BlockState result = cir.getReturnValue();
        if (result == null)
            return;
        BooleanProperty property = this.mv$propertyFor(direction);
        if (result.getValue(property))
            return;

        if (neighborState.getBlock() instanceof PicketFenceBlock fence
                && fence.connectsToEdge(neighborState, direction.getOpposite()))
            cir.setReturnValue(result.setValue(property, true));
    }

    @Unique
    private BooleanProperty mv$propertyFor(Direction direction) {
        return switch (direction) {
            case NORTH -> CrossCollisionBlock.NORTH;
            case EAST -> CrossCollisionBlock.EAST;
            case SOUTH -> CrossCollisionBlock.SOUTH;
            case WEST -> CrossCollisionBlock.WEST;
            default -> throw new IllegalArgumentException("Not a horizontal direction: " + direction);
        };
    }
}