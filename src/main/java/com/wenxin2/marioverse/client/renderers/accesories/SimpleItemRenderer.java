package com.wenxin2.marioverse.client.renderers.accesories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.init.ConfigRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface SimpleItemRenderer extends AccessoryRenderer {

    @Override
    default <M extends LivingEntity> void render(ItemStack stack, SlotReference slotReference, PoseStack poseStack,
                                                        EntityModel<M> model, MultiBufferSource buffer,
                                                        int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                        float ageInTicks, float netHeadYaw, float headPitch) {
        if (ConfigRegistry.RENDER_ONE_UP_CURIO.get()) {
            poseStack.pushPose();
            poseStack.mulPose(Direction.UP.getRotation());
            poseStack.scale(0.25F, 0.25F, 0.25F);
            Minecraft.getInstance().getItemRenderer()
                    .renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY,
                            poseStack, buffer, slotReference.entity().level(), 0);
            poseStack.popPose();
        }
    }

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
