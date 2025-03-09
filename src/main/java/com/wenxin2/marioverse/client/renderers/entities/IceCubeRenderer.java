package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.IceCubeModel;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceCubeRenderer extends GeoEntityRenderer<IceCubeEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public IceCubeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceCubeModel());
        this.entityRenderer = renderManager.getEntityRenderDispatcher();
    }

    @Override
    public void render(IceCubeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        Entity frozenEntity = entity.getOrCreateDisplayEntity(entity.level());
        if (frozenEntity != null) {
            float width = frozenEntity.getBbWidth();
            float height = frozenEntity.getBbHeight();

            poseStack.pushPose();
                this.withScale(width * 1.45F, height * 1.35F);
                entity.setSize(width * 1.45F, height * 1.35F);
                poseStack.translate(0, ((height * 1.35F) - height) / 2, 0);
                renderEntityInSpawner(entityYaw, poseStack, buffer, packedLight, frozenEntity, this.entityRenderer);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public static void renderEntityInSpawner(float entityYaw, PoseStack poseStack, MultiBufferSource buffer, int packedLight, Entity entity,
                                             EntityRenderDispatcher renderDispatcher) {
        poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(0.0F));
            renderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, entityYaw, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
