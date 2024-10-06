package com.wenxin2.marioverse.client.renderers.entities.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.projectiles.BouncingFireballModel;
import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BouncingFireballRenderer extends GeoEntityRenderer<BouncingFireballProjectile> {
    public BouncingFireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BouncingFireballModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void render(BouncingFireballProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Apply rotations based on entity's current rotation
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));

        // Render the model
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }
}
