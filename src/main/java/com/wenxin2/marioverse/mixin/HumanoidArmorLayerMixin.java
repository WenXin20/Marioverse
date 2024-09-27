package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.client.renderers.ArmorRenderingExtension;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.Color;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, A extends HumanoidModel<T>> implements ArmorRenderingExtension<T, A> {

    @Shadow
    protected abstract void renderArmorPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity,
                                             EquipmentSlot equipmentSlot, int i, A humanoidModel);

    @Shadow
    protected abstract void renderTrim(Holder<ArmorMaterial> armorMaterial, PoseStack poseStack, MultiBufferSource buffer,
                            int i, ArmorTrim trim, Model model, boolean humanoidModel);

    @Shadow
    private A getArmorModel(EquipmentSlot slot) { return null; }

    @Shadow
    private boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    @Shadow @Final private A innerModel;
    @Unique
    @Nullable
    private ItemStack tempStack = null;

    @Override
    public void renderArmorPiece(ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity,
                                     EquipmentSlot equipmentSlot, int light, A baseModel) {
        this.tempStack = stack;

        HumanoidModel<?> geckolibModel = GeoRenderProvider.of(stack).getGeoArmorRenderer(livingEntity, stack, equipmentSlot, baseModel);

        if (geckolibModel instanceof GeoArmorRenderer<?> geoArmorRenderer) {
            geoArmorRenderer.prepForRender(livingEntity, stack, equipmentSlot, baseModel);
            geoArmorRenderer.renderToBuffer(poseStack, null,
                    light, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());
            baseModel.copyPropertiesTo((A)geckolibModel);
            ArmorTrim armortrim = stack.get(DataComponents.TRIM);
            if (stack.getItem() instanceof ArmorItem armoritem && armortrim != null) {
                this.renderTrim(armoritem.getMaterial(), poseStack, multiBufferSource, light, armortrim, baseModel, this.usesInnerModel(equipmentSlot));
            }
        } else {
            this.renderArmorPiece(poseStack, multiBufferSource, livingEntity, equipmentSlot, light, this.getArmorModel(equipmentSlot));
        }

        this.tempStack = null;
    }

    @WrapOperation(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack getAlternativeStack(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        if (tempStack != null) return tempStack;

        return original.call(instance, equipmentSlot);
    }
}