package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

public class StoneFloorBlock extends DirectionalBlock {
    public static final MapCodec<StoneFloorBlock> CODEC = simpleCodec(StoneFloorBlock::new);

    @NotNull
    @Override
    public MapCodec<StoneFloorBlock> codec() {
        return CODEC;
    }

    public StoneFloorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @NotNull
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @NotNull
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public static BlockState getFloorState(Block floorBlock, Direction.Axis axis, Direction clickedFace) {
        Direction facing = clickedFace.getAxis() == axis
                ? clickedFace : Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        return floorBlock.defaultBlockState().setValue(FACING, facing);
    }
}
