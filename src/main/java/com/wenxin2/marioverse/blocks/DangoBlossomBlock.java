package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DangoBlossomBlock extends Block implements SimpleWaterloggedBlock {
    public static final MapCodec<DangoBlossomBlock> CODEC = simpleCodec(DangoBlossomBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE =
            Shapes.or(Block.box(1, 0, 1, 15, 6, 15)).optimize();

    @Override
    public MapCodec<DangoBlossomBlock> codec() {
        return CODEC;
    }

    public DangoBlossomBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(WATERLOGGED);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader worldReader, BlockPos pos) {
        return Block.canSupportCenter(worldReader, pos.below(), Direction.UP)
                || worldReader.getBlockState(pos.below()).getBlock() instanceof CactusBlock
                || worldReader.getBlockState(pos.below()).getBlock() instanceof LeavesBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        BlockPos pos = placeContext.getClickedPos();
        FluidState fluidState = placeContext.getLevel().getFluidState(pos);

        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 100;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        BlockPos.MutableBlockPos posMutable = new BlockPos.MutableBlockPos();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (ConfigRegistry.DANGO_BLOSSOM_PARTICLES.get()) {
            world.addParticle(ParticleRegistry.GOLD_POLLEN.get(),
                    x + random.nextDouble(), y + 0.25, z + random.nextDouble(),
                    0.0, 0.0, 0.0);

            for (int amt = 0; amt < 14; amt++) {
                posMutable.set(x + Mth.nextInt(random, -10, 10), y + random.nextInt(10), z + Mth.nextInt(random, -10, 10));
                BlockState stateMutable = world.getBlockState(posMutable);

                if (!stateMutable.isCollisionShapeFullBlock(world, posMutable)) {
                    world.addParticle(ParticleRegistry.GOLD_POLLEN.get(),
                            (double) posMutable.getX() + random.nextDouble(),
                            (double) posMutable.getY() + random.nextDouble(),
                            (double) posMutable.getZ() + random.nextDouble(),
                            0.0, 0.0, 0.0);
                }
            }
        }
    }
}
