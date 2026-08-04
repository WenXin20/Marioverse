package com.wenxin2.marioverse.client.renderers.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.client.renderers.accesories.ArmorRenderingExtension;
import com.wenxin2.marioverse.mixin.LivingEntityRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CostumeCurioRenderer implements ICurioRenderer {
    private final EquipmentSlot equipmentSlot;

    public CostumeCurioRenderer(EquipmentSlot equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
          PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource bufferSource,
          int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        @SuppressWarnings("unchecked")
        T entity = (T) slotContext.entity();

        EntityRenderer<?> entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer))
            return;
        if (!(renderLayerParent.getModel() instanceof HumanoidModel))
            return;

        @SuppressWarnings("unchecked")
        LivingEntityRendererAccessor<T, M> accessor = (LivingEntityRendererAccessor<T, M>) livingRenderer;

        for (RenderLayer<T, M> layer : accessor.mv$getLayers()) {
            if (layer instanceof ArmorRenderingExtension) {
                @SuppressWarnings("unchecked")
                ArmorRenderingExtension<T, HumanoidModel<T>> armorLayer =
                        (ArmorRenderingExtension<T, HumanoidModel<T>>) layer;
                @SuppressWarnings("unchecked")
                HumanoidModel<T> baseModel = (HumanoidModel<T>) renderLayerParent.getModel();

                armorLayer.renderEquipmentStack(stack, poseStack, bufferSource, entity, this.equipmentSlot,
                        light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, baseModel);
                return;
            }
        }
    }
}