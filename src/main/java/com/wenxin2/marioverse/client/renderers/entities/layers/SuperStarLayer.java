package com.wenxin2.marioverse.client.renderers.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.registries.TextureRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.awt.Color;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public class SuperStarLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public SuperStarLayer(LivingEntityRenderer<?, ?> parent) {
        super((RenderLayerParent<T, M>) parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (entity instanceof AbilitiesHandler handler && handler.mv$hasSuperStar()) {
            float halfHeight = entity.getBbHeight() / 2.0F;
            float alpha = 1.0F;
            float speed = 40.0F;
            float hue = ((entity.tickCount + partialTicks) % speed) / speed;
            int rgb = Color.HSBtoRGB(hue, 1.0F, 1.0F);
            int argb = ((int) (alpha * 255) << 24) | (rgb & 0xFFFFFF);

            VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TextureRegistry.SUPER_STAR_OVERLAY));

            poseStack.pushPose();
                this.getParentModel().renderToBuffer(poseStack, consumer, 0xF000F0,
                        LivingEntityRenderer.getOverlayCoords(entity, 0.0F), argb);
            poseStack.popPose();
        }
    }
}
