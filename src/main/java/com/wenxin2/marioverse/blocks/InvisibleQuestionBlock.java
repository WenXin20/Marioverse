package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class InvisibleQuestionBlock extends QuestionBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<InvisibleQuestionBlock> CODEC = simpleCodec(InvisibleQuestionBlock::new);
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public InvisibleQuestionBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(EMPTY, Boolean.TRUE)
                .setValue(INVISIBLE, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @NotNull
    @Override
    protected MapCodec<? extends InvisibleQuestionBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(EMPTY, INVISIBLE, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity() instanceof Player player) {
            if ((player.hasPermissions(1) && player.isCreative()) || !state.getValue(INVISIBLE)
                    || (!player.isCreative() && !player.isSpectator() && ConfigRegistry.SELECT_INVISIBLE_QUESTION.get())
                    || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem
                    || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DebugStickItem
                    || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock() instanceof InvisibleQuestionBlock) {
                return Shapes.block();
            }
        }
        // Shapes.empty() causes a crash, use a tiny bounding box instead
        return Shapes.box(8, 8, 8, 8.00001, 8.00001, 8.00001);
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (!state.getValue(INVISIBLE))
            return Shapes.block();
        else if (state.getValue(INVISIBLE) && collisionContext instanceof EntityCollisionContext entityCollisionContext) {
            if (entityCollisionContext.getEntity() != null
                    && entityCollisionContext.getEntity().getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)) {
                if (entityCollisionContext.getEntity().getY() + entityCollisionContext.getEntity().getBbHeight() < pos.getY()) {
                    return Shapes.block();
                }
            }
        }
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());

        return this.defaultBlockState().setValue(EMPTY, Boolean.TRUE).setValue(INVISIBLE, Boolean.FALSE)
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof QuestionBlockEntity questionBE && ConfigRegistry.REDSTONE_OPENS_QUESTION.get()) {
            boolean isPowered = world.hasNeighborSignal(pos);
            if (isPowered && !state.getValue(EMPTY) && !questionBE.isLastPowered()) {
                ItemStack storedItem = questionBE.getTheItem();

                if (!storedItem.isEmpty()) {
                    if (!world.isClientSide)
                        this.spawnFromQuestionBlock(world, pos, storedItem, null, Boolean.FALSE, Boolean.TRUE);

                    if (state.hasProperty(InvisibleQuestionBlock.INVISIBLE))
                        world.setBlock(pos, state.setValue(INVISIBLE, Boolean.FALSE), 3);

                    QuestionBlock.playSounds(world, pos, storedItem);
                    questionBE.splitTheItem(1);
                    questionBE.setChanged();
                }

                if (storedItem.isEmpty())
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);

                if (questionBE.getLootTable() != null)
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE).setValue(INVISIBLE, Boolean.TRUE), 3);
            }
            questionBE.setLastPowered(isPowered);
        }
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof QuestionBlockEntity questionBE && !heldItem.is(TagRegistry.CANNOT_PLACE_IN_QUESTION_BLOCKS)) {
            ItemStack blockStack = questionBE.getTheItem();

            if (world.isClientSide) {
                return ItemInteractionResult.CONSUME;
            } else {
                if (!heldItem.isEmpty()
                        && (ConfigRegistry.QUESTION_ADD_ITEMS.get() || player.isCreative())
                        && (blockStack.isEmpty() || ItemStack.isSameItemSameComponents(heldItem, blockStack)
                        && blockStack.getCount() < blockStack.getMaxStackSize())) {
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    ItemStack itemstack = stack.consumeAndReturn(1, player);

                    float f;
                    if (questionBE.isEmpty()) {
                        questionBE.setTheItem(itemstack);
                        f = (float) itemstack.getCount() / (float) itemstack.getMaxStackSize();
                    } else {
                        blockStack.grow(1);
                        f = (float) blockStack.getCount() / (float) blockStack.getMaxStackSize();
                    }
                    world.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1.0F, 0.7F + 0.5F * f);

                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE).setValue(INVISIBLE, Boolean.TRUE), 3);
                    questionBE.setChanged();
                    world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

                    return ItemInteractionResult.SUCCESS;
                } else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        } else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());

        if (world.getBlockEntity(pos) instanceof QuestionBlockEntity questionBE) {
            ItemStack blockStack = questionBE.getTheItem();

            if ((heldItem.isEmpty() || !ItemStack.isSameItemSameComponents(heldItem, blockStack))
                    && (ConfigRegistry.QUESTION_REMOVE_ITEMS.get() || player.isCreative())
                    && !state.getValue(EMPTY)) {
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

                ItemStack storedItem = questionBE.getTheItem();

                if (!storedItem.isEmpty()) {
                    if (!world.isClientSide)
                        this.spawnFromQuestionBlock(world, pos, storedItem, null, Boolean.FALSE, Boolean.TRUE);

                    if (state.is(BlockTags.GUARDED_BY_PIGLINS))
                        PiglinAi.angerNearbyPiglins(player, false);

                    if (state.hasProperty(InvisibleQuestionBlock.INVISIBLE))
                        world.setBlock(pos, state.setValue(INVISIBLE, Boolean.FALSE), 3);

                    QuestionBlock.playSounds(world, pos, storedItem);
                    questionBE.splitTheItem(1);
                    questionBE.setChanged();
                }

                if (storedItem.isEmpty())
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE).setValue(INVISIBLE, Boolean.FALSE), 3);

                return InteractionResult.SUCCESS;
            } else return InteractionResult.PASS;
        } else return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        Player nearestPlayer = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0D, false);
        if (nearestPlayer != null) {
            if (state.getValue(INVISIBLE) && (nearestPlayer.isCreative() && nearestPlayer.hasPermissions(1)
                    || nearestPlayer.isSpectator() && nearestPlayer.hasPermissions(1))) {
                world.addParticle(new ItemParticleOption(ParticleRegistry.NO_MOVEMENT_ITEM.get(), this.asItem().getDefaultInstance()),
                        x + 0.5, y + 0.5, z + 0.5, 0, 0, 0);
            }
        }
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
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return state.getFluidState().isEmpty() && state.getValue(INVISIBLE);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
        return (neighborState.getBlock() instanceof InvisibleQuestionBlock
                && state.getValue(INVISIBLE) && neighborState.getValue(INVISIBLE))
                || super.skipRendering(state, neighborState, direction);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(INVISIBLE))
            return 1.0F;
        else return state.isCollisionShapeFullBlock(blockGetter, pos) ? 0.2F : 1.0F;
    }
}