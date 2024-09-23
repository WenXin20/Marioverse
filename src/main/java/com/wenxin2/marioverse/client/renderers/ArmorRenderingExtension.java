package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface ArmorRenderingExtension<T extends LivingEntity, A extends HumanoidModel<T>> {

    AccessoryRenderer RENDERER = new AccessoryRenderer() {
        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
                                                    EntityModel<M> model, MultiBufferSource multiBufferSource,
                                                    int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                    float ageInTicks, float netHeadYaw, float headPitch) {

            if (stack.getItem() instanceof Equipable equipable) {
                var equipmentSlot = equipable.getEquipmentSlot();
                var entityRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(reference.entity());

                if (model instanceof HumanoidModel<M> humanoidModel && entityRender instanceof LivingEntityRendererAccessor<?, ?> accessor) {
                    var possibleLayer = accessor.getLayers().stream()
                            .filter(renderLayer -> renderLayer instanceof HumanoidArmorLayer<?, ?, ?>)
                            .findFirst();

                    possibleLayer.ifPresent(layer -> {
                        ((ArmorRenderingExtension) layer).renderArmorPiece(stack, matrices, multiBufferSource, reference.entity(),
                                equipmentSlot, light, humanoidModel);

//                        ((ArmorRenderingExtension) layer).renderArmorPiece(Items.DIAMOND_HELMET.getDefaultInstance(), matrices, multiBufferSource, reference.entity(),
//                                EquipmentSlot.HEAD, light, ((ArmorRenderingExtension<?, ?>) layer).getArmorModel(EquipmentSlot.HEAD));
                    });
                }
            }
        }
    };

    default void renderArmorPiece(ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource,
                                  T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel) {

        throw new IllegalStateException("Injected interface method is unimplemented!");
    }

    default A getArmorModel(EquipmentSlot slot) {
        return null;
    }
}