package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.NotNull;

public class SpikePanelBlock extends PanelBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<SpikePanelBlock> CODEC = simpleCodec(SpikePanelBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty SPIKES = BooleanProperty.create("spikes");

    @NotNull
    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public SpikePanelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACE, AttachFace.FLOOR).setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false).setValue(SPIKES, true).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACE, FACING, POWERED, SPIKES, WATERLOGGED);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide && state.getValue(SPIKES)) {
            if (!entity.getType().is(TagRegistry.IRON_SPIKE_IMMUNE) && !(entity instanceof ItemEntity)) // TODO
                entity.hurt(DamageSourceRegistry.spiked(entity), ConfigRegistry.IRON_SPIKE_DAMAGE.get().floatValue());
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos posNeighbor, boolean b) {
        if (world instanceof ServerLevel serverWorld)
            this.checkAndFlip(state, serverWorld, pos);

        super.neighborChanged(state, world, pos, block, posNeighbor, b);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState neighborState, boolean b) {
        if (neighborState.getBlock() != state.getBlock() && world instanceof ServerLevel serverWorld)
            this.checkAndFlip(state, serverWorld, pos);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return world.getBlockState(pos).getValue(SPIKES) ? 15 : 0;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType computationType) {
        return false;
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return false;
    }

    public void checkAndFlip(BlockState state, ServerLevel serverWorld, BlockPos pos) {
        boolean hasNeighborSignal = serverWorld.hasNeighborSignal(pos);
        BlockState newState = state;
        if (hasNeighborSignal != state.getValue(POWERED)) {
            if (!state.getValue(POWERED)) {
                newState = state.cycle(SPIKES);
                serverWorld.playSound(null, pos, newState.getValue(SPIKES)
                        ? SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS); //TODO
            }

            serverWorld.setBlock(pos, newState.setValue(POWERED, hasNeighborSignal), 3);
        }
    }
}
