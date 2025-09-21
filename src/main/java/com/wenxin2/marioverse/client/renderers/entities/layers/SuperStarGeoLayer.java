package com.wenxin2.marioverse.client.renderers.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SuperStarGeoLayer<T extends LivingEntity & GeoAnimatable> extends GeoRenderLayer<T> {
    public SuperStarGeoLayer(GeoEntityRenderer<T> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource,
                       @Nullable VertexConsumer buffer, float partialTicks, int packedLight, int packedOverlay) {
        super.render(poseStack, entity, bakedModel, renderType, bufferSource, buffer, partialTicks, packedLight, packedOverlay);

        if (entity instanceof AbilitiesHandler handler && handler.mv$hasSuperStar()) {
            float alpha = 0.5F;
            float speed = 40.0F;
            float hue = ((entity.tickCount + partialTicks) % speed) / speed;
            int rgb = java.awt.Color.HSBtoRGB(hue, 1.0F, 1.0F);

            int argb = ((int) (alpha * 255) << 24) | (rgb & 0xFFFFFF);

            GeoRenderer<T> renderer = this.getRenderer();
            ResourceLocation baseTexture = renderer.getTextureLocation(entity);
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(baseTexture));

            poseStack.pushPose();
                this.getRenderer().actuallyRender(poseStack, entity, bakedModel, renderType, bufferSource, consumer, true, partialTicks, 0xF000F0,
                        LivingEntityRenderer.getOverlayCoords(entity, 0.0F), argb);
            poseStack.popPose();
        }
    }
}
