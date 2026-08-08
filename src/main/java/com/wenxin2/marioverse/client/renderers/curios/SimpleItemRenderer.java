package com.wenxin2.marioverse.client.renderers.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

public interface SimpleItemRenderer {
    default void translateIfSneaking(PoseStack poseStack, LivingEntity livingEntity) {
        if (livingEntity.isCrouching()) {
            poseStack.translate(0.0F, 0.1875F, 0.0F);
        }
    }

    default void rotateIfSneaking(PoseStack poseStack, LivingEntity livingEntity, HumanoidModel<LivingEntity> model) {
        if (livingEntity.isCrouching()) {
            EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(livingEntity);
            if (render instanceof LivingEntityRenderer) {
                if (model instanceof HumanoidModel) {
                    poseStack.mulPose(Axis.XP.rotation(model.body.xRot));
                }
            }
        }
    }
}
