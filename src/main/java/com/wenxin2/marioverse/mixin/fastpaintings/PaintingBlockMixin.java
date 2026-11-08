package com.wenxin2.marioverse.mixin.fastpaintings;

import com.wenxin2.marioverse.integration.fast_paintings_compat.FastPaintingsCompat;
import net.mehvahdjukaar.fastpaintings.PaintingBlock;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PaintingBlock.class)
public abstract class PaintingBlockMixin {
    @Inject(method = "tryConverting", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/Painting;discard()V"))
    private static void mv$transferWarpData(Painting painting, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        FastPaintingsCompat.transferWarpData(painting);
    }
}
