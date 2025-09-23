package com.wenxin2.marioverse.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.awt.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

            poseStack.scale(widthScale, heightScale, widthScale);
        }

//        if (animatable instanceof AbilitiesHandler handler && handler.mv$hasSuperStar()) {
//            float speed = 40.0F;
//            float hue = ((animatable.tickCount + partialTick) % speed) / speed;
//            int rgb = Color.HSBtoRGB(hue, 1.0F, 1.0F);
//            float r = ((rgb >> 16) & 0xFF) / 255.0F;
//            float g = ((rgb >> 8)  & 0xFF) / 255.0F;
//            float b = (rgb & 0xFF) / 255.0F;
//            float a = 1.0F;
//
//            RenderSystem.setShaderColor(r, g, b, a);
//        }

//        if (animatable instanceof AbilitiesHandler handler && handler.mv$hasSuperStar()) {
//            float alpha = 0.5F;
//            float speed = 40.0F;
//            float hue = ((animatable.tickCount + partialTick) % speed) / speed;
//            int rgb = Color.HSBtoRGB(hue, 1.0F, 1.0F);
//            int argb = ((int)(alpha * 255) << 24) | (rgb & 0xFFFFFF);
//
//            GeoEntityRenderer<T> self = (GeoEntityRenderer<T>)(Object)this;
//            ResourceLocation texture = self.getTextureLocation(animatable);
//            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
//
//            poseStack.pushPose();
//            self.applyRenderLayers(poseStack, animatable, model, RenderType.entityTranslucent(texture),
//                    bufferSource, consumer, partialTick, 0xF000F0, argb);
//            poseStack.popPose();
//        }
    }

//    @Inject(method = "preRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/Entity;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIII)V",
//            at = @At(value = "RETURN"))
//    public void onRenderReturn(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
//                          @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
//                          int packedOverlay, int colour, CallbackInfo ci) {
//        if (animatable instanceof AbilitiesHandler handler && !handler.mv$hasSuperStar())
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//    }
}
