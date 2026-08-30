package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.LargeStandingArrowSignBlock;
import com.wenxin2.marioverse.blocks.LargeWallArrowSignBlock;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));

            list.add(Component.translatable("item.marioverse.large_arrow_signs.tooltip.instructions"));
            list.add(Component.translatable("item.marioverse.large_arrow_signs.tooltip.instructions.placement"));
            list.add(Component.translatable("item.marioverse.large_arrow_signs.tooltip.right_click"));
            list.add(Component.translatable("item.marioverse.large_arrow_signs.tooltip.right_click.axe"));
            list.add(Component.translatable("item.marioverse.large_arrow_signs.tooltip.right_click.shears"));
            list.add(Component.translatable("item.marioverse.large_arrow_signs.tooltip.shift_right_click"));
            list.add(Component.translatable("item.marioverse.large_arrow_signs.tooltip.shift_right_click.post"));

            list.add(Component.literal(""));
        } else list.add(Component.translatable("block.marioverse.warp_pipe.tooltip"));
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
        ItemStack stack = context.getItemInHand();
        Boolean savedWaxed = stack.get(DataComponentRegistry.WAXED.get());
        ArrowDirection savedDirection = stack.get(DataComponentRegistry.ARROW_SIGN_DIRECTION.get());
        DyeColor savedDyeColor = stack.get(DataComponentRegistry.ARROW_SIGN_DYE_COLOR.get());

        InteractionResult result = super.place(context);

        if (result.consumesAction()) {
            BlockPos pos = context.getClickedPos();
            Level level = context.getLevel();

            if (savedWaxed != null || savedDirection != null || savedDyeColor != null) {
                BlockState state = level.getBlockState(pos);
                Block block = state.getBlock();

                BlockPos[] siblingPositions;
                if (block instanceof LargeStandingArrowSignBlock standingSign)
                    siblingPositions = new BlockPos[] { standingSign.otherHalfPos(state, pos) };
                else if (block instanceof LargeWallArrowSignBlock wallSign)
                    siblingPositions = wallSign.siblingPositions(state, pos);
                else siblingPositions = new BlockPos[0];

                for (BlockPos siblingPos : siblingPositions) {
                    if (!(level.getBlockEntity(siblingPos) instanceof ArrowSignBlockEntity siblingEntity))
                        continue;

                    if (savedWaxed != null)
                        siblingEntity.setWaxed(savedWaxed);
                    if (savedDyeColor != null)
                        siblingEntity.setArrowDyeColor(savedDyeColor);

                    if (savedDirection != null) {
                        siblingEntity.setArrowDirection(savedDirection);

                        BlockState siblingState = level.getBlockState(siblingPos);
                        if (siblingState.hasProperty(BlockStatePropertyRegistry.ARROW_DIRECTION))
                            level.setBlock(siblingPos, siblingState.setValue(BlockStatePropertyRegistry.ARROW_DIRECTION, savedDirection),
                                    Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
        return result;
    }
}
