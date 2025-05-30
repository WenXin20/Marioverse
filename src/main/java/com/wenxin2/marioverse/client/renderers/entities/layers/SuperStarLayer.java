package com.wenxin2.marioverse.client.renderers.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.registries.TextureRegistry;
import com.wenxin2.marioverse.utils.PowerUpHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SuperStarLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public SuperStarLayer(EntityRenderer<? extends Player> parent) {
        super((RenderLayerParent<T, M>) parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (entity instanceof PowerUpHandler handler && handler.mv$hasSuperStar()) {
            float alpha = 1F;

            Minecraft.getInstance().getTextureManager().getTexture(TextureRegistry.SUPER_STAR_OVERLAY);

            VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TextureRegistry.SUPER_STAR_OVERLAY));

            poseStack.pushPose();
                this.getParentModel().renderToBuffer(poseStack, consumer, packedLight,
                        LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
            poseStack.popPose();
        }
    }
}
