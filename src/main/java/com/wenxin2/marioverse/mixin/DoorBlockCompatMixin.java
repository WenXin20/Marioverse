package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Added for compat, prevents overwriting modded door block entities
@Mixin(DoorBlock.class)
public abstract class DoorBlockCompatMixin implements EntityBlock {
    @Dynamic("This method is added by my first mixin and may be overridden by another mod")
    @Inject(method = "newBlockEntity", at = @At("RETURN"), cancellable = true)
    private void injectBlockEntity(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir) {
        System.out.println("Injected some code... here's the return value: " + cir.getReturnValue());
        if (!ConfigRegistry.DISABLE_WARP_DOORS.get() && cir.getReturnValue() == null)
            cir.setReturnValue(new WarpDoorBlockEntity(pos, state));
        else cir.setReturnValue(cir.getReturnValue());
    }
}
