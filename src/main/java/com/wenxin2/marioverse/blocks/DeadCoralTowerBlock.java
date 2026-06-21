package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseCoralPlantBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DeadCoralTowerBlock extends BaseCoralPlantBlock {
    public static final MapCodec<DeadCoralTowerBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(CoralBlock.DEAD_CORAL_FIELD.forGetter(DeadCoralTowerBlock::getLiveBlock), propertiesCodec())
                    .apply(instance, DeadCoralTowerBlock::new));
    public static final BooleanProperty TOP = BlockStatePropertyRegistry.TOP;
    private final Supplier<? extends Block> liveBlock;

    protected static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14).optimize();
    protected static final VoxelShape SHAPE_TOP = Block.box(2, 0, 2, 14, 14, 14).optimize();

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<BaseCoralPlantBlock> codec() {
        return (MapCodec<BaseCoralPlantBlock>) (MapCodec<?>) CODEC;
    }

    public DeadCoralTowerBlock(Block liveBlock, Properties properties) {
        this(() -> liveBlock, properties);
    }

    public DeadCoralTowerBlock(Supplier<? extends Block> liveBlock, Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, true).setValue(WATERLOGGED, false));
        this.liveBlock = liveBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TOP);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return state.getValue(TOP) ? SHAPE_TOP : SHAPE;
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor levelAccessor, BlockPos pos, BlockPos posNeighbor) {
        state = super.updateShape(state, direction, neighborState, levelAccessor, pos, posNeighbor);

        if (direction == Direction.UP)
            state = this.calculateTop(state, levelAccessor, pos);
        return state;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        BlockState newState = this.calculateTop(state, level, pos);

        if (newState != state)
            level.setBlock(pos, newState, 3);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockState stateBelow = levelReader.getBlockState(pos.below());
        return super.canSurvive(state, levelReader, pos) || stateBelow.is(this) || stateBelow.is(this.getLiveBlock());
    }

    public Block getLiveBlock() {
        return this.liveBlock.get();
    }

    public BlockState calculateTop(BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState stateAbove = level.getBlockState(pos.above());

        boolean connects = stateAbove.is(this) || stateAbove.is(this.getLiveBlock());
        boolean shouldBeTop = !connects;

        if (state.getValue(TOP) != shouldBeTop)
            return state.setValue(TOP, shouldBeTop);
        return state;
    }
}