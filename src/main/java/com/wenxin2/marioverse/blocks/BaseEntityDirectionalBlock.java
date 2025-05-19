package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseEntityDirectionalBlock extends DirectionalBlock implements EntityBlock {
    protected BaseEntityDirectionalBlock(BlockBehaviour.Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    protected abstract MapCodec<? extends BaseEntityDirectionalBlock> codec();

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level world, BlockPos pos, int i, int j) {
        super.triggerEvent(state, world, pos, i, j);
        BlockEntity blockentity = world.getBlockEntity(pos);
        return blockentity == null ? false : blockentity.triggerEvent(i, j);
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity blockentity = world.getBlockEntity(pos);
        return blockentity instanceof MenuProvider ? (MenuProvider)blockentity : null;
    }

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> type, BlockEntityType<E> type1, BlockEntityTicker<? super E> ticker) {
        return type1 == type ? (BlockEntityTicker<A>) ticker : null;
    }
}
