package com.wenxin2.marioverse.client.renderers.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.renderers.entities.SuperStarRenderType;
import com.wenxin2.marioverse.init.TextureRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SuperStarLayer extends RenderLayer {
    private final RenderLayerParent parent;
    public SuperStarLayer(RenderLayerParent parent) {
        super(parent);
        this.parent = parent;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Entity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!entity.getPersistentData().getBoolean("marioverse:has_super_star")) {
            return;
        }

        float alpha = 1F;

        Minecraft.getInstance().getTextureManager().getTexture(TextureRegistry.SUPER_STAR_OVERLAY);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TextureRegistry.SUPER_STAR_OVERLAY));

        poseStack.pushPose();
            this.getParentModel().renderToBuffer(poseStack, consumer, packedLight,
                    LivingEntityRenderer.getOverlayCoords((LivingEntity) entity, 0.0F));
        poseStack.popPose();
    }
}
