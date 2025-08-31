package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.KoopaTroopaModel;
import com.wenxin2.marioverse.entities.GoldKoopaTroopaEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
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
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

public class KoopaTroopaRenderer extends GeoEntityRenderer<KoopaTroopaEntity> {
    private static final String HELMET = "armorHead";
    private static final String CHEST = "armorBody";
    private static final String DRESS = "armorDress";
    private static final String WAIST = "armorWaist";
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
                    case LEFT_LEG, RIGHT_LEG, DRESS, WAIST -> this.leggingsStack;
                    case LEFT_BOOT, RIGHT_BOOT -> this.bootsStack;
                    default -> super.getArmorItemForBone(bone, animatable);
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
                    case LEFT_LEG, RIGHT_LEG, DRESS, WAIST -> EquipmentSlot.LEGS;
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
                    case LEFT_LEG, LEFT_BOOT, DRESS, WAIST -> baseModel.leftLeg;
                    case RIGHT_LEG, RIGHT_BOOT -> baseModel.rightLeg;
                    default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
                };
            }

            @Override
            protected void renderSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock,
                                              MultiBufferSource bufferSource, int packedLight) {
                poseStack.scale(1.0F, 1.0F, 1.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-30));
                poseStack.translate(0.0F, 0.1F, 0.75F);
                super.renderSkullAsArmor(poseStack, bone, stack, skullBlock, bufferSource, packedLight);
            }
        });

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, KoopaTroopaEntity animatable) {
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
                    poseStack.translate(0.0, 0.125, -0.5);

                    if (stack.getItem() instanceof ShieldItem)
                        poseStack.translate(-0.05, 0.0, 0.4);
                } else if (stack == KoopaTroopaRenderer.this.offhandItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                    poseStack.translate(0.0, 0.125, -0.5);

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(-0.05, 0.0, 0.4);
                        poseStack.mulPose(Axis.YP.rotationDegrees(180));
                    }
                } else if (stack.getItem() instanceof BlockItem) {
                    poseStack.scale(0.63F, 0.63F, 0.63F);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-30));
                    poseStack.translate(0.0F, 0.6F, 0.1F);
                } else {
                    poseStack.scale(0.6F, 0.6F, 0.6F);
                    poseStack.translate(0.0F, 0.5F, 0.0F);
                }
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public void preRender(PoseStack poseStack, KoopaTroopaEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        this.mainHandItem = animatable.getMainHandItem();
        this.offhandItem = animatable.getOffhandItem();
    }

    @Override
    public void renderFinal(PoseStack poseStack, KoopaTroopaEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource,
                            @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int color) {
        if (animatable instanceof GoldKoopaTroopaEntity) {
            this.model.getBone("shell_particles").ifPresent(bone -> {
                Vector3d bonePos = bone.getWorldPosition();

                if (animatable.tickCount % 80 == 0) {
                    double randomX = (Math.random() - 0.5) * bone.getScaleX();
                    double randomY = (Math.random() - 0.5) * bone.getScaleY();
                    double randomZ = (Math.random() - 0.5) * bone.getScaleZ();

                    animatable.getCommandSenderWorld().addParticle(ParticleRegistry.COIN_GLINT.get(),
                            bonePos.x() + randomX, bonePos.y() + randomY, bonePos.z() + randomZ,
                            0, 0, 0);
                }
            });
        }
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, color);
    }
}
