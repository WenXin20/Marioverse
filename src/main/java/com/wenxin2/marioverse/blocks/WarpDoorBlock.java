package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WarpDoorBlock extends DoorBlock implements EntityBlock {

    private final DoorBlock source;

    public WarpDoorBlock(DoorBlock source) {
        super(source.type(), BlockBehaviour.Properties.ofFullCopy(source));
        this.source = source;
    }

    public DoorBlock source() {
        return source;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpDoorBlockEntity(pos, state);
    }
}

