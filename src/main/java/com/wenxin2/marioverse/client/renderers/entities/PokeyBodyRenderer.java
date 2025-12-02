package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.PokeyBodyModel;
import com.wenxin2.marioverse.entities.PokeyBodyEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PokeyBodyRenderer extends GeoEntityRenderer<PokeyBodyEntity> {
    public PokeyBodyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PokeyBodyModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(PokeyBodyEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        int rotIndex = Math.floorMod(entity.getUUID().hashCode(), 4);
        float degrees = rotIndex * 90f;

        poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(degrees));
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}