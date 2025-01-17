package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.PiranhaPlantModel;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.joml.Vector3f;
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

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            renderHitboxes(poseStack, vertexConsumer, entity, 1.0F, 0.0F, 1.0F, 0.0F);
        }
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

    private static void renderHitboxes(PoseStack poseStack, VertexConsumer vertexConsumer, PiranhaPlantEntity entity,
                                       float mainHitbox, float red, float green, float blue) {
        AABB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aabb, red, green, blue, 1.0F);
        if (entity.isMultipartEntity()) {
            double d0 = -Mth.lerp(mainHitbox, entity.xOld, entity.getX());
            double d1 = -Mth.lerp(mainHitbox, entity.yOld, entity.getY());
            double d2 = -Mth.lerp(mainHitbox, entity.zOld, entity.getZ());

            for (PartEntity<?> part : entity.getParts()) {
                poseStack.pushPose();
                double d3 = d0 + Mth.lerp(mainHitbox, part.xOld, part.getX());
                double d4 = d1 + Mth.lerp(mainHitbox, part.yOld, part.getY());
                double d5 = d2 + Mth.lerp(mainHitbox, part.zOld, part.getZ());
                poseStack.translate(d3, d4, d5);
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, part.getBoundingBox().move(-part.getX(), -part.getY(), -part.getZ()),
                        0.25F, 1.0F, 0.0F, 1.0F);
                poseStack.popPose();
            }
        }

        float f1 = 0.01F;
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aabb.minX,
                entity.getEyeHeight() - 0.01F, aabb.minZ, aabb.maxX,
                entity.getEyeHeight() + 0.01F, aabb.maxZ,
                1.0F, 0.0F, 0.0F, 1.0F);

        renderVector(poseStack, vertexConsumer, new Vector3f(0.0F, entity.getEyeHeight(), 0.0F), entity.getViewVector(mainHitbox).scale(2.0), -16776961);
    }

    private static void renderVector(PoseStack poseStack, VertexConsumer vertexConsumer, Vector3f vector3f, Vec3 vec3, int color) {
        PoseStack.Pose posestack$pose = poseStack.last();
        vertexConsumer.addVertex(posestack$pose, vector3f)
                .setColor(color)
                .setNormal(posestack$pose, (float) vec3.x, (float) vec3.y, (float) vec3.z);
        vertexConsumer.addVertex(
                        posestack$pose,
                        (float) ((double) vector3f.x() + vec3.x),
                        (float) ((double) vector3f.y() + vec3.y),
                        (float) ((double) vector3f.z() + vec3.z))
                .setColor(color)
                .setNormal(posestack$pose, (float) vec3.x, (float) vec3.y, (float) vec3.z);
    }
}
