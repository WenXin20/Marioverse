package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TntBlock;
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
                    || (((player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock() instanceof InvisibleQuestionBlock)
                    || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem
                    || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DebugStickItem))) {
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
            if (entityCollisionContext.getEntity() instanceof Player player) {
                if (player.getY() + player.getBbHeight() < pos.getY()) {
                    return Shapes.block();
                }
            }
        }
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());

        return this.defaultBlockState().setValue(EMPTY, Boolean.TRUE).setValue(INVISIBLE, Boolean.TRUE)
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
                        this.spawnEntity(world, pos, storedItem);

                    if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
                        this.playCoinSound(world, pos);
                    else if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
                        this.playPrimedTNTSound(world, pos);
                    else if (storedItem.getItem() instanceof BasePowerUpItem)
                        this.playPowerUpSound(world, pos);
                    else if (storedItem.getItem() instanceof SpawnEggItem)
                        this.playMobSound(world, pos);
                    else if (storedItem.getItem() instanceof ArmorStandItem)
                        this.playArmorStandSound(world, pos);
                    else if (storedItem.getItem() instanceof BoatItem)
                        this.playBoatSound(world, pos);
                    else if (storedItem.getItem() instanceof MinecartItem)
                        this.playMinecartSound(world, pos);
                    else this.playItemSound(world, pos);

                    questionBlockEntity.removeItems();
                    questionBlockEntity.setChanged();
                }

                if (storedItem.isEmpty()) {
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE).setValue(INVISIBLE, Boolean.FALSE), 3);
                }

                Player nearestPlayer = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0D, false);
                if (questionBlockEntity.getLootTable() != null && nearestPlayer != null) {
                    this.unpackLootTable(nearestPlayer, questionBlockEntity);
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE).setValue(INVISIBLE, Boolean.TRUE), 3);
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

        if (blockEntity instanceof QuestionBlockEntity questionBlockEntity && !heldItem.is(TagRegistry.QUESTION_BLOCK_ITEM_BLACKLIST)) {
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

                if (!storedItem.isEmpty()) {
                    if (!world.isClientSide)
                        this.spawnEntity(world, pos, storedItem);

                    if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
                        this.playCoinSound(world, pos);
                    else if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
                        this.playPrimedTNTSound(world, pos);
                    else if (storedItem.getItem() instanceof BasePowerUpItem)
                        this.playPowerUpSound(world, pos);
                    else if (storedItem.getItem() instanceof SpawnEggItem)
                        this.playMobSound(world, pos);
                    else if (storedItem.getItem() instanceof ArmorStandItem)
                        this.playArmorStandSound(world, pos);
                    else if (storedItem.getItem() instanceof BoatItem)
                        this.playBoatSound(world, pos);
                    else if (storedItem.getItem() instanceof MinecartItem)
                        this.playMinecartSound(world, pos);
                    else this.playItemSound(world, pos);

                    questionBlockEntity.removeItems();
                    questionBlockEntity.setChanged();
                }

                if (storedItem.isEmpty()) {
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE).setValue(INVISIBLE, Boolean.FALSE), 3);
                }
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
            InteractionHand hand = nearestPlayer.getUsedItemHand();
            if (state.getValue(INVISIBLE) && (nearestPlayer.isCreative() || nearestPlayer.isSpectator()
                    || (nearestPlayer.getItemInHand(hand).getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof InvisibleQuestionBlock))) {
                world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, state),
                        x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
            }
        }
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));
        }

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return state.getFluidState().isEmpty() && state.getValue(INVISIBLE);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        if (state.getValue(INVISIBLE))
            return RenderShape.INVISIBLE;
        else return RenderShape.MODEL;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(INVISIBLE))
            return 1.0F;
        else return state.isCollisionShapeFullBlock(blockGetter, pos) ? 0.2F : 1.0F;
    }
}