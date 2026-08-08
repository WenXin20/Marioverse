package com.wenxin2.marioverse.client.renderers.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ArmorRenderingExtension<T extends LivingEntity, A extends HumanoidModel<T>> {
    default void renderEquipmentStack(ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot,
                                      int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                                      float netHeadYaw, float headPitch, A baseModel) {
        throw new IllegalStateException("Injected interface method is unimplemented!");
    }
}