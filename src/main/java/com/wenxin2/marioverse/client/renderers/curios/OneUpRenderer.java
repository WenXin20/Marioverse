package com.wenxin2.marioverse.client.renderers.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class OneUpRenderer implements ICurioRenderer, SimpleItemRenderer {
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
           PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer,
           int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (ConfigRegistry.RENDER_ONE_UP_CHARM.get()) {
            poseStack.pushPose();
            this.translateIfSneaking(poseStack, slotContext.entity());
            EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getEntityRenderDispatcher()
                    .getRenderer(slotContext.entity());
            if (render instanceof LivingEntityRenderer) {
                if (renderLayerParent.getModel() instanceof HumanoidModel humanoidModel)
                    this.rotateIfSneaking(poseStack, slotContext.entity(), humanoidModel);
            }
            poseStack.mulPose(Direction.DOWN.getRotation());
            poseStack.translate(0.15F, -0.45F, 0.145F);
            poseStack.scale(0.25F, 0.25F, 0.45F);
            Minecraft.getInstance().getItemRenderer()
                    .renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY,
                            poseStack, buffer, slotContext.entity().level(), 0);
            poseStack.popPose();
        }
    }
}
