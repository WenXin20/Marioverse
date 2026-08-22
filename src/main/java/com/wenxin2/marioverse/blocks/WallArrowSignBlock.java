package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.items.WrenchItem;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;

public class WallArrowSignBlock extends WallSignBlock {
    public WallArrowSignBlock(WoodType woodType, BlockBehaviour.Properties properties) {
        super(woodType, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStatePropertyRegistry.ARROW_DIRECTION, ArrowDirection.UP)
                .setValue(BlockStatePropertyRegistry.BOARD, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStatePropertyRegistry.ARROW_DIRECTION, BlockStatePropertyRegistry.BOARD);
    }

    @NotNull
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArrowSignBlockEntity(BlockEntityRegistry.ARROW_SIGN.get(), pos, state);
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.rotateArrow(level, state, pos) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity
                && signBlockEntity.isWaxed())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (this.wax(level, pos, stack))
            return ItemInteractionResult.SUCCESS;
        if (this.removeArrow(level, state, pos, stack))
            return ItemInteractionResult.SUCCESS;

        if (stack.is(TagRegistry.WRENCHES)) {
            return this.rotateArrow(level, state, pos)
                    ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean rotateArrow(Level level, BlockState state, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;
        if (!state.getValue(BlockStatePropertyRegistry.BOARD))
            return false;
        if (level.isClientSide)
            return true;

        var direction = state.getValue(BlockStatePropertyRegistry.ARROW_DIRECTION).next();
        level.setBlock(pos, state.setValue(BlockStatePropertyRegistry.ARROW_DIRECTION, direction), Block.UPDATE_CLIENTS);
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS); // TODO New sound
        return true;
    }

    private boolean wax(Level level, BlockPos pos, ItemStack stack) {
        if (!stack.is(Items.HONEYCOMB))
            return false;
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;

        if (!level.isClientSide) {
            signBlockEntity.setWaxed(true);
            stack.shrink(1);
            level.levelEvent(null, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, pos, 0);
        }
        return true;
    }

    private boolean removeArrow(Level level, BlockState state, BlockPos pos, ItemStack stack) {
        if (!stack.is(TagRegistry.ARROW_ERASERS))
            return false;
        if (state.getValue(BlockStatePropertyRegistry.ARROW_DIRECTION) == ArrowDirection.NONE)
            return false;

        if (!level.isClientSide)
            level.setBlock(pos, state.setValue(BlockStatePropertyRegistry.ARROW_DIRECTION, ArrowDirection.NONE),
                    Block.UPDATE_CLIENTS);
        return true;
    }
}