package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.DisguisedBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getShape(blockGetter, pos, context);
        return Shapes.block();
    }

    @NotNull
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getCollisionShape(blockGetter, pos, context);
        return super.getCollisionShape(state, blockGetter, pos, context);
    }

    @NotNull
    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getVisualShape(blockGetter, pos, context);
        return super.getVisualShape(state, blockGetter, pos, context);
    }

    @NotNull
    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getBlockSupportShape(blockGetter, pos);
        return super.getBlockSupportShape(state, blockGetter, pos);
    }

    @NotNull
    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getInteractionShape(blockGetter, pos);
        return super.getInteractionShape(state, blockGetter, pos);
    }

    @NotNull
    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getOcclusionShape(blockGetter, pos);
        return super.getOcclusionShape(state, blockGetter, pos);
    }

    @NotNull
    @Override
    public MapColor getMapColor(BlockState state, BlockGetter blockGetter, BlockPos pos, MapColor defaultColor) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getMapColor(blockGetter, pos);
        return super.getMapColor(state, blockGetter, pos, defaultColor);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getLightBlock(blockGetter, pos);
        return super.getLightBlock(state, blockGetter, pos);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getLightEmission(blockGetter, pos);
        return super.getLightEmission(state, blockGetter, pos);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().getShadeBrightness(blockGetter, pos);
        return super.getShadeBrightness(state, blockGetter, pos);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(DISGUISED) && blockGetter.getBlockEntity(pos) instanceof DisguisedBlockEntity blockEntity)
            return blockEntity.getDisguise().propagatesSkylightDown(blockGetter, pos);
        return super.propagatesSkylightDown(state, blockGetter, pos);
    }
}
