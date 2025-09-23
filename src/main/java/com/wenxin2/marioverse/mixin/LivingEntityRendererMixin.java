package com.wenxin2.marioverse.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.awt.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

//        if (entity instanceof AbilitiesHandler handler && handler.mv$hasSuperStar()) {
//            float speed = 40.0F;
//            float hue = ((entity.tickCount + partialTicks) % speed) / speed;
//            int rgb = Color.HSBtoRGB(hue, 1.0F, 1.0F);
//
//            float r = ((rgb >> 16) & 0xFF) / 255.0F;
//            float g = ((rgb >> 8)  & 0xFF) / 255.0F;
//            float b = (rgb & 0xFF) / 255.0F;
//            float a = 1.0F;
//
//            RenderSystem.setShaderColor(r, g, b, a);
//        }
//        if (entity instanceof AbilitiesHandler handler && handler.mv$hasSuperStar()) {
//            float alpha = 0.5F;
//            float speed = 40.0F;
//            float hue = ((entity.tickCount + partialTicks) % speed) / speed;
//            int rgb = Color.HSBtoRGB(hue, 1.0F, 1.0F);
//            int argb = ((int) (alpha * 255) << 24) | (rgb & 0xFFFFFF);
//
//            LivingEntityRenderer<T, M> self = (LivingEntityRenderer<T, M>)(Object)this;
//            M model = self.getModel();
//            ResourceLocation baseTexture = self.getTextureLocation(entity);
//            VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(baseTexture));
//
//            poseStack.pushPose();
//            model.renderToBuffer(poseStack, consumer, 0xF000F0,
//                    LivingEntityRenderer.getOverlayCoords(entity, 0.0F), argb);
//            poseStack.popPose();
//        }
    }

//    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//            at = @At("RETURN"))
//    private void onRenderReturn(T entity, float entityYaw, float partialTicks,
//                                PoseStack poseStack, MultiBufferSource buffer,
//                                int packedLight, CallbackInfo ci) {
//        if (entity instanceof AbilitiesHandler handler && !handler.mv$hasSuperStar())
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//    }
}
