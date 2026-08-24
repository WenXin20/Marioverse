package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class ArrowSignItem extends BlockItem {
    private final Block wallSign;
    private final Block hangingSign;

    public ArrowSignItem(Properties properties, Block standingSign, Block wallSign, Block hangingSign) {
        super(standingSign, properties);
        this.wallSign = wallSign;
        this.hangingSign = hangingSign;
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        ArrowDirection direction = stack.get(DataComponentRegistry.ARROW_SIGN_DIRECTION.get());
        if (direction == null)
            return super.getName(stack);

        String key = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(this)).toLanguageKey("item") + "." + direction.getSerializedName();
        return Component.translatable(key);
    }

    protected boolean canPlace(LevelReader level, BlockState state, BlockPos pos) {
        return state.canSurvive(level, pos);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        Block candidate = switch (context.getClickedFace()) {
            case UP -> this.getBlock();
            case DOWN -> this.hangingSign;
            default -> this.wallSign;
        };

        BlockState result = candidate.getStateForPlacement(context);
        if (result == null) {
            return null;
        }

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
        }
        return result;
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        super.registerBlocks(blockToItemMap, item);
        blockToItemMap.put(this.wallSign, item);
        blockToItemMap.put(this.hangingSign, item);
    }
}