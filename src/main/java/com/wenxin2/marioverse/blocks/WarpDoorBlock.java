package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
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
        return this.source;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpDoorBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity blockEntityAbove = world.getBlockEntity(pos.above());
        BlockEntity blockEntityBelow = world.getBlockEntity(pos.below());
        UUID uuid = UUID.randomUUID();

        if (blockEntity instanceof WarpDoorBlockEntity doorBE) {
            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null && data.copyTag().hasUUID("UUID")) {
                doorBE.setUUID(data.copyTag().getUUID("UUID"));
                doorBE.setChanged();

                if (blockEntityAbove instanceof WarpDoorBlockEntity doorBEAbove) {
                    doorBEAbove.setUUID(data.copyTag().getUUID("UUID"));
                    doorBEAbove.setChanged();
                } else if (blockEntityBelow instanceof WarpDoorBlockEntity doorBEBelow) {
                    doorBEBelow.setUUID(data.copyTag().getUUID("UUID"));
                    doorBEBelow.setChanged();
                }
            } else {
                doorBE.setUUID(uuid);
                doorBE.setChanged();

                if (blockEntityAbove instanceof WarpDoorBlockEntity doorBEAbove) {
                    doorBEAbove.setUUID(uuid);
                    doorBEAbove.setChanged();
                } else if (blockEntityBelow instanceof WarpDoorBlockEntity doorBEBelow) {
                    doorBEBelow.setUUID(uuid);
                    doorBEBelow.setChanged();
                }
            }
            doorBE.onLoad();
        }
        super.setPlacedBy(world, pos, state, entity, stack);
    }
}

