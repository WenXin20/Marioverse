package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.PokeyModel;
import com.wenxin2.marioverse.entities.PokeyEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.LightLayer;
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
import software.bernie.geckolib.util.Color;

public class PokeyRenderer extends GeoEntityRenderer<PokeyEntity> {
    private static final String HELMET = "armorBipedHead";

    public PokeyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PokeyModel());
        this.shadowRadius = 0.5F;

        this.addRenderLayer(new ItemArmorGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, PokeyEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> animatable.getItemBySlot(EquipmentSlot.HEAD);
                    default -> null;
                };
            }

            @NotNull
            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, PokeyEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> EquipmentSlot.HEAD;
                    default -> super.getEquipmentSlotForBone(bone, stack, animatable);
                };
            }

            @NotNull
            @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, PokeyEntity animatable, HumanoidModel<?> baseModel) {
                return switch (bone.getName()) {
                    case HELMET -> baseModel.head;
                    default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
                };
            }

            @Override
            protected void renderSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock,
                                              MultiBufferSource bufferSource, int packedLight) {
                poseStack.scale(1.35F, 1.35F, 1.35F);
                super.renderSkullAsArmor(poseStack, bone, stack, skullBlock, bufferSource, packedLight);
            }
        });

        this.addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, PokeyEntity animatable) {
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
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, PokeyEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> ItemDisplayContext.HEAD;
                    default -> ItemDisplayContext.NONE;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, PokeyEntity animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (stack.getItem() instanceof BlockItem) {
                    poseStack.scale(0.95F, 0.95F, 0.95F);
                    poseStack.translate(0.0F, 0.5F, 0.0F);
                } else poseStack.translate(0.0F, 0.2F, 0.0F);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public void preRender(PoseStack poseStack, PokeyEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
                          @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        this.model.getBone("spikes").ifPresent(spike -> {
            spike.setHidden(false);
            if (!animatable.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
                spike.setHidden(true);

//            spike.setHidden(animatable.getData(DataAttachmentRegistry.IS_BLOOMING));
        });

        this.model.getBone("flower").ifPresent(flower -> {
//            if (!animatable.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
//                flower.setPosY(0.8125F);
//            else flower.setPosY(0.75F);

//            if (animatable.getData(DataAttachmentRegistry.IS_BLOOMING))
//                flower.setHidden(false);
//            else flower.setHidden(true);
        });
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}