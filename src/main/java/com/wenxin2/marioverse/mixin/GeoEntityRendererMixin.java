package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.PipeTravelRotations;
import com.wenxin2.marioverse.client.renderers.SuperStarBufferSource;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> {
    @Inject(method = "preRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/Entity;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIII)V",
            at = @At(value = "HEAD"))
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
                          @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, int colour, CallbackInfo ci) {
        if (animatable instanceof LivingEntity livingEntity) {
            AttributeMap attributemap = livingEntity.getAttributes();
            float heightScale = (float) attributemap.getValue(AttributesRegistry.HEIGHT_SCALE);
            float widthScale = (float) attributemap.getValue(AttributesRegistry.WIDTH_SCALE);

            if (!isReRender)
                poseStack.scale(widthScale, heightScale, widthScale);
        }
    }

    @WrapOperation(method = "actuallyRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/Entity;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIII)V",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/renderer/GeoEntityRenderer;applyRotations(Lnet/minecraft/world/entity/Entity;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V"),
            require = 0, remap = false)
    private void mv$applyPipeRotations(GeoEntityRenderer<T> renderer, T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
                                       float partialTick, float nativeScale, Operation<Void> original) {
        if (!(animatable instanceof LivingEntity livingEntity) || !PipeTravelRotations.applyRotations(livingEntity, poseStack, partialTick, nativeScale))
            original.call(renderer, animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
    }

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), argsOnly = true, remap = false)
    private MultiBufferSource mv$wrapSuperStarBuffer(MultiBufferSource bufferSource, @Local(argsOnly = true) T entity,
                                                     @Local(argsOnly = true, ordinal = 1) float partialTick) {
        if (entity instanceof LivingEntity livingEntity)
            return SuperStarBufferSource.wrap(livingEntity, bufferSource, partialTick);
        return bufferSource;
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN"), remap = false)
    private void mv$flushSuperStarBuffer(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                                         int packedLight, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity && bufferSource instanceof SuperStarBufferSource superStarBuffer)
            superStarBuffer.flush(livingEntity, poseStack);
    }
}
