package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PottedTrampolineCapBlock extends FlowerPotBlock implements ToggleableBlock {
    public PottedTrampolineCapBlock(@Nullable Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> block, Properties properties) {
        super(emptyPot, block, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(OnBlock.ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OnBlock.ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.getStateForPlacementSavedData(this.defaultBlockState(), placeContext);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.onPlaceSavedData(level, pos);
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        this.onRemoveSavedData(level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        if (!player.getItemInHand(hand).is(ItemRegistry.CREATIVE_WRENCH.get()))
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }
}
