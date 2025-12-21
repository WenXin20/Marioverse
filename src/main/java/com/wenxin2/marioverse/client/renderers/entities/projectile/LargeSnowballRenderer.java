package com.wenxin2.marioverse.client.renderers.entities.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.models.entities.projectiles.LargeSnowballModel;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LargeSnowballRenderer extends GeoEntityRenderer<LargeSnowballProjectile> {
    public LargeSnowballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LargeSnowballModel());
    }

    @Override
    public void preRender(PoseStack poseStack, LargeSnowballProjectile animatable, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay, int colour) {
        this.model.getBone("root").ifPresent(root -> {
            float yaw = Mth.lerp(partialTick, animatable.prevVisualYaw, animatable.visualYaw);

            root.setRotY(yaw);
            root.setRotX(-animatable.roll);
            root.setRotZ(0.0F);
        });

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
