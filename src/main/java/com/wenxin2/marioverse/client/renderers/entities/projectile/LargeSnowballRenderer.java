package com.wenxin2.marioverse.client.renderers.entities.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.projectiles.LargeSnowballModel;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class LargeSnowballRenderer extends GeoEntityRenderer<LargeSnowballProjectile> {
    public LargeSnowballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LargeSnowballModel());
    }

    @Override
    public void render(LargeSnowballProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
//        poseStack.pushPose();
//            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
//            poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
//        poseStack.popPose();
    }
}
