package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
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
                .setValue(TOP, true).setValue(SNOWY, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TOP, SNOWY, WATERLOGGED);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(TOP))
            return SHAPE_TOP;
        return SHAPE;
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
        boolean snowStorm = level.isRaining() && biome.getPrecipitationAt(pos) == Biome.Precipitation.SNOW;

        if (!snowy && (snowStorm || this.alwaysSnowy()) && random.nextInt(16) == 0)
            level.setBlockAndUpdate(pos, state.setValue(SNOWY, true));
        else if (snowy && !this.alwaysSnowy() && !snowStorm && !snowyBiome)
            level.setBlockAndUpdate(pos, state.setValue(SNOWY, false));
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(SNOWY) || !stack.is(ItemTags.SHOVELS))
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);

        level.setBlockAndUpdate(pos, state.setValue(SNOWY, false));
        level.playSound(player, pos, SoundEvents.SNOW_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

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

    protected boolean alwaysSnowy() {
        return false;
    }

    public BlockState calculateTop(BlockState state, LevelAccessor level, BlockPos pos) {
        boolean shouldBeTop = !level.getBlockState(pos.above()).is(this);

        if (state.getValue(TOP) != shouldBeTop)
            return state.setValue(TOP, shouldBeTop);
        return state;
    }

    private static boolean canSpread(LevelReader levelReader, BlockPos pos, BlockState state) {
        for (Direction direction : SPREAD_DIRECTIONS) {
            BlockPos targetPos = pos.relative(direction);

            if (levelReader.isEmptyBlock(targetPos) && state.canSurvive(levelReader, targetPos))
                return true;
        }
        return false;
    }

    @Nullable
    private static BlockPos findSpreadPos(LevelReader levelReader, RandomSource random, BlockPos pos, BlockState state) {
        for (Direction direction : Util.shuffledCopy(SPREAD_DIRECTIONS, random)) {
            BlockPos targetPos = pos.relative(direction);

            if (levelReader.isEmptyBlock(targetPos) && state.canSurvive(levelReader, targetPos))
                return targetPos;
        }
        return null;
    }
}
