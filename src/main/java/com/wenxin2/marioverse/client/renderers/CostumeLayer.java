package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public class CostumeLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final HumanoidArmorModel<T> humanoidArmorModelInner;
    private final HumanoidArmorModel<T> humanoidArmorModelOuter;

    public CostumeLayer(RenderLayerParent<T, M> layerParent, EntityModelSet modelSet) {
        super(layerParent);
        humanoidArmorModelInner = new HumanoidArmorModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        humanoidArmorModelOuter = new HumanoidArmorModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer,
                       int light, @Nonnull T livingEntity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        poseStack.pushPose();
        this.getParentModel().copyPropertiesTo(this.humanoidArmorModelInner);
        this.getParentModel().copyPropertiesTo(this.humanoidArmorModelOuter);
        this.humanoidArmorModelInner.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks,
                netHeadYaw, headPitch);
        this.humanoidArmorModelOuter.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks,
                netHeadYaw, headPitch);
        poseStack.popPose();
    }
}
