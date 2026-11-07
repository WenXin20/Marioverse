package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class MegaMushroomBlock extends SuperMushroomBlock implements BonemealableBlock {
    public static final BooleanProperty TOP = BlockStatePropertyRegistry.TOP;

    protected static final VoxelShape TOP_SHAPE = Shapes
            .or(Block.box(5, 0, 5, 11, 4, 11),
                    Block.box(0, 4, 0, 16, 9, 16),
                    Block.box(3, 9, 3, 13, 15, 13)).optimize();
    protected static final VoxelShape SHAPE = Block
            .box(5, 0, 5, 11, 16, 11).optimize();

    public MegaMushroomBlock(ResourceKey<ConfiguredFeature<?, ?>> configuredFeature,
                             ResourceKey<ConfiguredFeature<?, ?>> wideFeature, Properties properties) {
        super(configuredFeature, wideFeature, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(TOP))
            return TOP_SHAPE;
        return SHAPE;
    }
}
