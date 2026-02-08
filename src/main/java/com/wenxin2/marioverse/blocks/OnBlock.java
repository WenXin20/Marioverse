package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.world.SwitchSavedData;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class OnBlock extends Block {
    public static final MapCodec<OnBlock> CODEC = simpleCodec(OnBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    @NotNull
    @Override
    protected MapCodec<OnBlock> codec() {
        return CODEC;
    }

    public OnBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(ACTIVE);
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (state.getValue(ACTIVE))
            return Shapes.block();
        return Shapes.empty();
    }

    @NotNull
    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (state.getValue(ACTIVE))
            return Shapes.block();
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (!state.getValue(ACTIVE))
            return 1.0F;
        return 0.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(ACTIVE))
            return false;
        return true;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
        return state.is(this) && neighborState.is(this)
                && !state.getValue(ACTIVE) && ! neighborState.getValue(ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        Level level = placeContext.getLevel();
        Player player = placeContext.getPlayer();

        if (level instanceof ServerLevel serverLevel) {
            SwitchSavedData data = SwitchSavedData.get(serverLevel);
            if (player.isShiftKeyDown())
                return this.defaultBlockState().setValue(ACTIVE, !data.isActive());
            return this.defaultBlockState().setValue(ACTIVE, data.isActive());
        }
        return this.defaultBlockState().setValue(ACTIVE, true);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        if (!level.isClientSide && level instanceof ServerLevel server)
            SwitchSavedData.get(server).add(pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!level.isClientSide && level instanceof ServerLevel server)
            SwitchSavedData.get(server).remove(pos);
    }

    public static void toggle(ServerLevel level) {
        SwitchSavedData data = SwitchSavedData.get(level);
        data.setOn(!data.isActive());

        boolean isActive = data.isActive();

        for (Set<BlockPos> set : List.copyOf(data.allPositions())) {
            for (BlockPos pos : List.copyOf(set)) {
                if (!level.isLoaded(pos)) continue;
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof OnBlock)) {
                    data.remove(pos);
                    continue;
                }
                if (state.getValue(OnBlock.ACTIVE) != isActive)
                    level.setBlock(pos, state.setValue(OnBlock.ACTIVE, isActive), Block.UPDATE_CLIENTS);
            }
        }
    }
}
