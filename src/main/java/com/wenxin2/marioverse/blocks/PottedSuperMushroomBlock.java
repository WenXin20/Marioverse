package com.wenxin2.marioverse.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PottedSuperMushroomBlock extends FlowerPotBlock {
    public static final VoxelShape SUPER_SHAPE = Shapes
            .or(Block.box(5, 0, 5, 11, 6, 11),
                    Block.box(6, 6, 6, 10, 9, 10),
                    Block.box(3, 9, 3, 13, 15, 13)).optimize();
    public static final VoxelShape MEGA_SHAPE = Shapes
            .or(Block.box(5, 0, 5, 11, 6, 11),
                    Block.box(6, 6, 6, 10, 9, 10),
                    Block.box(0, 9, 0, 16, 14, 16),
                    Block.box(3, 14, 3, 13, 20, 13)).optimize();

    public PottedSuperMushroomBlock(@Nullable Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> block, Properties properties) {
        super(emptyPot, block, properties);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return this.getPotted() instanceof MegaMushroomBlock ? MEGA_SHAPE : SUPER_SHAPE;
    }
}
