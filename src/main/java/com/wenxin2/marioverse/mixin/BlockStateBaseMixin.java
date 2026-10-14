package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.SuperMushroomBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Shadow
    public abstract Block getBlock();

    @Inject(method = "getOffset", at = @At("HEAD"), cancellable = true)
    private void getOffset(BlockGetter blockGetter, BlockPos pos, CallbackInfoReturnable<Vec3> cir) {
        if (this.getBlock() instanceof SuperMushroomBlock mushroom && !mushroom.hasOffset(blockGetter, pos))
            cir.setReturnValue(Vec3.ZERO);
    }
}
