package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.KoopaTroopaModel;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;
import software.bernie.geckolib.renderer.specialty.DynamicGeoEntityRenderer;

public class KoopaTroopaRenderer extends DynamicGeoEntityRenderer<KoopaTroopaEntity> {
    private static final String HELMET = "armorHead";
    private static final String CHEST = "armorBody";
    private static final String LEFT_ARM = "armorLeftArm";
    private static final String RIGHT_ARM = "armorRightArm";
    private static final String LEFT_LEG = "armorLeftLeg";
    private static final String RIGHT_LEG = "armorRightLeg";
    private static final String LEFT_BOOT = "armorLeftBoot";
    private static final String RIGHT_BOOT = "armorRightBoot";
    protected ItemStack mainHandItem;
    protected ItemStack offhandItem;

    public KoopaTroopaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KoopaTroopaModel());
        this.shadowRadius = 0.5F;

        addRenderLayer(new ItemArmorGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, KoopaTroopaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> this.helmetStack;
                    case CHEST, LEFT_ARM, RIGHT_ARM -> this.chestplateStack;
                    case LEFT_LEG, RIGHT_LEG -> this.leggingsStack;
                    case LEFT_BOOT, RIGHT_BOOT -> this.bootsStack;
                    default -> null;
                };
            }

            @NotNull
            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, KoopaTroopaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> EquipmentSlot.HEAD;
                    case CHEST -> EquipmentSlot.CHEST;
                    case RIGHT_ARM -> !animatable.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                    case LEFT_ARM -> animatable.isLeftHanded() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                    case LEFT_LEG, RIGHT_LEG -> EquipmentSlot.LEGS;
                    case LEFT_BOOT, RIGHT_BOOT -> EquipmentSlot.FEET;
                    default -> super.getEquipmentSlotForBone(bone, stack, animatable);
                };
            }

            @NotNull
            @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, KoopaTroopaEntity animatable, HumanoidModel<?> baseModel) {
                return switch (bone.getName()) {
                    case HELMET -> baseModel.head;
                    case CHEST -> baseModel.body;
                    case LEFT_ARM -> baseModel.leftArm;
                    case RIGHT_ARM -> baseModel.rightArm;
                    case LEFT_LEG, LEFT_BOOT -> baseModel.leftLeg;
                    case RIGHT_LEG, RIGHT_BOOT -> baseModel.rightLeg;
                    default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
                };
            }

            @Override
            protected void renderSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock,
                                              MultiBufferSource bufferSource, int packedLight) {
                poseStack.scale(1.0F, 1.0F, 0.94F);
                poseStack.translate(0.0F, 0.0F, 0.0F);
                super.renderSkullAsArmor(poseStack, bone, stack, skullBlock, bufferSource, packedLight);
            }

            private void renderGoombellaSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock,
                                                     MultiBufferSource bufferSource, int packedLight) {
                poseStack.scale(1.28F, 1.28F, 1.22F);
                poseStack.translate(0.0F, -0.125F, 0.0F);
                super.renderSkullAsArmor(poseStack, bone, stack, skullBlock, bufferSource, packedLight);
            }
        });

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, KoopaTroopaEntity animatable) {
                if (!(animatable.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem)
                        && !(animatable.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock() instanceof SkullBlock)) {
                    return switch (bone.getName()) {
                        case HELMET -> animatable.getItemBySlot(EquipmentSlot.HEAD);
                        default -> null;
                    };
                }
                else return switch (bone.getName()) {
                    case LEFT_ARM -> animatable.isLeftHanded() ?
                            KoopaTroopaRenderer.this.mainHandItem : KoopaTroopaRenderer.this.offhandItem;
                    case RIGHT_ARM -> animatable.isLeftHanded() ?
                            KoopaTroopaRenderer.this.offhandItem : KoopaTroopaRenderer.this.mainHandItem;
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, KoopaTroopaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> ItemDisplayContext.HEAD;
                    case LEFT_ARM, RIGHT_ARM -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    default -> ItemDisplayContext.NONE;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, KoopaTroopaEntity animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (stack == KoopaTroopaRenderer.this.mainHandItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem)
                        poseStack.translate(0, 0.125, -0.25);
                } else if (stack == KoopaTroopaRenderer.this.offhandItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(0, 0.125, 0.25);
                        poseStack.mulPose(Axis.YP.rotationDegrees(180));
                    }
                } else {
                    poseStack.scale(0.6F, 0.6F, 0.55F);
                    poseStack.translate(0.0F, 0.5F, 0.0F);
                }
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

//    @Override
//    protected float getDeathMaxRotation(KoopaTroopaEntity animatable) {
//        return 0.0F;
//    }

    @Override
    public void preRender(PoseStack poseStack, KoopaTroopaEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        this.mainHandItem = animatable.getMainHandItem();
        this.offhandItem = animatable.getOffhandItem();
    }
}
