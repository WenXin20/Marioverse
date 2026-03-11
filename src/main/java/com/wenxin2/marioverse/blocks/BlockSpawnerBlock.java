package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.inventory.QuestionBlockMenu;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSpawnerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<BlockSpawnerBlock> CODEC = simpleCodec(BlockSpawnerBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE =
            Shapes.or(Block.box(0, 0, 0, 16, 16, 16)).optimize();

    @NotNull
    @Override
    public MapCodec<BlockSpawnerBlock> codec() {
        return CODEC;
    }

    public BlockSpawnerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockSpawnerBlockEntity(BlockEntityRegistry.BLOCK_SPAWNER_BLOCK_ENTITY.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityRegistry.BLOCK_SPAWNER_BLOCK_ENTITY.get(), BlockSpawnerBlockEntity::tick);
    }

    @NotNull
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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

        return direction == Direction.DOWN && !this.canSurvive(state, worldAccessor, pos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader worldReader, BlockPos pos) {
        return Block.canSupportCenter(worldReader, pos.below(), Direction.UP)
                || worldReader.getBlockState(pos.below()).is(TagRegistry.SUPPORTS_DANGO_BLOSSOM);
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

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof BlockSpawnerBlockEntity blockEntity) {
            if (stack.getItem() instanceof BlockItem) {
                blockEntity.setItem(0, stack);
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
                return ItemInteractionResult.SUCCESS;
            } else {
                player.openMenu(new SimpleMenuProvider((id, playerInventory, playerIn) ->
                        new BlockSpawnerMenu(id, playerInventory, blockEntity,
                                blockEntity.getDataAccess(), ContainerLevelAccess.create(level, pos)), blockEntity.getDisplayName()));
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        Player nearestPlayer = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0D, false);
        if (nearestPlayer != null) {
            if (world.getBlockEntity(pos) instanceof BlockSpawnerBlockEntity blockEntity
                    && (nearestPlayer.isCreative() && nearestPlayer.hasPermissions(1)
                        || nearestPlayer.isSpectator() && nearestPlayer.hasPermissions(1))) {
                if (blockEntity.getDisguise().getBlock() == Blocks.AIR) {
                    ItemStack stack = blockEntity.getGhostItem();
                    if (!stack.isEmpty())
                        world.addParticle(new ItemParticleOption(ParticleRegistry.NO_MOVEMENT_ITEM.get(),stack),
                                x + 0.5, y + 0.5, z + 0.5, 0, 0, 0);
                }
            }
        }
    }
}