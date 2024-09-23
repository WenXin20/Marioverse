package com.wenxin2.marioverse.client.renderers.accesories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.client.renderers.ArmorRenderingExtension;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.client.Side;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CostumeAccessoryRenderer implements AccessoryRenderer {

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference slotReference, PoseStack poseStack,
                                                EntityModel<M> model, MultiBufferSource buffer,
                                                int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                float ageInTicks, float netHeadYaw, float headPitch) {
        poseStack.pushPose();
            ArmorRenderingExtension.RENDERER.render(Items.DIAMOND_HELMET.getDefaultInstance(), slotReference, poseStack, model,
                    buffer, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        poseStack.popPose();
    }
}
