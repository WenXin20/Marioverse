package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.entities.SnowPokeyBodyEntity;
import com.wenxin2.marioverse.entities.SnowPokeyEntity;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HedgeBlock extends Block implements BonemealableBlock, SimpleWaterloggedBlock {
    public static final ResourceKey<LootTable> SNOW_LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, Marioverse.id("gameplay/hedge_snow"));
    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;
    public static final BooleanProperty TOP = BlockStatePropertyRegistry.TOP;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final Direction[] SPREAD_DIRECTIONS =
            { Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    protected static final VoxelShape SHAPE = Shapes.block();
    protected static final VoxelShape SHAPE_TOP = Shapes
            .or(Block.box(0, 0, 0, 16, 10, 16),
                    Block.box(3, 10, 3, 13, 16, 13)).optimize();

    public HedgeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SNOWY, false).setValue(TOP, true).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SNOWY, TOP, WATERLOGGED);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(TOP))
            return SHAPE_TOP;
        return SHAPE;
    }

    @Override
    protected boolean skipRendering(@NotNull BlockState state, @NotNull BlockState neighborState, @NotNull Direction direction) {
        return (neighborState.getBlock() instanceof HedgeBlock && !neighborState.getValue(TOP) && !state.getValue(TOP))
                || super.skipRendering(state, neighborState, direction);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);

        return this.calculateTop(state, context.getLevel(), context.getClickedPos());
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

        state = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (direction == Direction.UP)
            state = this.calculateTop(state, level, pos);

        return state;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        BlockState newState = this.calculateTop(state, level, pos);

        if (newState != state)
            level.setBlock(pos, newState, 3);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockState stateBelow = levelReader.getBlockState(pos.below());
        if (!stateBelow.canBeReplaced())
            return true;
        return super.canSurvive(state, levelReader, pos);
    }

    @NotNull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean snowy = state.getValue(SNOWY);
        Biome biome = level.getBiome(pos).value();
        boolean snowyBiome = biome.coldEnoughToSnow(pos);
        boolean isSnowing = level.isRaining() && biome.getPrecipitationAt(pos) == Biome.Precipitation.SNOW
                && HedgeBlock.isExposedToSky(level, pos);

        if (!snowy && isSnowing && random.nextInt(16) == 0)
            level.setBlockAndUpdate(pos, state.setValue(SNOWY, true));
        else if (snowy && !this.alwaysSnowy() && !isSnowing && !snowyBiome)
            level.setBlockAndUpdate(pos, state.setValue(SNOWY, false));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (!level.isClientSide && !state.getValue(SNOWY) && (entity instanceof SnowGolem
                || entity instanceof SnowPokeyEntity || entity instanceof SnowPokeyBodyEntity))
            level.setBlockAndUpdate(pos, state.setValue(SNOWY, true));
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (state.getValue(SNOWY) && stack.is(ItemTags.SHOVELS)) {
            level.setBlockAndUpdate(pos, state.setValue(SNOWY, false));
            level.playSound(player, pos, SoundEvents.SNOW_BREAK, SoundSource.BLOCKS, 1.0F, pitch);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            if (stack.isDamageableItem())
                stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));

            if (level instanceof ServerLevel serverLevel) {
                LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(SNOW_LOOT_TABLE);
                LootParams lootParams = new LootParams.Builder(serverLevel)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                        .withParameter(LootContextParams.BLOCK_STATE, state)
                        .create(LootContextParamSets.BLOCK_USE);

                lootTable.getRandomItems(lootParams).forEach(drop -> Block.popResource(level, pos, drop));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else if (!state.getValue(SNOWY) && stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof SnowLayerBlock) {
            level.setBlockAndUpdate(pos, state.setValue(SNOWY, true));
            level.playSound(player, pos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0F, pitch);
            stack.consume(1, player);

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return HedgeBlock.canSpread(levelReader, pos, state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return HedgeBlock.canSpread(level, pos, state);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos targetPos = HedgeBlock.findSpreadPos(serverLevel, random, pos, state);
        if (targetPos != null)
            serverLevel.setBlockAndUpdate(targetPos, state.setValue(SNOWY, false));
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.is(TagRegistry.HEDGE_BLOCKS);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 30;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 60;
    }

    protected boolean alwaysSnowy() {
        return false;
    }

    public BlockState calculateTop(BlockState state, LevelAccessor level, BlockPos pos) {
        boolean shouldBeTop = !(level.getBlockState(pos.above()).getBlock() instanceof HedgeBlock);

        if (state.getValue(TOP) != shouldBeTop)
            return state.setValue(TOP, shouldBeTop);
        return state;
    }

    public static boolean isExposedToSky(LevelReader level, BlockPos pos) {
        if (level.canSeeSky(pos))
            return true;

        BlockState aboveState = level.getBlockState(pos.above());
        if (aboveState.getBlock() instanceof HedgeBlock || aboveState.is(BlockTags.LEAVES) || aboveState.canBeReplaced())
            return HedgeBlock.isExposedToSky(level, pos.above());
        return false;
    }

    public static boolean canSpread(LevelReader levelReader, BlockPos pos, BlockState state) {
        for (Direction direction : SPREAD_DIRECTIONS) {
            BlockPos targetPos = pos.relative(direction);

            if (levelReader.getBlockState(targetPos).canBeReplaced() && state.canSurvive(levelReader, targetPos))
                return true;
        }
        return false;
    }

    @Nullable
    private static BlockPos findSpreadPos(LevelReader levelReader, RandomSource random, BlockPos pos, BlockState state) {
        for (Direction direction : Util.shuffledCopy(SPREAD_DIRECTIONS, random)) {
            BlockPos targetPos = pos.relative(direction);

            if (levelReader.getBlockState(targetPos).canBeReplaced() && state.canSurvive(levelReader, targetPos))
                return targetPos;
        }
        return null;
    }
}