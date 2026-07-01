package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.SoundRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class QuestionPanelBlock extends PanelBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<QuestionPanelBlock> CODEC = simpleCodec(QuestionPanelBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    @NotNull
    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public QuestionPanelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACE, AttachFace.FLOOR).setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACE, FACING, POWERED, WATERLOGGED);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        int power = this.getSignalForState(state);
        if (power == 0)
            this.checkPressed(entity, world, pos, state, power);
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        int power = this.getSignalForState(state);
        if (power > 0)
            this.checkPressed(null, serverWorld, pos, state, power);
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState neighborState, boolean isMoving) {
        if (!isMoving && !state.is(neighborState.getBlock())) {
            if (this.getSignalForState(state) > 0)
                this.updateNeighbours(world, pos);

            super.onRemove(state, world, pos, neighborState, isMoving);
        }
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction) {
        return this.getSignalForState(state);
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction) {
        return direction == Direction.UP ? this.getSignalForState(state) : 0;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    protected int getPressedTime() {
        return 40;
    }

    protected int getSignalForState(BlockState state) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    protected BlockState setSignalForState(BlockState state, int signalStrength) {
        return state.setValue(POWERED, signalStrength > 0);
    }

    protected int getSignalStrength(Level world, BlockPos pos) {
        return getEntityCount(world, getTouchAABB(world.getBlockState(pos)).move(pos), LivingEntity.class) > 0 ? 15 : 0;
    }

    protected static int getEntityCount(Level world, AABB aabb, Class<? extends Entity> entityClass) {
        return world.getEntitiesOfClass(entityClass, aabb, EntitySelector.NO_SPECTATORS.and(entity -> !entity.isIgnoringBlockTriggers())).size();
    }

    protected void updateNeighbours(Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.below(), this);
    }

    private void checkPressed(@Nullable Entity entity, Level level, BlockPos pos, BlockState state, int power) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        int signalStrength = this.getSignalStrength(level, pos);
        boolean isPowered = power > 0;
        boolean isSignaled = signalStrength > 0;
        if (power != signalStrength) {
            BlockState stateSignal = this.setSignalForState(state, signalStrength);
            level.setBlock(pos, stateSignal, 2);
            this.updateNeighbours(level, pos);
            level.setBlocksDirty(pos, state, stateSignal);
        }

        if (!isSignaled && isPowered) {
            level.playSound(null, pos, SoundRegistry.QUESTION_PANEL_DEACTIVATED.get(),
                    SoundSource.BLOCKS, 1.0F, pitch);
            level.gameEvent(entity, GameEvent.BLOCK_DEACTIVATE, pos);
        } else if (isSignaled && !isPowered) {
            level.playSound(null, pos, SoundRegistry.QUESTION_PANEL_ACTIVATED.get(),
                    SoundSource.BLOCKS, 1.0F, pitch);
            level.gameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);
        }

        if (isSignaled)
            level.scheduleTick(new BlockPos(pos), this, this.getPressedTime());
    }

    private static AABB getTouchAABB(BlockState state) {
        AttachFace face = state.getValue(FACE);
        Direction facing = state.getValue(FACING);

        switch (face) {
            case FLOOR:
                return FLOOR.bounds();
            case CEILING:
                return CEILING.bounds();
            case WALL:
                switch (facing) {
                    case NORTH: return NORTH.bounds();
                    case SOUTH: return SOUTH.bounds();
                    case WEST:  return WEST.bounds();
                    case EAST:  return EAST.bounds();
                }
        }
        return FLOOR.bounds();
    }
}
