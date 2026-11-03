package com.wenxin2.marioverse.mixin.immersive_paintings;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.renderers.PaintingRippleRenderer;
import net.conczin.immersive_paintings.entity.ImmersivePaintingEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.conczin.immersive_paintings.client.render.ImmersivePaintingEntityRenderer")
public abstract class ImmersivePaintingEntityRendererMixin {
    @Inject(method = "render(Lnet/conczin/immersive_paintings/entity/ImmersivePaintingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), require = 0, remap = false)
    private void mv$beginRipple(ImmersivePaintingEntity painting, float entityYaw, float partialTick, PoseStack poseStack,
                                MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        PaintingRippleRenderer.begin(painting, partialTick, poseStack);
    }

    @Inject(method = "render(Lnet/conczin/immersive_paintings/entity/ImmersivePaintingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN"), require = 0, remap = false)
    private void mv$endRipple(ImmersivePaintingEntity painting, float entityYaw, float partialTick, PoseStack poseStack,
                              MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        PaintingRippleRenderer.end();
    }

    @WrapOperation(method = "renderPainting(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/conczin/immersive_paintings/entity/ImmersivePaintingEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entitySolid(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"),
            require = 0, remap = false)
    private RenderType mv$getRipplingSolidType(ResourceLocation texture, Operation<RenderType> original) {
        return PaintingRippleRenderer.renderType(original.call(texture), texture, false);
    }

    @WrapOperation(method = "renderPainting(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/conczin/immersive_paintings/entity/ImmersivePaintingEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityTranslucent(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"),
            require = 0, remap = false)
    private RenderType mv$getRipplingTranslucentType(ResourceLocation texture, Operation<RenderType> original) {
        return PaintingRippleRenderer.renderType(original.call(texture), texture, true);
    }

    @WrapOperation(method = "renderPainting(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/conczin/immersive_paintings/entity/ImmersivePaintingEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
            require = 0, remap = false)
    private VertexConsumer mv$getRipplingBuffer(MultiBufferSource bufferSource, RenderType renderType, Operation<VertexConsumer> original) {
        return PaintingRippleRenderer.buffer(renderType, original.call(bufferSource, renderType));
    }
}
