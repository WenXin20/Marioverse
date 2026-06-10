package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.client.models.entities.SpinyCheepCheepModel;
import com.wenxin2.marioverse.entities.SpinyCheepCheepEntity;
import com.wenxin2.marioverse.entities.variants.CheepCheepVariants;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

public class SpinyCheepCheepRenderer extends GeoEntityRenderer<SpinyCheepCheepEntity> {
    private static final String HELMET = "armorBipedHead";
    public SpinyCheepCheepRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpinyCheepCheepModel());
        this.shadowRadius = 0.6F;

        this.addRenderLayer(new ItemArmorGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, SpinyCheepCheepEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> animatable.getItemBySlot(EquipmentSlot.HEAD);
                    default -> null;
                };
            }

            @NotNull
            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, SpinyCheepCheepEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> EquipmentSlot.HEAD;
                    default -> super.getEquipmentSlotForBone(bone, stack, animatable);
                };
            }

            @NotNull
            @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, SpinyCheepCheepEntity animatable, HumanoidModel<?> baseModel) {
                return switch (bone.getName()) {
                    case HELMET -> baseModel.head;
                    default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
                };
            }

            @Override
            protected void renderSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock,
                                              MultiBufferSource bufferSource, int packedLight) {
                poseStack.scale(1.3F, 1.3F, 1.3F);
                poseStack.translate(0.0F, -0.1F, 0.0F);
                super.renderSkullAsArmor(poseStack, bone, stack, skullBlock, bufferSource, packedLight);
            }
        });

        this.addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, SpinyCheepCheepEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> {
                        if (!(animatable.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem)
                                && !(animatable.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BlockItem blockItem
                                && blockItem.getBlock() instanceof SkullBlock)) {
                            yield animatable.getItemBySlot(EquipmentSlot.HEAD);
                        }
                        yield ItemStack.EMPTY;
                    }
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, SpinyCheepCheepEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> ItemDisplayContext.HEAD;
                    default -> ItemDisplayContext.NONE;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, SpinyCheepCheepEntity animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (stack.getItem() instanceof BlockItem) {
                    poseStack.scale(0.875F, 0.875F, 0.875F);
                    poseStack.translate(0.0F, 0.2F, 0.0F);
                } else poseStack.translate(0.0F, 0.2F, 0.0F);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, SpinyCheepCheepEntity animatable,
                                    BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = 1.0F;
        String size = animatable.getSize();

        if (size.equals(CheepCheepVariants.SMALL))
            scale = 0.75F;
        else if (size.equals(CheepCheepVariants.LARGE))
            scale = 1.25F;

        poseStack.scale(scale, scale, scale);
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
