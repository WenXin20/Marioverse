package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SuperMushroomBlock extends MushroomBlock implements BonemealableBlock {
    public static final BooleanProperty TOP = BlockStatePropertyRegistry.TOP;

    protected static final VoxelShape TOP_SHAPE = Shapes
            .or(Block.box(6, 0, 6, 10, 4, 10),
                    Block.box(3, 4, 3, 13, 10, 13)).optimize();
    protected static final VoxelShape SHAPE = Block
            .box(6, 0, 6, 10, 16, 10).optimize();

    public SuperMushroomBlock(ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, Properties properties) {
        super(configuredFeature, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        final Vec3 offset = state.getOffset(blockGetter, pos);

        if (state.getValue(TOP))
            return TOP_SHAPE.move(offset.x, offset.y, offset.z);
        return SHAPE.move(offset.x, offset.y, offset.z);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateAbove = context.getLevel().getBlockState(context.getClickedPos().above());
        return this.defaultBlockState().setValue(TOP, !stateAbove.is(this));
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        BlockState newState = super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
        if (direction == Direction.UP && newState.is(this))
            return newState.setValue(TOP, !neighborState.is(this));
        return newState;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockPos posBelow = pos.below();
        BlockState stateBelow = levelReader.getBlockState(posBelow);

        return stateBelow.is(this) || stateBelow.isSolidRender(levelReader, posBelow)
                || stateBelow.getBlock() instanceof FarmBlock;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        if (levelReader.getBlockState(pos.below()).is(BlockTags.DIRT))
            return true;

        BlockPos posAbove = this.getTopPos(levelReader, pos).above();
        return !levelReader.isOutsideBuildHeight(posAbove) && levelReader.isEmptyBlock(posAbove);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos.below()).is(BlockTags.DIRT))
            return super.isBonemealSuccess(level, random, pos, state);
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        if (serverLevel.getBlockState(pos.below()).is(BlockTags.DIRT))
            this.growMushroom(serverLevel, pos, state, random);
        else serverLevel.setBlock(this.getTopPos(serverLevel, pos).above(), this.defaultBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance * 0.5F);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        if (entity.isSuppressingBounce())
            super.updateEntityAfterFallOn(blockGetter, entity);
        else this.bounceUp(entity);
    }

    private void bounceUp(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0) {
            double dy = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(vec3.x, -vec3.y * 0.66F * dy, vec3.z);
        }
    }

    private BlockPos getTopPos(BlockGetter blockGetter, BlockPos pos) {
        BlockPos.MutableBlockPos topPos = pos.mutable();
        while (blockGetter.getBlockState(topPos.above()).is(this))
            topPos.move(Direction.UP);
        return topPos.immutable();
    }
}
