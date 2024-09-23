package com.wenxin2.marioverse.client.renderers.accesories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.init.ConfigRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface SimpleItemRenderer extends AccessoryRenderer {
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    default <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, PoseStack poseStack) {
//        align(stack, reference, (HumanoidModel<M>) model, poseStack);
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, HumanoidModel<M> model, PoseStack matrices);

    @Override
    default <M extends LivingEntity> void render(ItemStack stack, SlotReference slotReference, PoseStack poseStack,
                                                        EntityModel<M> model, MultiBufferSource buffer,
                                                        int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                        float ageInTicks, float netHeadYaw, float headPitch) {
        if (ConfigRegistry.RENDER_ONE_UP_CURIO.get()) {
            poseStack.pushPose();
//                if (model instanceof HumanoidModel<M> humanoidModel)
//                    AccessoryRenderer.followBodyRotations(slotReference.entity(), (HumanoidModel<M>) humanoidModel);
            poseStack.mulPose(Direction.UP.getRotation());
            poseStack.translate(0.15F, 0.45F, -0.13F);
            poseStack.scale(0.25F, 0.25F, 0.25F);
            Minecraft.getInstance().getItemRenderer()
                    .renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY,
                            poseStack, buffer, slotReference.entity().level(), 0);
            poseStack.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    <M extends LivingEntity> void render (ItemStack stack, SlotReference slotReference, PoseStack poseStack,
                                          HumanoidModel<LivingEntity> model, MultiBufferSource buffer,
                                          int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                          float ageInTicks, float netHeadYaw, float headPitch);
}
