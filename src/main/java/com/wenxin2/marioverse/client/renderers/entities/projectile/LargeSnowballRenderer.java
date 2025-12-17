package com.wenxin2.marioverse.client.renderers.entities.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.models.entities.projectiles.LargeSnowballModel;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LargeSnowballRenderer extends GeoEntityRenderer<LargeSnowballProjectile> {
    public LargeSnowballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LargeSnowballModel());
    }

    @Override
    public void preRender(PoseStack poseStack, LargeSnowballProjectile animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        this.model.getBone("root").ifPresent(root -> {
            Vec3 velocity = animatable.getDeltaMovement();
            float horizontalSpeed = (float) velocity.horizontalDistance();
            horizontalSpeed = Mth.clamp(horizontalSpeed, 0.0F, 2.0F);

            float time = animatable.tickCount + partialTick;
            float roll = time * horizontalSpeed * 0.35F;

            if (velocity.horizontalDistanceSqr() > 0.0001) {
                float yawRad = (float) Math.atan2(velocity.x, velocity.z);
                root.setRotY(yawRad);
            }

            root.setRotX(-roll);
            root.setRotZ(0.0F);
        });

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
