package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.client.models.entities.HeftyGoombaModel;
import com.wenxin2.marioverse.entities.HeftyGoombaEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

public class HeftyGoombaRenderer extends GeoEntityRenderer<HeftyGoombaEntity> {
    private static final String HELMET = "armorBipedHead";
    protected ItemStack helmetItem;

    public HeftyGoombaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HeftyGoombaModel());
        addRenderLayer(new ItemArmorGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, HeftyGoombaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> animatable.getItemBySlot(EquipmentSlot.HEAD);
                    default -> null;
                };
            }

            @NotNull
            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, HeftyGoombaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> EquipmentSlot.HEAD;
                    default -> super.getEquipmentSlotForBone(bone, stack, animatable);
                };
            }

            @NotNull
            @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, HeftyGoombaEntity animatable, HumanoidModel<?> baseModel) {
                return switch (bone.getName()) {
                    case HELMET -> baseModel.head;
                    default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
                };
            }

            @Override
            protected void renderSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock, MultiBufferSource bufferSource, int packedLight) {
                poseStack.scale(2.0F, 2.0F, 1.975F);
                poseStack.translate(0.0F, -0.535F, 0.0F);
                super.renderSkullAsArmor(poseStack, bone, stack, skullBlock, bufferSource, packedLight);
            }
        });

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, HeftyGoombaEntity animatable) {
                if (!(animatable.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem)
                        && !(animatable.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock() instanceof SkullBlock)) {
                    return switch (bone.getName()) {
                        case HELMET -> animatable.getItemBySlot(EquipmentSlot.HEAD);
                        default -> null;
                    };
                }
                else return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, HeftyGoombaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> ItemDisplayContext.HEAD;
                    default -> ItemDisplayContext.NONE;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, HeftyGoombaEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.scale(1.3F, 1.2F, 1.185F);
                poseStack.translate(0.0F, 0.5F, 0.0F);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    protected float getDeathMaxRotation(HeftyGoombaEntity animatable) {
        return 0.0F;
    }
}
