package com.wenxin2.marioverse.client.renderers.entities.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.projectiles.BouncingFireballModel;
import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BouncingFireballRenderer extends GeoEntityRenderer<BouncingFireballProjectile> {
    public BouncingFireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BouncingFireballModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void preRender(PoseStack poseStack, BouncingFireballProjectile animatable, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-animatable.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(animatable.getXRot()));
            super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
                    LightTexture.FULL_BRIGHT, packedOverlay, colour);
        poseStack.popPose();
    }
}
