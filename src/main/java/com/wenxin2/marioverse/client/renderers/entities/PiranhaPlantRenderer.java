package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.PiranhaPlantModel;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PiranhaPlantRenderer extends GeoEntityRenderer<PiranhaPlantEntity> {
    public PiranhaPlantRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PiranhaPlantModel());
    }

    @Override
    protected float getDeathMaxRotation(PiranhaPlantEntity animatable) {
        return 0.0F;
    }

    @Override
    public void render(PiranhaPlantEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes())
            renderHitboxes(poseStack, vertexConsumer, entity);
    }

    @Override
    protected void applyRotations(PiranhaPlantEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        if (animatable.getAttachedSide() != null) {
            switch (animatable.getAttachedSide()) {
                case UP:
                    break;
                case DOWN:
                    poseStack.mulPose(Axis.XP.rotationDegrees(180));
                    poseStack.translate(0, -1.0, 0);
                    break;
                case NORTH:
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                    poseStack.translate(0, -0.5, 0.5);
                    break;
                case SOUTH:
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    poseStack.translate(0, -0.5, -0.5);
                    break;
                case EAST:
                    poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                    poseStack.translate(-0.5, -0.5, 0);
                    break;
                case WEST:
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                    poseStack.translate(0.5, -0.5, 0);
                    break;
            }
        }

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
    }

    private static void renderHitboxes(PoseStack poseStack, VertexConsumer vertexConsumer, PiranhaPlantEntity entity) {
        AABB mainBoundingBox = entity.makeBoundingBox();
        AABB headBoundingBox = entity.head.makeBoundingBox();
        AABB relativeBox = mainBoundingBox.move(-entity.getX(), -entity.getY(), -entity.getZ());
        AABB relativeBoxInflate = mainBoundingBox.move(-entity.getX(), -entity.getY(), -entity.getZ()).inflate(0.01);

        if (entity.isMultipartEntity()) {
            AABB piranhaPart = headBoundingBox.move(-entity.head.getX(), -entity.head.getY(), -entity.head.getZ());

            poseStack.pushPose();
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, piranhaPart, 0.25F, 1.0F, 0.0F, 1.0F);
            poseStack.popPose();
        }

        poseStack.pushPose();
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, relativeBox, 0.3F, 0.4F, 0.5F, 1.0F);
        poseStack.popPose();

        poseStack.pushPose();
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, relativeBoxInflate, 0.5F, 0.8F, 1.0F, 0.5F);
        poseStack.popPose();
    }
}
