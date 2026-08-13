package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.PicketFenceBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin {

    @Inject(method = "connectsTo", at = @At("HEAD"), cancellable = true)
    private void mv$connectsTo(BlockState state, boolean sideSolid, Direction direction,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof PicketFenceBlock fence
                && fence.connectsToEdge(state, direction))
            cir.setReturnValue(true);
    }
}
