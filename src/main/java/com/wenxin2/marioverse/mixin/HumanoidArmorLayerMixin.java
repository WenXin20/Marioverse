package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.client.renderers.accesories.ArmorRenderingExtension;
import io.wispforest.accessories.AccessoriesLoaderInternals;
import io.wispforest.accessories.compat.GeckoLibCompat;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.Color;

@Mixin({HumanoidArmorLayer.class})
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>,
        A extends HumanoidModel<T>> extends RenderLayer<T, M> implements ArmorRenderingExtension<T, A> {
    @Unique
    private @Nullable ItemStack tempStack = null;

    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Shadow
    protected abstract void renderArmorPiece(PoseStack var1, MultiBufferSource var2, T var3, EquipmentSlot var4, int var5, A var6, float var7, float var8, float var9, float var10, float var11, float var12);

    @Shadow
    private A getArmorModel(EquipmentSlot slot) {
        return null;
    }

    @Shadow
    protected abstract void setPartVisibility(A var1, EquipmentSlot var2);

    public void renderEquipmentStack(ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource,
                                     T livingEntity, EquipmentSlot equipmentSlot, int light, float limbSwing, float limbSwingAmount,
                                     float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, A baseModel) {
        this.tempStack = stack;

        HumanoidModel<?> geckolibModel = GeoRenderProvider.of(stack).getGeoArmorRenderer(livingEntity, stack, equipmentSlot, baseModel);
        if (geckolibModel instanceof GeoArmorRenderer<?> geoArmorRenderer) {
            geoArmorRenderer.prepForRender(livingEntity, stack, equipmentSlot, baseModel, multiBufferSource, partialTicks, limbSwing, limbSwingAmount, netHeadYaw, headPitch);
            geoArmorRenderer.renderToBuffer(poseStack, null,
                    light, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());
            baseModel.copyPropertiesTo((A)geckolibModel);
        } else this.renderArmorPiece(poseStack, multiBufferSource, livingEntity, equipmentSlot, light, this.getArmorModel(equipmentSlot), limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        this.tempStack = null;
    }

    @WrapOperation(
            method = {"renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            )}
    )
    private ItemStack getAlternativeStack(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        return this.tempStack != null ? this.tempStack : (ItemStack)original.call(instance, equipmentSlot);
    }

    @Unique
    private boolean attemptGeckoRender(ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        return !AccessoriesLoaderInternals.isModLoaded("geckolib") ? false : GeckoLibCompat.renderGeckoArmor(poseStack, multiBufferSource, livingEntity, stack, equipmentSlot, (HumanoidModel)this.getParentModel(), this.getArmorModel(equipmentSlot), partialTicks, light, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, this::setPartVisibility);
    }
}
