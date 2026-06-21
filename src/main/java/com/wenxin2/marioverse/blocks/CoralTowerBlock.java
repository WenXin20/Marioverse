package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import team.chisel.ctm.client.util.Dir;

public class CoralTowerBlock extends BaseCoralPlantTypeBlock implements BonemealableBlock {
    public static final MapCodec<CoralTowerBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(CoralBlock.DEAD_CORAL_FIELD.forGetter(CoralTowerBlock::getDeadBlock), propertiesCodec())
                    .apply(instance, CoralTowerBlock::new));
    public static final BooleanProperty TOP = BlockStatePropertyRegistry.TOP;
    private final Supplier<? extends Block> deadBlock;

    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14).optimize();
    protected static final VoxelShape SHAPE_TOP = Block.box(2, 0, 2, 14, 14, 14).optimize();

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<CoralPlantBlock> codec() {
        return (MapCodec<CoralPlantBlock>) (MapCodec<?>) CODEC;
    }

    public CoralTowerBlock(Block deadBlock, Properties properties) {
        this(() -> deadBlock, properties);
    }

    public CoralTowerBlock(Supplier<? extends Block> deadBlock, Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, true).setValue(WATERLOGGED, false));
        this.deadBlock = deadBlock;
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
        this.tryScheduleDieTick(state, levelAccessor, pos);

        if (direction == Direction.DOWN && !state.canSurvive(levelAccessor, pos))
            return Blocks.AIR.defaultBlockState();

        if (state.getValue(WATERLOGGED))
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));

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
        this.tryScheduleDieTick(state, level, pos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockState stateBelow = levelReader.getBlockState(pos.below());
        return super.canSurvive(state, levelReader, pos) || stateBelow.is(this) || stateBelow.is(this.getDeadBlock());
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!scanForWater(state, level, pos))
            level.setBlock(pos, this.deadBlock.get().defaultBlockState().setValue(WATERLOGGED, false), 2);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return levelReader.getBlockState(pos).getValue(WATERLOGGED) &&
                (levelReader.getBlockState(pos.above()).canBeReplaced()
                    || levelReader.getBlockState(pos.above()).is(this));
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return level.getBlockState(pos).getValue(WATERLOGGED);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos growPos = pos.above();
        int blocksToGrow = 2 + random.nextInt(3);

        for (int i = 0; i < blocksToGrow && level.getBlockState(growPos).getValue(WATERLOGGED)
                && (level.getBlockState(growPos).canBeReplaced() || level.getBlockState(growPos).is(this)); i++) {
            boolean waterlogged = level.getFluidState(growPos).is(FluidTags.WATER);
            level.setBlockAndUpdate(growPos, defaultBlockState().setValue(WATERLOGGED, waterlogged));
            growPos = growPos.above();
        }
    }

    public Block getDeadBlock() {
        return this.deadBlock.get();
    }

    public BlockState calculateTop(BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState stateAbove = level.getBlockState(pos.above());
        boolean connects = stateAbove.is(this) || stateAbove.is(this.getDeadBlock());
        boolean shouldBeTop = !connects;

        if (state.getValue(TOP) != shouldBeTop)
            return state.setValue(TOP, shouldBeTop);
        return state;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}