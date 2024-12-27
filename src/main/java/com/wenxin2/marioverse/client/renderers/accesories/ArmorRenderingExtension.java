package com.wenxin2.marioverse.client.renderers.accesories;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.mixin.client.LivingEntityRendererAccessor;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ArmorRenderingExtension<T extends LivingEntity, A extends HumanoidModel<T>> {
    AccessoryRenderer RENDERER = new AccessoryRenderer() {
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model,
                                                    MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount,
                                                    float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            EntityRenderer<? super LivingEntity> entityRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(reference.entity());
            if (entityRender instanceof LivingEntityRendererAccessor<?, ?> accessor) {
                Item var16 = stack.getItem();
                if (var16 instanceof Equipable equipable) {
                    EquipmentSlot equipmentSlot = equipable.getEquipmentSlot();
                    Optional var17 = accessor.getLayers().stream().filter((renderLayer) -> {
                        return renderLayer instanceof io.wispforest.accessories.api.client.ArmorRenderingExtension
                                || renderLayer instanceof ArmorRenderingExtension;
                    }).findFirst();
                    if (model instanceof HumanoidModel<M> humanoidModel) {
                        var17.ifPresent((layer) -> {
                            ((ArmorRenderingExtension) layer).renderEquipmentStack(stack, matrices, multiBufferSource, reference.entity(),
                                    equipmentSlot, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, humanoidModel);
                        });
                    }
                }
            }
        }
    };

    default void renderEquipmentStack(ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot,
                                      int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                                      float netHeadYaw, float headPitch, A baseModel) {
        throw new IllegalStateException("Injected interface method is unimplemented!");
    }
}