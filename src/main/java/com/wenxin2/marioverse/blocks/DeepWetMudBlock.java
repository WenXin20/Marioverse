package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DeepWetMudBlock extends DirtPathBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block
            .box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
    protected static final VoxelShape COLLISION_SHAPE = Block
            .box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    private static final int TRAIL_PARTICLE_COUNT = 6;
    private static final double SPLASH_SPREAD_FACTOR = 2.0;

    public DeepWetMudBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(WATERLOGGED);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @NotNull
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos()))
            return Block.pushEntitiesUp(this.defaultBlockState(), BlockRegistry.WET_MUD.get().defaultBlockState(),
                    context.getLevel(), context.getClickedPos());

        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context)
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        WetMudFarmlandBlock.turnToMud(null, state, serverLevel, pos);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        Vec3 deltaMovement = entity.getDeltaMovement();
        boolean isMoving = deltaMovement.horizontalDistance() > 0.1;

        if (level.isClientSide && isMoving) {
            RandomSource random = level.getRandom();
            double vx = deltaMovement.x * -4.0;
            double vz = deltaMovement.z * -4.0;
            double horizontalSpeed = Math.sqrt(vx * vx + vz * vz);
            Vec3 perpendicular = new Vec3(-vz, 0.0, vx).normalize();

            for (int i = 0; i < TRAIL_PARTICLE_COUNT; i++) {
                double x = entity.getX() + (random.nextDouble() - 0.8) * entity.getBbWidth() * 1.1;
                double z = entity.getZ() + (random.nextDouble() - 0.8) * entity.getBbWidth() * 1.1;
                double spread = (random.nextDouble() - 0.5) * horizontalSpeed * SPLASH_SPREAD_FACTOR;

                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        x, pos.getY() + 1, z,
                        vx + perpendicular.x * spread, 1.5, vz + perpendicular.z * spread);
            }
        }
        super.stepOn(level, pos, state, entity);
    }
}
