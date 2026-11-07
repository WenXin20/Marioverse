package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScrewBlock extends DirectionalBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<ScrewBlock> CODEC = simpleCodec(ScrewBlock::new);
    public static final BooleanProperty TOP = BlockStatePropertyRegistry.TOP;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty HEIGHT = BlockStatePropertyRegistry.HEIGHT_8;
    public static final int MAX_HEIGHT = 8;

    private static final Map<Direction, VoxelShape> SCREW_SHAPES = Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction facing : Direction.values())
            map.put(facing, ScrewBlock.orientedBox(facing, 6, 0, 6, 10, 16, 10));
    });

    private static final Map<Direction, VoxelShape[]> TOP_SHAPES = Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction facing : Direction.values()) {
            VoxelShape[] shapes = new VoxelShape[MAX_HEIGHT + 1];
            shapes[0] = ScrewBlock.orientedBox(facing, 2.05, -0.95, 2.05, 13.95, 0.95, 13.95);

            for (int height = 1; height <= MAX_HEIGHT; height++) {
                int headTop = ScrewBlock.headTop(height);
                VoxelShape head = ScrewBlock.orientedBox(facing, 2, headTop - 2, 2, 14, headTop, 14);
                shapes[height] = headTop <= 2 ? head
                        : Shapes.or(head, ScrewBlock.orientedBox(facing, 6, 0, 6, 10, headTop - 2, 10));
            }
            map.put(facing, shapes);
        }
    });

    @NotNull
    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public ScrewBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, true)
                .setValue(FACING, Direction.UP).setValue(HEIGHT, MAX_HEIGHT).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HEIGHT, TOP, WATERLOGGED);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return state.getValue(TOP) ? TOP_SHAPES.get(facing)[state.getValue(HEIGHT)] : SCREW_SHAPES.get(facing);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = this.defaultBlockState().setValue(FACING, context.getClickedFace())
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);

        return ScrewBlock.hasSupport(state, context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));

        if (direction == state.getValue(FACING) && !state.getValue(TOP) && !ScrewBlock.isStackedScrew(state, neighborState))
            return state.setValue(TOP, true);

        return super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos supportPos = pos.relative(state.getValue(FACING).getOpposite());
        BlockState supportState = level.getBlockState(supportPos);
        boolean sneaking = placer != null && placer.isShiftKeyDown();

        if (!level.isClientSide && !sneaking && ScrewBlock.isStackedScrew(supportState, state))
            level.setBlock(supportPos, supportState.setValue(TOP, false), Block.UPDATE_ALL);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof BlockItem || ScrewBlock.isModifiedByItem(stack, state, player, hand, hitResult))
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos topPos = ScrewBlock.findTopScrew(level, pos, state);
        BlockState topState = level.getBlockState(topPos);

        if (!topState.getValue(TOP))
            return InteractionResult.PASS;

        boolean changed = player.isSecondaryUseActive()
                ? ScrewBlock.unscrew(level, topPos, topState, player)
                : ScrewBlock.screw(level, topPos, topState, player);

        if (!changed)
            return InteractionResult.PASS;

        if (!level.isClientSide) {
            level.playSound(null, topPos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS,
                    1.0F, 0.9F + level.random.nextFloat() * 0.2F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, topPos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (!state.getValue(TOP) || ScrewBlock.isStackedOnScrew(level, pos, state))
            return 0;
        return (MAX_HEIGHT - state.getValue(HEIGHT)) * 15 / MAX_HEIGHT;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return direction == state.getValue(FACING) ? state.getSignal(level, pos, direction) : 0;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            Direction facing = state.getValue(FACING);
            BlockPos frontPos = pos.relative(facing);
            BlockState frontState = level.getBlockState(frontPos);

            level.updateNeighborsAt(pos.relative(facing.getOpposite()), this);
            if (!level.isClientSide && !state.getValue(TOP) && ScrewBlock.isStackedScrew(state, frontState))
                level.scheduleTick(frontPos, frontState.getBlock(), 1);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState behindState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
        if (!ScrewBlock.isStackedScrew(behindState, state))
            level.destroyBlock(pos, true);
    }

    private static BlockPos findTopScrew(BlockGetter level, BlockPos pos, BlockState state) {
        BlockPos topPos = pos;
        BlockState topState = state;

        while (!topState.getValue(TOP)) {
            BlockPos nextPos = topPos.relative(topState.getValue(FACING));
            BlockState nextState = level.getBlockState(nextPos);
            if (!ScrewBlock.isStackedScrew(topState, nextState))
                break;
            topPos = nextPos;
            topState = nextState;
        }
        return topPos;
    }

    private static boolean screw(Level level, BlockPos topPos, BlockState topState, Player player) {
        int height = topState.getValue(HEIGHT);

        if (height > 0) {
            if (!level.isClientSide)
                level.setBlock(topPos, topState.setValue(HEIGHT, height - 1), Block.UPDATE_ALL);
            return true;
        }

        BlockPos belowPos = topPos.relative(topState.getValue(FACING).getOpposite());
        BlockState belowState = level.getBlockState(belowPos);
        if (!ScrewBlock.isStackedScrew(belowState, topState) || belowState.getValue(TOP))
            return false;

        if (!level.isClientSide) {
            level.removeBlock(topPos, false);
            if (!player.hasInfiniteMaterials())
                player.getInventory().placeItemBackInInventory(new ItemStack(topState.getBlock()));
            level.setBlock(belowPos, belowState.setValue(TOP, true).setValue(HEIGHT, MAX_HEIGHT - 1), Block.UPDATE_ALL);
        }
        return true;
    }

    private static boolean unscrew(Level level, BlockPos topPos, BlockState topState, Player player) {
        int height = topState.getValue(HEIGHT);
        Direction facing = topState.getValue(FACING);

        if (height < MAX_HEIGHT) {
            if (!level.isClientSide) {
                ScrewBlock.pushEntities(level, topPos, facing, ScrewBlock.headTop(height), ScrewBlock.headTop(height + 1));
                level.setBlock(topPos, topState.setValue(HEIGHT, height + 1), Block.UPDATE_ALL);
            }
            return true;
        }

        ItemStack screwStack = ScrewBlock.findScrewItem(player, topState.getBlock());
        if (screwStack.isEmpty() && !player.hasInfiniteMaterials())
            return false;

        BlockPos newPos = topPos.relative(facing);
        FluidState fluidState = level.getFluidState(newPos);
        Block screwBlock = screwStack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock() : topState.getBlock();
        BlockState newState = screwBlock.defaultBlockState().setValue(FACING, facing).setValue(HEIGHT, 1)
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);

        if (!level.isInWorldBounds(newPos) || !level.getBlockState(newPos).canBeReplaced()
                || !ScrewBlock.hasSupport(newState, level, newPos))
            return false;

        if (!level.isClientSide) {
            ScrewBlock.pushEntities(level, topPos, facing, ScrewBlock.headTop(MAX_HEIGHT), 16 + ScrewBlock.headTop(1));
            level.setBlock(newPos, newState, Block.UPDATE_ALL);
            level.setBlock(topPos, topState.setValue(TOP, false), Block.UPDATE_ALL);
            if (!player.hasInfiniteMaterials())
                screwStack.shrink(1);
        }
        return true;
    }

    private static void pushEntities(Level level, BlockPos pos, Direction facing, int fromTop, int toTop) {
        AABB area = ScrewBlock.orientedBox(facing, 2, fromTop, 2, 14, toTop, 14).bounds().move(pos);
        Vec3 offset = Vec3.atLowerCornerOf(facing.getNormal()).scale((toTop - fromTop) / 16.0);

        for (Entity entity : level.getEntities((Entity) null, area, EntitySelector.NO_SPECTATORS)) {
            if (level.noCollision(entity, entity.getBoundingBox().move(offset)))
                entity.teleportTo(entity.getX() + offset.x, entity.getY() + offset.y, entity.getZ() + offset.z);
        }
    }

    private static ItemStack findScrewItem(Player player, Block preferredScrew) {
        Inventory inventory = player.getInventory();
        ItemStack fallback = ItemStack.EMPTY;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ScrewBlock) {
                if (blockItem.getBlock() == preferredScrew)
                    return stack;
                if (fallback.isEmpty())
                    fallback = stack;
            }
        }
        return fallback;
    }

    private static boolean isModifiedByItem(ItemStack stack, BlockState state, Player player,
                                            InteractionHand hand, BlockHitResult hitResult) {
        UseOnContext context = new UseOnContext(player, hand, hitResult);
        return state.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, true) != null
                || state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, true) != null
                || (stack.getItem() instanceof HoneycombItem && HoneycombItem.getWaxed(state).isPresent());
    }

    private static boolean hasSupport(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        VoxelShape supportFace = level.getBlockState(supportPos).getBlockSupportShape(level, supportPos).getFaceShape(facing);
        return !Shapes.joinIsNotEmpty(supportFace, SCREW_SHAPES.get(facing), BooleanOp.ONLY_SECOND);
    }

    private static boolean isStackedOnScrew(BlockGetter level, BlockPos pos, BlockState state) {
        BlockState behindState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
        return ScrewBlock.isStackedScrew(behindState, state) && !behindState.getValue(TOP);
    }

    private static int headTop(int height) {
        return height == 0 ? 1 : height * 2;
    }

    private static boolean isStackedScrew(BlockState below, BlockState above) {
        return below.getBlock() instanceof ScrewBlock && above.getBlock() instanceof ScrewBlock
                && below.getValue(FACING) == above.getValue(FACING);
    }

    private static VoxelShape orientedBox(Direction facing, double minX, double minY, double minZ,
                                          double maxX, double maxY, double maxZ) {
        return switch (facing) {
            case UP -> Block.box(minX, minY, minZ, maxX, maxY, maxZ);
            case DOWN -> Block.box(minX, 16 - maxY, minZ, maxX, 16 - minY, maxZ);
            case NORTH -> Block.box(minX, minZ, 16 - maxY, maxX, maxZ, 16 - minY);
            case SOUTH -> Block.box(minX, minZ, minY, maxX, maxZ, maxY);
            case WEST -> Block.box(16 - maxY, minX, minZ, 16 - minY, maxX, maxZ);
            case EAST -> Block.box(minY, minX, minZ, maxY, maxX, maxZ);
        };
    }
}