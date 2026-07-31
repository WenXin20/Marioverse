package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.models.entities.IceFlowerModel;
import com.wenxin2.marioverse.entities.power_ups.IceFlowerEntity;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class IceFlowerRenderer extends GeoEntityRenderer<IceFlowerEntity> {
    public IceFlowerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceFlowerModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void renderFinal(PoseStack poseStack, IceFlowerEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource,
                            @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int color) {
        this.model.getBone("flower").ifPresent(bone -> {
            Vector3d bonePos = bone.getWorldPosition();

            if (animatable.tickCount % 10 == 0) {
                double randomX = (Math.random() - 0.5) * bone.getScaleX();
                double randomY = (Math.random() - 0.5) * bone.getScaleY();
                double randomZ = (Math.random() - 0.5) * bone.getScaleZ();

                animatable.getCommandSenderWorld().addParticle(ParticleRegistry.ICE_STAR.get(),
                        bonePos.x() + randomX, bonePos.y() + randomY, bonePos.z() + randomZ,
                        0, 0, 0);
            }
        });
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, color);
    }
}
