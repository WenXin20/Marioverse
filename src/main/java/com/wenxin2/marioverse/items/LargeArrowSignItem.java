package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class LargeArrowSignItem extends ArrowSignItem {
    private final Block largeWallSign;

    public LargeArrowSignItem(Properties properties, Block standingSign, Block wallSign) {
        super(properties, standingSign, wallSign, wallSign);
        this.largeWallSign = wallSign;
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        if (context.getClickedFace() == Direction.DOWN)
            return null;

        Player player = context.getPlayer();
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Block candidate = context.getClickedFace() == Direction.UP ? this.getBlock() : this.largeWallSign;

        BlockState result = candidate.getStateForPlacement(context);
        if (result == null)
            return null;

        if (result.hasProperty(BlockStateProperties.ROTATION_16))
            result = result.setValue(BlockStateProperties.ROTATION_16,
                    RotationSegment.convertToSegment(context.getRotation()));
        if (result.hasProperty(BlockStatePropertyRegistry.BOARD)
                && result.hasProperty(BlockStatePropertyRegistry.POST)
                && player != null && player.isSecondaryUseActive())
            result = result.setValue(BlockStatePropertyRegistry.BOARD, false);

        return this.canPlace(level, result, pos)
                && level.isUnobstructed(result, pos, CollisionContext.empty()) ? result : null;
    }

    @NotNull
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);

        if (result.consumesAction()) {
            BlockPos pos = context.getClickedPos();
            Level level = context.getLevel();

            if (level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity) {
                ItemStack stack = context.getItemInHand();

                Boolean savedWaxed = stack.get(DataComponentRegistry.WAXED.get());
                if (savedWaxed != null)
                    signBlockEntity.setWaxed(savedWaxed);

                ArrowDirection savedDirection = stack.get(DataComponentRegistry.ARROW_SIGN_DIRECTION.get());
                if (savedDirection != null) {
                    signBlockEntity.setArrowDirection(savedDirection);

                    BlockState state = level.getBlockState(pos);
                    if (state.hasProperty(BlockStatePropertyRegistry.ARROW_DIRECTION))
                        level.setBlock(pos, state.setValue(BlockStatePropertyRegistry.ARROW_DIRECTION, savedDirection),
                                Block.UPDATE_CLIENTS);
                }
            }

            if (level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity) {
                ItemStack stack = context.getItemInHand();

                Boolean savedWaxed = stack.get(DataComponentRegistry.WAXED.get());
                if (savedWaxed != null)
                    signBlockEntity.setWaxed(savedWaxed);

                ArrowDirection savedDirection = stack.get(DataComponentRegistry.ARROW_SIGN_DIRECTION.get());
                if (savedDirection != null) {
                    signBlockEntity.setArrowDirection(savedDirection);

                    BlockState state = level.getBlockState(pos);
                    if (state.hasProperty(BlockStatePropertyRegistry.ARROW_DIRECTION))
                        level.setBlock(pos, state.setValue(BlockStatePropertyRegistry.ARROW_DIRECTION, savedDirection),
                                Block.UPDATE_CLIENTS);
                }
            }
        }
        return result;
    }
}
