package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.models.entities.GoombaModel;
import com.wenxin2.marioverse.entities.GoombaEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;
import software.bernie.geckolib.util.ClientUtil;

public class GoombaRenderer extends GeoEntityRenderer<GoombaEntity> {
    private static final String HELMET = "armorBipedHead";

    public GoombaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GoombaModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected @Nullable RenderType getRenderType(GoombaEntity animatable, @Nullable MultiBufferSource bufferSource) {
                if (animatable.hasCustomName() && ("Goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString()))
                        || "goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString())))) {
                    if (animatable instanceof Entity entity) {
                        boolean invisible = entity.isInvisible();
                        ResourceLocation texture = AutoGlowingTexture.getEmissiveResource(this.getTextureResource(animatable));
                        if (invisible && !entity.isInvisibleTo(ClientUtil.getClientPlayer())) {
                            return RenderType.itemEntityTranslucentCull(texture);
                        } else if (Minecraft.getInstance().shouldEntityAppearGlowing(entity)) {
                            return invisible ? RenderType.outline(texture) : AutoGlowingTexture.getOutlineRenderType(this.getTextureResource(animatable));
                        } else {
                            return invisible ? null : AutoGlowingTexture.getRenderType(this.getTextureResource(animatable));
                        }
                    } else {
                        return AutoGlowingTexture.getRenderType(this.getTextureResource(animatable));
                    }
                } else return null;
            }
        });

        addRenderLayer(new ItemArmorGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, GoombaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> animatable.getItemBySlot(EquipmentSlot.HEAD);
                    default -> null;
                };
            }

            @NotNull
            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, GoombaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> EquipmentSlot.HEAD;
                    default -> super.getEquipmentSlotForBone(bone, stack, animatable);
                };
            }

            @NotNull
            @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, GoombaEntity animatable, HumanoidModel<?> baseModel) {
                return switch (bone.getName()) {
                    case HELMET -> baseModel.head;
                    default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
                };
            }

            @Override
            protected void renderSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock, MultiBufferSource bufferSource, int packedLight) {
                poseStack.scale(1.0F, 1.0F, 0.94F);
                poseStack.translate(0.0F, 0.0F, 0.0F);
                super.renderSkullAsArmor(poseStack, bone, stack, skullBlock, bufferSource, packedLight);
            }

            private void renderGoombellaSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock, MultiBufferSource bufferSource, int packedLight) {
                poseStack.scale(1.28F, 1.28F, 1.22F);
                poseStack.translate(0.0F, -0.125F, 0.0F);
                super.renderSkullAsArmor(poseStack, bone, stack, skullBlock, bufferSource, packedLight);
            }

            @Override
            public void renderForBone(PoseStack poseStack, GoombaEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                ItemStack armorStack = this.getArmorItemForBone(bone, animatable);
                if (animatable.hasCustomName() && ("Goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString()))
                        || "goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString())))) {
                    if (armorStack != null) {
                        Item var13 = armorStack.getItem();
                        if (var13 instanceof BlockItem) {
                            BlockItem blockItem = (BlockItem) var13;
                            Block var17 = blockItem.getBlock();
                            if (var17 instanceof AbstractSkullBlock) {
                                AbstractSkullBlock skullBlock = (AbstractSkullBlock) var17;
                                this.renderGoombellaSkullAsArmor(poseStack, bone, armorStack, skullBlock, bufferSource, packedLight);
                                return;
                            }
                        }
                    }
                } else super.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            }
        });

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, GoombaEntity animatable) {
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
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, GoombaEntity animatable) {
                return switch (bone.getName()) {
                    case HELMET -> ItemDisplayContext.HEAD;
                    default -> ItemDisplayContext.NONE;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, GoombaEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (animatable.hasCustomName() && ("Goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString()))
                        || "goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString())))) {
                    poseStack.scale(0.77F, 0.77F, 0.72F);
                    poseStack.translate(0.0F, 0.5F, 0.0F);
                } else {
                    poseStack.scale(0.6F, 0.6F, 0.55F);
                    poseStack.translate(0.0F, 0.5F, 0.0F);
                }
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    protected float getDeathMaxRotation(GoombaEntity animatable) {
        return 0.0F;
    }
}
