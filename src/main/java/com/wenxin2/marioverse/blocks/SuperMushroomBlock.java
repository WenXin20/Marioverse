package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import org.jetbrains.annotations.NotNull;

public class SuperMushroomBlock extends MushroomBlock implements BonemealableBlock {
    public static final BooleanProperty TOP = BlockStatePropertyRegistry.TOP;

    protected static final VoxelShape TOP_SHAPE = Shapes
            .or(Block.box(6, 0, 6, 10, 4, 10),
                    Block.box(3, 4, 3, 13, 10, 13)).optimize();
    protected static final VoxelShape SHAPE = Block
            .box(6, 0, 6, 10, 16, 10).optimize();

    protected ResourceKey<ConfiguredFeature<?, ?>> wideFeature;

    public SuperMushroomBlock(ResourceKey<ConfiguredFeature<?, ?>> configuredFeature,
                              ResourceKey<ConfiguredFeature<?, ?>> wideFeature, Properties properties) {
        super(configuredFeature, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, true));
        this.wideFeature = wideFeature;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        final Vec3 offset = state.getOffset(blockGetter, pos);

        if (state.getValue(TOP))
            return TOP_SHAPE.move(offset.x, offset.y, offset.z);
        return SHAPE.move(offset.x, offset.y, offset.z);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateAbove = context.getLevel().getBlockState(context.getClickedPos().above());
        return this.defaultBlockState().setValue(TOP, !stateAbove.is(this));
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        BlockState newState = super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
        if (direction == Direction.UP && newState.is(this))
            return newState.setValue(TOP, !neighborState.is(this));
        return newState;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockPos posBelow = pos.below();
        BlockState stateBelow = levelReader.getBlockState(posBelow);

        return stateBelow.is(this) || stateBelow.isSolidRender(levelReader, posBelow)
                || stateBelow.getBlock() instanceof FarmBlock;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        if (random.nextInt(25) != 0)
            return;

        int mushroomsAllowed = 5;
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 1, 4))) {
            if (serverLevel.getBlockState(nearbyPos).is(this) && --mushroomsAllowed <= 0)
                return;
        }

        BlockPos spreadPos = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
        for (int i = 0; i < 4; i++) {
            if (this.canSpreadTo(serverLevel, spreadPos))
                pos = spreadPos;
            spreadPos = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
        }

        if (this.canSpreadTo(serverLevel, spreadPos))
            serverLevel.setBlock(spreadPos, this.defaultBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        if (this.canGrowHuge(levelReader, pos))
            return true;

        BlockPos posAbove = this.getTopPos(levelReader, pos).above();
        return !levelReader.isOutsideBuildHeight(posAbove) && levelReader.isEmptyBlock(posAbove);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        if (this.canGrowHuge(level, pos))
            return super.isBonemealSuccess(level, random, pos, state);
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        if (this.canGrowHuge(serverLevel, pos)) {
            if (!this.growWideMushroom(serverLevel, pos, random))
                this.growMushroom(serverLevel, pos, state, random);
        } else serverLevel.setBlock(this.getTopPos(serverLevel, pos).above(), this.defaultBlockState(), Block.UPDATE_ALL);
    }

    private boolean canSpreadTo(ServerLevel serverLevel, BlockPos pos) {
        BlockState stateBelow = serverLevel.getBlockState(pos.below());
        if (!serverLevel.isEmptyBlock(pos) || !this.defaultBlockState().canSurvive(serverLevel, pos))
            return false;
        return stateBelow.is(BlockTags.MUSHROOM_GROW_BLOCK) || serverLevel.getRawBrightness(pos, 0) < 13;
    }

    private boolean canGrowHuge(BlockGetter blockGetter, BlockPos pos) {
        return blockGetter.getBlockState(pos.below()).is(BlockTags.DIRT)
                && !blockGetter.getBlockState(pos.above()).is(this);
    }

    private boolean growWideMushroom(ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        for (int offsetX = 0; offsetX >= -1; offsetX--) {
            for (int offsetZ = 0; offsetZ >= -1; offsetZ--) {
                BlockPos cornerPos = pos.offset(offsetX, 0, offsetZ);
                List<BlockPos> positions = List.of(cornerPos, cornerPos.east(), cornerPos.south(), cornerPos.east().south());
                if (positions.stream().allMatch(mushroomPos -> serverLevel.getBlockState(mushroomPos).is(this))) {
                    this.placeWideMushroom(serverLevel, cornerPos, positions, random);
                    return true;
                }
            }
        }
        return false;
    }

    private void placeWideMushroom(ServerLevel serverLevel, BlockPos cornerPos, List<BlockPos> positions, RandomSource random) {
        Optional<Holder.Reference<ConfiguredFeature<?, ?>>> feature = serverLevel.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(this.wideFeature);
        if (feature.isEmpty())
            return;

        BlockGrowFeatureEvent event = EventHooks.fireBlockGrowFeature(serverLevel, random, cornerPos, feature.get());
        if (event.isCanceled() || event.getFeature() == null)
            return;

        List<BlockState> states = positions.stream().map(serverLevel::getBlockState).toList();
        positions.forEach(mushroomPos -> serverLevel.removeBlock(mushroomPos, false));
        if (!event.getFeature().value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, cornerPos)) {
            for (int i = 0; i < positions.size(); i++)
                serverLevel.setBlock(positions.get(i), states.get(i), Block.UPDATE_ALL);
        }
    }

    private BlockPos getTopPos(BlockGetter blockGetter, BlockPos pos) {
        BlockPos.MutableBlockPos topPos = pos.mutable();
        while (blockGetter.getBlockState(topPos.above()).is(this))
            topPos.move(Direction.UP);
        return topPos.immutable();
    }
}
