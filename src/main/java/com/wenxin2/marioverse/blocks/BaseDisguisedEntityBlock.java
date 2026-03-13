package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseDisguisedEntityBlock extends BaseEntityBlock {
    public static final MapCodec<BaseDisguisedEntityBlock> CODEC = simpleCodec(BaseDisguisedEntityBlock::new);
    public static final BooleanProperty DISGUISED = BlockStatePropertyRegistry.DISGUISED;

    @NotNull
    @Override
    public MapCodec<BaseDisguisedEntityBlock> codec() {
        return CODEC;
    }

    public BaseDisguisedEntityBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DISGUISED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISGUISED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }
}
