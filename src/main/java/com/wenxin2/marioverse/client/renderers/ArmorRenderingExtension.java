package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.mixin.LivingEntityRendererMixinAccessor;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

public interface ArmorRenderingExtension<T extends LivingEntity, A extends HumanoidModel<T>> {

    AccessoryRenderer RENDERER = new AccessoryRenderer() {
        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
                                                    EntityModel<M> model, MultiBufferSource multiBufferSource,
                                                    int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                    float ageInTicks, float netHeadYaw, float headPitch) {
            var entityRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(reference.entity());

            if (!(entityRender instanceof LivingEntityRendererMixinAccessor<?, ?> accessor)) return;
            if (!(stack.getItem() instanceof Equipable equipable)) return;
            var equipmentSlot = equipable.getEquipmentSlot();
            var possibleLayer = accessor.getLayers().stream()
                    .filter(renderLayer -> renderLayer instanceof ArmorRenderingExtension)
                    .findFirst();

            if (model instanceof HumanoidModel<M> humanoidModel)
                possibleLayer.ifPresent(layer ->
                        ((ArmorRenderingExtension) layer).renderArmorPiece(stack, matrices, multiBufferSource, reference.entity(), equipmentSlot, light, humanoidModel));
        }
    };

    default void renderArmorPiece(ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource,
                                      T livingEntity, EquipmentSlot equipmentSlot, int light, A baseModel) {
        throw new IllegalStateException("Injected interface method is unimplemented!");
    }
}