package com.wenxin2.marioverse.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.init.AttributesRegistry;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {
    @Inject(method = "scale(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At("HEAD"))
    public void modifyScale(T livingEntity, PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        AttributeMap attributemap = livingEntity.getAttributes();
        float heightScale = (float) attributemap.getValue(AttributesRegistry.HEIGHT_SCALE);
        float widthScale = (float) attributemap.getValue(AttributesRegistry.WIDTH_SCALE);

        poseStack.scale(widthScale, heightScale, widthScale);
    }
}
