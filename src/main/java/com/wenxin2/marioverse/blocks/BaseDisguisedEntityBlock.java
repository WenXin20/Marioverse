package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.DisguisedBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseDisguisedEntityBlock extends BaseEntityBlock {
    public static final MapCodec<BaseDisguisedEntityBlock> CODEC = simpleCodec(BaseDisguisedEntityBlock::new);
    public static final BooleanProperty DISGUISED = BlockStatePropertyRegistry.DISGUISED;

    @NotNull
    @Override
    public MapCodec<BaseDisguisedEntityBlock> codec() {
        return CODEC;
    }

    public BaseDisguisedEntityBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DISGUISED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISGUISED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(DISGUISED)) {
            if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir()) {
                    if (blockGetter instanceof LevelAccessor levelAccessor)
                        disguiseState = Block.updateFromNeighbourShapes(disguiseState, levelAccessor, pos);
                    return disguiseState.getShape(blockGetter, pos, context);
                }
            }
        }
        return Shapes.block();
    }

    @NotNull
    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter blockGetter, BlockPos pos, Direction side,
                                    @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        if (state.getValue(DISGUISED)) {
            if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir())
                    return disguiseState.getAppearance(blockGetter, pos, side, queryState, queryPos);
            }
        }
        return super.getAppearance(state, blockGetter, pos, side, queryState, queryPos);
    }

    @NotNull
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(DISGUISED)) {
            if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir())
                    return disguiseState.getCollisionShape(blockGetter, pos, context);
            }
        }
        return super.getCollisionShape(state, blockGetter, pos, context);
    }

    @NotNull
    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(DISGUISED)) {
            if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir())
                    return disguiseState.getVisualShape(blockGetter, pos, context);
            }
        }
        return super.getVisualShape(state, blockGetter, pos, context);
    }

    @NotNull
    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED)) {
            if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState.getBlock() instanceof CrossCollisionBlock
                        || disguiseState.getBlock() instanceof FenceBlock
                        || disguiseState.getBlock() instanceof WallBlock)
                    return Shapes.block();
                if (!disguiseState.isAir())
                    return disguiseState.getBlockSupportShape(blockGetter, pos);
            }
        }
        return super.getBlockSupportShape(state, blockGetter, pos);
    }

    @NotNull
    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED)) {
            if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir())
                    return disguiseState.getInteractionShape(blockGetter, pos);
            }
        }
        return super.getInteractionShape(state, blockGetter, pos);
    }

    @NotNull
    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED)) {
            if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir())
                    return disguiseState.getOcclusionShape(blockGetter, pos);
            }
        }
        return super.getOcclusionShape(state, blockGetter, pos);
    }

    @Override
    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED)) {
            if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir())
                    return disguiseState.isCollisionShapeFullBlock(blockGetter, pos);
            }
        }
        return super.isCollisionShapeFullBlock(state, blockGetter, pos);
    }

    @Override
    protected void updateIndirectNeighbourShapes(BlockState state, LevelAccessor levelAccessor, BlockPos pos, int flags, int recursionLeft) {
        if (state.getValue(DISGUISED)) {
            if (levelAccessor.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir())
                    disguiseState.updateIndirectNeighbourShapes(levelAccessor, pos, flags, recursionLeft);
            }
        }
        super.updateIndirectNeighbourShapes(state, levelAccessor, pos, flags, recursionLeft);
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(DISGUISED)) {
            if (levelAccessor.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir()) {
                    if (levelAccessor.getBlockEntity(neighborPos) instanceof DisguisedBlockEntity neighborBE) {
                        BlockState neighborDisguise = neighborBE.getDisguiseState();
                        if (neighborDisguise != null && !neighborDisguise.isAir())
                            neighborState = neighborDisguise;
                    }

                    BlockState updatedState = disguiseState.updateShape(direction, neighborState, levelAccessor, pos, neighborPos);

                    if (updatedState != disguiseState) {
                        blockEntity.setDisguiseState(updatedState);
                        blockEntity.requestModelDataUpdate();
                        if (levelAccessor instanceof Level level)
                            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), Block.UPDATE_CLIENTS);
                    }
                    return state;
                }
            }
        }
        return state;
    }

    @NotNull
    @Override
    public MapColor getMapColor(BlockState state, BlockGetter blockGetter, BlockPos pos, MapColor defaultColor) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguiseState().getMapColor(blockGetter, pos);
        return super.getMapColor(state, blockGetter, pos, defaultColor);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED)) {
            if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir())
                    return disguiseState.getLightBlock(blockGetter, pos);
            }
        }
        return super.getLightBlock(state, blockGetter, pos);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguiseState().getLightEmission(blockGetter, pos);
        return super.getLightEmission(state, blockGetter, pos);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguiseState().getShadeBrightness(blockGetter, pos);
        return super.getShadeBrightness(state, blockGetter, pos);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguiseState().propagatesSkylightDown(blockGetter, pos);
        return super.propagatesSkylightDown(state, blockGetter, pos);
    }

    @NotNull
    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        if (state.getValue(DISGUISED) && level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguiseState().getSoundType(level, pos, entity);
        return super.getSoundType(state, level, pos, entity);
    }

    @NotNull
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (player.isShiftKeyDown() && state.getValue(DISGUISED)
                && level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguiseState().getCloneItemStack(target, level, pos, player);
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Nullable
    @Override
    public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        if (state.getValue(DISGUISED) && level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguiseState().getBeaconColorMultiplier(level, pos, beaconPos);
        return super.getBeaconColorMultiplier(state, level, pos, beaconPos);
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction direction) {
        if (state.getValue(DISGUISED) && level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguiseState().hidesNeighborFace(level, pos, neighborState, direction);
        return super.hidesNeighborFace(level, pos, state, neighborState, direction);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(DISGUISED)) {
            if (level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
                BlockState disguiseState = blockEntity.getDisguiseState();
                if (disguiseState != null && !disguiseState.isAir())
                    blockEntity.getDisguiseState().getBlock().animateTick(state, level, pos, random);
            }
        }
        else super.animateTick(state, level, pos, random);
    }
}