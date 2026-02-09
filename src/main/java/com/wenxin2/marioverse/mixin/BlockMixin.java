package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.BouncyOnBlock;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (state.is(TagRegistry.BOUNCY_BLOCKS)
                && !entity.getType().is(TagRegistry.CANNOT_BOUNCE_ON_BLOCKS)
                && !entity.isSuppressingBounce() && !entity.isNoGravity())
            ci.cancel();
    }

    @Inject(method = "updateEntityAfterFallOn", at = @At("HEAD"), cancellable = true)
    private void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity, CallbackInfo ci) {
        if (blockGetter.getBlockState(entity.blockPosition().below()).is(TagRegistry.BOUNCY_BLOCKS)
                && !entity.getType().is(TagRegistry.CANNOT_BOUNCE_ON_BLOCKS)
                && !entity.isSuppressingBounce() && !entity.isNoGravity()
                && !(entity instanceof Player)) {
            BouncyOnBlock.bounceEntity(entity.level(), entity, true);
            ci.cancel();
        }
    }
}
