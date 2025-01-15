package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.PiranhaPlantModel;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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
                    poseStack.translate(0, 0, 0.5D);
                    break;
                case SOUTH:
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    poseStack.translate(0, 0, -0.5D);
                    break;
                case EAST:
                    poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                    poseStack.translate(-0.5D, 0, 0);
                    break;
                case WEST:
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                    poseStack.translate(0.5D, 0, 0);
                    break;
            }
        }

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
    }
}
