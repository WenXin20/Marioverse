package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class InvisibleQuestionBlock extends QuestionBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public InvisibleQuestionBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(EMPTY, Boolean.TRUE)
                .setValue(INVISIBLE, Boolean.TRUE).setValue(WATERLOGGED, Boolean.FALSE));
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
        if (blockEntity instanceof QuestionBlockEntity questionBlockEntity && ConfigRegistry.REDSTONE_OPENS_QUESTION.get()) {
            boolean isPowered = world.hasNeighborSignal(pos);
            if (isPowered && !state.getValue(EMPTY) && !questionBlockEntity.isLastPowered()) {
                ItemStack storedItem = questionBlockEntity.getItems().getFirst();

                if (!storedItem.isEmpty()) {
                    if (!world.isClientSide)
                        this.spawnFromQuestionBlock(world, pos, storedItem, null, Boolean.FALSE, Boolean.TRUE);

                    this.playSounds(world, pos, storedItem);
                    questionBlockEntity.removeItems();
                    questionBlockEntity.setChanged();
                }

                if (storedItem.isEmpty()) {
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
                }

                if (state.getValue(InvisibleQuestionBlock.INVISIBLE)) {
                    world.setBlock(pos, state.setValue(INVISIBLE, Boolean.FALSE), 3);
                }

                Player nearestPlayer = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0D, false);
                if (questionBlockEntity.getLootTable() != null && nearestPlayer != null) {
                    this.unpackLootTable(nearestPlayer, questionBlockEntity);
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
                }
            }
            questionBlockEntity.setLastPowered(isPowered);
        }
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof QuestionBlockEntity questionBlockEntity && !heldItem.is(TagRegistry.CANNOT_PLACE_IN_QUESTION_BLOCKS)) {
            ItemStack blockStack = questionBlockEntity.getStackInSlot();

            if (questionBlockEntity.getLootTable() != null)
                this.unpackLootTable(player, questionBlockEntity);

            if (!heldItem.isEmpty() && questionBlockEntity.getLootTable() == null
                    && (ConfigRegistry.QUESTION_ADD_ITEMS.get() || player.isCreative())
                    && (!questionBlockEntity.hasItems() || ItemStack.isSameItemSameComponents(heldItem, blockStack))) {
                questionBlockEntity.addItem(heldItem);
                world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE).setValue(INVISIBLE, Boolean.TRUE), 3);
                questionBlockEntity.setChanged();
                if(!player.isCreative())
                    stack.shrink(heldItem.getCount());
                return ItemInteractionResult.SUCCESS;
            } else if (heldItem.isEmpty() && (ConfigRegistry.QUESTION_REMOVE_ITEMS.get() || player.isCreative())
                    && !state.getValue(EMPTY)) {
                ItemStack storedItem = questionBlockEntity.getItems().getFirst();

                // Keep above "if (!storedItem.isEmpty())"
                if (state.getValue(InvisibleQuestionBlock.INVISIBLE))
                    world.setBlock(pos, state.setValue(INVISIBLE, Boolean.FALSE), 3);

                if (!storedItem.isEmpty()) {
                    if (!world.isClientSide)
                        this.spawnFromQuestionBlock(world, pos, storedItem, null, Boolean.FALSE, Boolean.TRUE);

                    this.playSounds(world, pos, storedItem);
                    questionBlockEntity.removeItems();
                    questionBlockEntity.setChanged();
                }

                if (storedItem.isEmpty())
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE).setValue(INVISIBLE, Boolean.FALSE), 3);

                return ItemInteractionResult.SUCCESS;
            } else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
                if (state.getBlock() == BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_AMETHYST_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_BLACKSTONE_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_COPPER_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_DEEPSLATE_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_DEEPSLATE_TILE_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_END_STONE_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_MOSSY_STONE_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_MUD_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_NETHER_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_PRISMARINE_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_PURPUR_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_QUARTZ_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_RED_NETHER_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_STONE_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_TUFF_BRICK_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else if (state.getBlock() == BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK.get())
                    world.addParticle(ParticleRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
                else world.addParticle(ParticleRegistry.INVISIBLE_FUNGAL_QUESTION.get(),
                            x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
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

    @NotNull
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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