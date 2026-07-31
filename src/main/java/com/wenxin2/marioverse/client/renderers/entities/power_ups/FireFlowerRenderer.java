package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.models.entities.FireFlowerModel;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FireFlowerRenderer extends GeoEntityRenderer<FireFlowerEntity> {
    public FireFlowerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireFlowerModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void renderFinal(PoseStack poseStack, FireFlowerEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource,
                            @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int color) {
        this.model.getBone("flower").ifPresent(bone -> {
            Vector3d bonePos = bone.getWorldPosition();

            if (animatable.tickCount % 10 == 0) {
                double randomX = (Math.random() - 0.5) * bone.getScaleX();
                double randomY = (Math.random() - 0.5) * bone.getScaleY();
                double randomZ = (Math.random() - 0.5) * bone.getScaleZ();

                animatable.getCommandSenderWorld().addParticle(ParticleTypes.FLAME,
                        bonePos.x() + randomX, bonePos.y() + randomY, bonePos.z() + randomZ,
                        0, 0, 0);
            }
        });
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, color);
    }
}
