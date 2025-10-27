package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.BooModel;
import com.wenxin2.marioverse.entities.BooEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

public class BooRenderer extends GeoEntityRenderer<BooEntity> {
    private static final String HELMET = "armorBipedHead";
    private static final String LEFT_ARM = "bipedLeftArm";
    private static final String RIGHT_ARM = "bipedRightArm";
    protected ItemStack mainHandItem;
    protected ItemStack offhandItem;

    public BooRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BooModel());
        this.shadowRadius = 0.6F;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));

        this.addRenderLayer(new ItemArmorGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, BooEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> animatable.getItemBySlot(EquipmentSlot.HEAD);
                    default -> null;
                };
            }

            @NotNull
            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, BooEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> EquipmentSlot.HEAD;
                    case RIGHT_ARM -> !animatable.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                    case LEFT_ARM -> animatable.isLeftHanded() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                    default -> super.getEquipmentSlotForBone(bone, stack, animatable);
                };
            }

            @NotNull
            @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, BooEntity animatable, HumanoidModel<?> baseModel) {
                return switch (bone.getName()) {
                    case HELMET -> baseModel.head;
                    case LEFT_ARM -> baseModel.leftArm;
                    case RIGHT_ARM -> baseModel.rightArm;
                    default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
                };
            }

            @Override
            protected void renderSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock,
                                              MultiBufferSource bufferSource, int packedLight) {
                poseStack.scale(1.2F, 1.2F, 1.2F);
                poseStack.translate(0.0F, -0.2F, 0.0F);
                super.renderSkullAsArmor(poseStack, bone, stack, skullBlock, bufferSource, packedLight);
            }
        });

        this.addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, BooEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> {
                        if (!(animatable.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem)
                                && !(animatable.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BlockItem blockItem
                                && blockItem.getBlock() instanceof SkullBlock)) {
                            yield animatable.getItemBySlot(EquipmentSlot.HEAD);
                        }
                        yield ItemStack.EMPTY;
                    }
                    case LEFT_ARM -> animatable.isLeftHanded() ?
                            BooRenderer.this.mainHandItem : BooRenderer.this.offhandItem;
                    case RIGHT_ARM -> animatable.isLeftHanded() ?
                            BooRenderer.this.offhandItem : BooRenderer.this.mainHandItem;
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, BooEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> ItemDisplayContext.HEAD;
                    case LEFT_ARM, RIGHT_ARM -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    default -> ItemDisplayContext.NONE;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, BooEntity animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (stack == BooRenderer.this.mainHandItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(-90F));
                    poseStack.translate(0.1, -0.05, -0.325);

                    if (stack.getItem() instanceof ShieldItem)
                        poseStack.translate(0.02, 0.0, -0.125);
                } else if (stack == BooRenderer.this.offhandItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(90F));
                    poseStack.translate(-0.1, -0.05, -0.325);

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(0.02, 0.0, 0.4);
                        poseStack.mulPose(Axis.YP.rotationDegrees(180));
                    }
                } if (stack.getItem() instanceof BlockItem) {
                    poseStack.scale(0.875F, 0.875F, 0.875F);
                    poseStack.translate(0.0F, 0.2F, 0.0F);
                } else {
                    poseStack.translate(0.0F, 0.2F, 0.0F);
                }
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public void preRender(PoseStack poseStack, BooEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        this.mainHandItem = animatable.getMainHandItem();
        this.offhandItem = animatable.getOffhandItem();
    }

    @Override
    protected float getDeathMaxRotation(BooEntity animatable) {
        if (animatable.getLastDamageSource() != null
                && animatable.getLastDamageSource().is(DamageTypes.GENERIC))
            return 0.0F;
        return super.getDeathMaxRotation(animatable);
    }
}
