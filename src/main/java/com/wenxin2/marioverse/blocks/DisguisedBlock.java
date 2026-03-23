package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.blocks.entities.DisguisedBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisguisedBlock extends BaseEntityBlock {
    public static final MapCodec<DisguisedBlock> CODEC = simpleCodec(DisguisedBlock::new);
    public static final BooleanProperty DISGUISED = BlockStatePropertyRegistry.DISGUISED;

    @NotNull
    @Override
    public MapCodec<DisguisedBlock> codec() {
        return CODEC;
    }

    public DisguisedBlock(Properties properties) {
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

    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!state.getValue(DISGUISED))
            return null;

        return (lvl, pos, st, be) -> {
            if (be instanceof DisguisedBlockEntity disguisedBE) {
                BlockState disguiseState = disguisedBE.getDisguiseState();
                BlockEntity disguiseBE = disguisedBE.getDisguiseBlockEntity();

                if (disguiseState != null && disguiseBE != null) {
                    Block block = disguiseState.getBlock();

                    if (block instanceof EntityBlock entityBlock) {
                        BlockEntityTicker ticker = entityBlock.getTicker(lvl, disguiseState, disguiseBE.getType());

                        if (ticker != null)
                            ticker.tick(lvl, pos, disguiseState, disguiseBE);
                    }
                }
            }
        };
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
                if (disguiseState != null && !disguiseState.isAir())
                    return disguiseState.getShape(blockGetter, pos, context);
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

                    if (updatedState != disguiseState && updatedState.getBlock() != Blocks.AIR) {
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
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
            BlockState disguiseState = blockEntity.getDisguiseState();
            if (disguiseState != null && !disguiseState.isAir())
                return blockEntity.getDisguiseState().getLightEmission(blockGetter, pos);
        }
        return super.getLightEmission(state, blockGetter, pos);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
            BlockState disguiseState = blockEntity.getDisguiseState();
            if (disguiseState != null && !disguiseState.isAir())
                return blockEntity.getDisguiseState().getShadeBrightness(blockGetter, pos);
        }
        return super.getShadeBrightness(state, blockGetter, pos);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
            BlockState disguiseState = blockEntity.getDisguiseState();
            if (disguiseState != null && !disguiseState.isAir())
                return blockEntity.getDisguiseState().propagatesSkylightDown(blockGetter, pos);
        }
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

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.isCreative() && level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) { // TODO: Gui edit
            BlockState disguiseState = blockEntity.getDisguiseState();
            if (disguiseState != null && !disguiseState.isAir() && !(disguiseState.getBlock() instanceof BlockSpawnerBlock))
                return blockEntity.getDisguiseState().useItemOn(stack, level, player, hand, hitResult);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.isCreative() && level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) { // TODO: Gui edit
            BlockState disguiseState = blockEntity.getDisguiseState();
            if (disguiseState != null && !disguiseState.isAir() && !(disguiseState.getBlock() instanceof BlockSpawnerBlock))
                return blockEntity.getDisguiseState().useWithoutItem(level, player, hitResult);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if ((player == null || !player.isCreative())
                && level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
            BlockState disguiseState = blockEntity.getDisguiseState();

            if (disguiseState != null && !disguiseState.isAir()) {
                BlockState modifiedState = disguiseState.getBlock().getToolModifiedState(disguiseState, context, itemAbility, simulate);

                if (modifiedState != null && !simulate) {
                    blockEntity.setDisguiseState(modifiedState);
                    blockEntity.setItem(0, modifiedState.getBlock().asItem().getDefaultInstance());
                    blockEntity.requestModelDataUpdate();
                    blockEntity.setChanged();
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                }
                return state;
            }
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) { // TODO: Gui edit
        if (state.getValue(DISGUISED) && level.getBlockEntity(pos) instanceof BlockSpawnerBlockEntity blockEntity
                && blockEntity.isInteractable() == 0)
            blockEntity.getDisguiseState().entityInside(level, pos, entity);
        else if (state.getValue(DISGUISED) && level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity
                && !(level.getBlockEntity(pos) instanceof BlockSpawnerBlockEntity))
            blockEntity.getDisguiseState().entityInside(level, pos, entity);
        else super.entityInside(state, level, pos, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
            BlockState disguiseState = blockEntity.getDisguiseState();
            if (disguiseState != null && !disguiseState.isAir() && !(disguiseState.getBlock() instanceof BlockSpawnerBlock))
                blockEntity.getDisguiseState().getBlock().stepOn(level, pos, blockEntity.getDisguiseState(), entity);
        }
        else super.stepOn(level, pos, state, entity);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
            BlockState disguiseState = blockEntity.getDisguiseState();
            if (disguiseState != null && !disguiseState.isAir() && !(disguiseState.getBlock() instanceof BlockSpawnerBlock))
                blockEntity.getDisguiseState().getBlock().fallOn(level, blockEntity.getDisguiseState(), pos, entity, fallDistance);
        }
        else super.fallOn(level, state, pos, entity, fallDistance);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        BlockPos pos = entity.getOnPos();

        if (blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
            BlockState disguiseState = blockEntity.getDisguiseState();
            if (disguiseState != null && !disguiseState.isAir() && !(disguiseState.getBlock() instanceof BlockSpawnerBlock))
                blockEntity.getDisguiseState().getBlock().updateEntityAfterFallOn(blockGetter, entity);
        }
        else super.updateEntityAfterFallOn(blockGetter, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity) {
            BlockState disguiseState = blockEntity.getDisguiseState();
            if (disguiseState != null && !disguiseState.isAir() && !(disguiseState.getBlock() instanceof BlockSpawnerBlock))
                disguiseState.getBlock().animateTick(disguiseState, level, pos, random);
        }
        super.animateTick(state, level, pos, random);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof BlockSpawnerBlockEntity blockEntity
                && blockEntity.isUnbreakable() == 0)
            return blockEntity.getDisguiseState().getDestroyProgress(player, blockGetter, pos);
        else if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity
                && !(blockGetter.getBlockEntity(pos) instanceof BlockSpawnerBlockEntity))
            return blockEntity.getDisguiseState().getDestroyProgress(player, blockGetter, pos);
        else return super.getDestroyProgress(state, player, blockGetter, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter blockGetter, BlockPos pos, Explosion explosion) { // TODO: Gui edit
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguiseState().getExplosionResistance(blockGetter, pos, explosion);
        else return super.getExplosionResistance(state, blockGetter, pos, explosion);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity
                && !blockEntity.getInventory().getStackInSlot(0).isEmpty())
            level.setBlock(pos, state.setValue(DISGUISED, true), 3);

        super.setPlacedBy(level, pos, state, entity, stack);
    }
}