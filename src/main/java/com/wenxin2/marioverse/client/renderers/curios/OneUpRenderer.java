package com.wenxin2.marioverse.client.renderers.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.init.ConfigRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class OneUpRenderer implements ICurioRenderer {
    public static void render(LivingEntity livingEntity, EntityModel<? extends LivingEntity> model,
                              PoseStack poseStack, ItemStack stack, MultiBufferSource buffer,
                              int light) {

        if (ConfigRegistry.RENDER_ONE_UP_CURIO.get()) {
            poseStack.pushPose();
                ICurioRenderer.translateIfSneaking(poseStack, livingEntity);
                ICurioRenderer.rotateIfSneaking(poseStack, livingEntity);
                poseStack.translate(0.15F, 0.45F, -0.13F);
                poseStack.scale(0.25F, 0.25F, 0.25F);
                poseStack.mulPose(Direction.DOWN.getRotation());
                Minecraft.getInstance().getItemRenderer()
                        .renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY,
                                poseStack, buffer, livingEntity.level(), 0);
            poseStack.popPose();
        }
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack,
                                                                          RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer,
                                                                          int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                                          float ageInTicks, float netHeadTaw, float headPitch) {
        render(slotContext.entity(), renderLayerParent.getModel(), poseStack, stack, buffer, light);
    }
}
