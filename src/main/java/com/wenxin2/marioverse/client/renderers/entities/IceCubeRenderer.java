package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.IceCubeModel;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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
        Entity frozenPlayer = entity.getPlayer(entity.level());

        if (frozenEntity != null) {
            float scale = 1.0F;
            float heightScale = 1.0F;
            float widthScale = 1.0F;

            if (frozenEntity instanceof LivingEntity livingEntity) {
                if (livingEntity.getPersistentData().contains("Scale"))
                    scale = livingEntity.getPersistentData().getFloat("Scale");
                if (livingEntity.getPersistentData().contains("HeightScale"))
                    heightScale = livingEntity.getPersistentData().getFloat("HeightScale");
                if (livingEntity.getPersistentData().contains("WidthScale"))
                    widthScale = livingEntity.getPersistentData().getFloat("WidthScale");
            }
            float height = frozenEntity.getBbHeight() * scale * heightScale * 1.55F;
            float width = frozenEntity.getBbWidth() * scale * widthScale * 1.55F;

            if (frozenEntity.getBbHeight() >= frozenEntity.getBbWidth() * 3)
                width *= 2.0F;

            poseStack.pushPose();
                this.withScale(width, height);
                entity.setSize(width, height);

                poseStack.translate(0, (height - (height / 1.55F)) / 2, 0);
                poseStack.scale(entity.getDataScale() * entity.getDataWidthScale(),
                        entity.getDataScale() * entity.getDataHeightScale(), entity.getDataScale() * entity.getDataWidthScale());

                renderEntityInIceCube(frozenEntity.getYRot(), poseStack, buffer, packedLight, frozenEntity, this.entityRenderer);
            poseStack.popPose();
        }

        if (frozenPlayer != null) {
            float width = frozenPlayer.getDimensions(Pose.STANDING).width() * 2.55F;
            float height = frozenPlayer.getDimensions(Pose.STANDING).height() * 1.55F;

            poseStack.pushPose();
                this.withScale(width, height);
                entity.setSize(width, height);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public static void renderEntityInIceCube(float entityYaw, PoseStack poseStack, MultiBufferSource buffer, int packedLight, Entity entity,
                                             EntityRenderDispatcher renderDispatcher) {
        poseStack.pushPose();
            poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw));
                poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
            poseStack.popPose();
            renderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, entityYaw, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
