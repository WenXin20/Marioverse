package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DeepWetMudBlock extends DirtPathBlock {
    protected static final VoxelShape SHAPE = Block
            .box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
    protected static final VoxelShape COLLISION_SHAPE = Block
            .box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    private static final int TRAIL_PARTICLE_COUNT = 6;

    public DeepWetMudBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? Block.pushEntitiesUp(this.defaultBlockState(), BlockRegistry.WET_MUD.get().defaultBlockState(),
                        context.getLevel(), context.getClickedPos())
                : super.getStateForPlacement(context);
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        WetMudFarmlandBlock.turnToMud(null, state, serverLevel, pos);
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

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        boolean isMoving = entity.xOld != entity.getX() || entity.zOld != entity.getZ();

        if (level.isClientSide && isMoving) {
            RandomSource random = level.getRandom();

            for (int i = 0; i < TRAIL_PARTICLE_COUNT; i++) {
                double x = entity.getX() + (random.nextDouble() - 0.5) * entity.getBbWidth();
                double z = entity.getZ() + (random.nextDouble() - 0.5) * entity.getBbWidth();
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        x, pos.getY() + 1, z, 0.0, 0.0, 0.0);
            }
        }
        super.stepOn(level, pos, state, entity);
    }
}
