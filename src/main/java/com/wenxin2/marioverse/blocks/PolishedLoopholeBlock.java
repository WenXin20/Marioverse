package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PolishedLoopholeBlock extends LoopholeBlock {
    public static final MapCodec<PolishedLoopholeBlock> CODEC = simpleCodec(PolishedLoopholeBlock::new);
    protected static final VoxelShape[] POLISHED_SHAPES = LoopholeBlock.makeShapes(4.0, 10.0);

    public PolishedLoopholeBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public MapCodec<PolishedLoopholeBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape[] getShapes() {
        return POLISHED_SHAPES;
    }
}
