package com.wenxin2.marioverse.client.renderers.entities;

import com.wenxin2.marioverse.client.models.entities.MiniGoombaModel;
import com.wenxin2.marioverse.entities.MiniGoombaEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

public class MiniGoombaRenderer extends GeoEntityRenderer<MiniGoombaEntity> {
    private static final String HELMET = "armorBipedHead";

    public MiniGoombaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MiniGoombaModel());
        this.withScale(0.25F);
        addRenderLayer(new ItemArmorGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, MiniGoombaEntity animatable) {
                // Return the items relevant to the bones being rendered for additional rendering
                return switch (bone.getName()) {
                    case HELMET -> animatable.getItemBySlot(EquipmentSlot.HEAD);
                    default -> null;
                };
            }

            // Return the equipment slot relevant to the bone we're using
            @NotNull
            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, MiniGoombaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> EquipmentSlot.HEAD;
                    default -> super.getEquipmentSlotForBone(bone, stack, animatable);
                };
            }

            // Return the ModelPart responsible for the armor pieces to render
            @NotNull
            @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, MiniGoombaEntity animatable, HumanoidModel<?> baseModel) {
                return switch (bone.getName()) {
                    case HELMET -> baseModel.head;
                    default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
                };
            }
        });
    }

    @Override
    protected float getDeathMaxRotation(MiniGoombaEntity animatable) {
        return 0.0F;
    }
}
