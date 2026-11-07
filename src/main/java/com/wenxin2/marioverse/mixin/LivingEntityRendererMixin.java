package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.client.PipeTravelRotations;
import com.wenxin2.marioverse.client.renderers.SuperStarBufferSource;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"))
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        AttributeMap attributemap = entity.getAttributes();
        float heightScale = (float) attributemap.getValue(AttributesRegistry.HEIGHT_SCALE);
        float widthScale = (float) attributemap.getValue(AttributesRegistry.WIDTH_SCALE);

        poseStack.scale(widthScale, heightScale, widthScale);
    }

    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;setupRotations(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V"),
            require = 0)
    private void mv$setupPipeRotations(LivingEntityRenderer<T, M> renderer, T entity, PoseStack poseStack, float bob, float bodyRot,
                                       float partialTick, float scale, Operation<Void> original) {
        if (!PipeTravelRotations.applyRotations(entity, poseStack, partialTick, scale))
            original.call(renderer, entity, poseStack, bob, bodyRot, partialTick, scale);
    }

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), argsOnly = true)
    private MultiBufferSource mv$wrapSuperStarBuffer(MultiBufferSource bufferSource, @Local(argsOnly = true) T entity,
                                                     @Local(argsOnly = true, ordinal = 1) float partialTicks) {
        return SuperStarBufferSource.wrap(entity, bufferSource, partialTicks);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN"))
    private void mv$flushSuperStarBuffer(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
                                         int packedLight, CallbackInfo ci) {
        if (buffer instanceof SuperStarBufferSource superStarBuffer)
            superStarBuffer.flush(entity, poseStack);
    }
}
