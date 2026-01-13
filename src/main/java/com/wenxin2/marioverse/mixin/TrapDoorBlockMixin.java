package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(TrapDoorBlock.class)
public class TrapDoorBlockMixin extends Block implements EntityBlock {
    public TrapDoorBlockMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get())
            return new WarpTrapDoorBlockEntity(pos, state);
        else return null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WarpTrapDoorBlockEntity doorBE) {
            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null && data.copyTag().hasUUID("UUID")) {
                doorBE.setUUID(data.copyTag().getUUID("UUID"));
                doorBE.setChanged();
            } else {
                UUID uuid = UUID.randomUUID();
                doorBE.setUUID(uuid);
                doorBE.setChanged();
            }

            doorBE.onLoad();
        }

        super.setPlacedBy(world, pos, state, entity, stack);
    }
}
