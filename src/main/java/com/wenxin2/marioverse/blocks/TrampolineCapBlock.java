package com.wenxin2.marioverse.blocks;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrampolineCapBlock extends MushroomBlock implements BonemealableBlock, ToggleableBlock {
    public static final BooleanProperty ACTIVE = BlockStatePropertyRegistry.ACTIVE;
    protected static final VoxelShape SHAPE = Shapes
            .or(Block.box(7, 0, 7, 9, 8, 9),
                    Block.box(4, 8, 4, 12, 10, 12)).optimize();

    public TrampolineCapBlock(ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, Properties properties) {
        super(configuredFeature, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        final Vec3 offset = state.getOffset(blockGetter, pos);

        return SHAPE.move(offset.x, offset.y, offset.z);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return this.getShape(state, blockGetter, pos, context);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        boolean isOn = state.getValue(ACTIVE) != state.is(BlockRegistry.BLUE_ON_OFF_MUSHROOM_TRAMPOLINE_CAP);
        super.fallOn(level, state, pos, entity, isOn ? fallDistance * 0.5F : fallDistance);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        BlockState state = blockGetter.getBlockState(entity.getOnPos());
        Vec3 motion = entity.getDeltaMovement();

        if (!entity.isSuppressingBounce() && motion.y < 0.0 && state.is(this)
                && state.getValue(ACTIVE) != state.is(BlockRegistry.BLUE_ON_OFF_MUSHROOM_TRAMPOLINE_CAP))
            entity.setDeltaMovement(motion.x, -motion.y * 0.5F * (entity instanceof LivingEntity ? 1.0 : 0.8), motion.z);
        else super.updateEntityAfterFallOn(blockGetter, entity);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.getStateForPlacementSavedData(this.defaultBlockState(), placeContext);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.onPlaceSavedData(level, pos);
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        this.onRemoveSavedData(level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockPos posBelow = pos.below();
        BlockState stateBelow = levelReader.getBlockState(posBelow);

        return stateBelow.isSolidRender(levelReader, pos) || state.getBlock() instanceof FarmBlock;
    }
}