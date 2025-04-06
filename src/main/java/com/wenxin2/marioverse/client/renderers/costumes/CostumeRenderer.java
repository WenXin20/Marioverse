package com.wenxin2.marioverse.client.renderers.costumes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.CostumeItem;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Optional;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.RenderUtil;

public class CostumeRenderer extends GeoArmorRenderer<CostumeItem> {
    protected GeoBone dress = null;
    protected GeoBone waist = null;

    private static final DefaultedItemGeoModel<CostumeItem> MARIO_MODEL =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/mario_costume"));
    private static final DefaultedItemGeoModel<CostumeItem> LUIGI_MODEL =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/luigi_costume"));
    private static final DefaultedItemGeoModel<CostumeItem> PEACH_MODEL =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/peach_costume"));

    public CostumeRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "costume/mario_costume")));
    }

    public @Nullable GeoBone getWaistBone(GeoModel<CostumeItem> model) {
        return model.getBone("armorWaist").orElse(null);
    }

    public @Nullable GeoBone getDressBone(GeoModel<CostumeItem> model) {
        return model.getBone("armorDress").orElse(null);
    }

    @Override
    public ResourceLocation getTextureLocation(CostumeItem animatable) {
        ItemStack stack = this.currentStack;
        if (stack.is(TagRegistry.FIRE_COSTUMES)) {
            if (stack.is(TagRegistry.LUIGI_FIRE_COSTUMES))
                return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/luigi_fire_costume.png");
            else if (stack.is(TagRegistry.PEACH_FIRE_COSTUMES))
                return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/peach_fire_costume.png");
            else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/mario_fire_costume.png");
        } else if (stack.is(TagRegistry.ICE_COSTUMES)) {
            if (stack.is(TagRegistry.LUIGI_ICE_COSTUMES))
                return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/luigi_ice_costume.png");
            else if (stack.is(TagRegistry.PEACH_ICE_COSTUMES))
                return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/peach_ice_costume.png");
            else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/mario_ice_costume.png");
        } else if (stack.is(TagRegistry.MARIO_COSTUMES)) {
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/mario_costume.png");
        } else if (stack.is(TagRegistry.LUIGI_COSTUMES)) {
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/luigi_costume.png");
        } else if (stack.is(TagRegistry.PEACH_COSTUMES)) {
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/peach_costume.png");
        } else return super.getTextureLocation(animatable);
    }

    @Override
    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        super.grabRelevantBones(bakedModel);

        if (this.lastModel != bakedModel) {
            GeoModel<CostumeItem> model = this.getGeoModel();
            this.lastModel = bakedModel;
            this.waist = this.getWaistBone(model);
            this.dress = this.getDressBone(model);
        }
    }

    @Override
    public GeoModel<CostumeItem> getGeoModel() {
        ItemStack stack = this.currentStack;

        if (stack.is(TagRegistry.MARIO_COSTUMES))
            return MARIO_MODEL;
        else if (stack.is(TagRegistry.LUIGI_COSTUMES))
            return LUIGI_MODEL;
        else if (stack.is(TagRegistry.PEACH_COSTUMES))
            return PEACH_MODEL;
        else return super.getGeoModel();
    }

    @Override
    public void renderChildBones(PoseStack poseStack, CostumeItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if ((bone.getName().equals("armorWaist") || bone.getName().equals("armorDress")) && currentSlot != EquipmentSlot.LEGS) {
            return;
        }
        super.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot slot) {
        super.applyBoneVisibilityBySlot(slot);

        this.getGeoModel().getBone("armorDress").ifPresent(bone -> bone.setHidden(true));
        this.getGeoModel().getBone("armorWaist").ifPresent(bone -> bone.setHidden(true));

        switch (currentSlot) {
            case HEAD:
                this.setBoneVisible(this.head, true);
                break;
            case CHEST:
                this.setBoneVisible(this.body, true);
                this.setBoneVisible(this.rightArm, true);
                this.setBoneVisible(this.leftArm, true);
                break;
            case LEGS:
                this.getGeoModel().getBone("armorDress").ifPresent(bone -> bone.setHidden(false));
                this.getGeoModel().getBone("armorWaist").ifPresent(bone -> bone.setHidden(false));
                this.setBoneVisible(this.waist, true);
                this.setBoneVisible(this.rightLeg, true);
                this.setBoneVisible(this.leftLeg, true);
                break;
            case FEET:
                this.setBoneVisible(this.rightBoot, true);
                this.setBoneVisible(this.leftBoot, true);
        }
    }

    @Override
    public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
        super.applyBoneVisibilityByPart(currentSlot, currentPart, model);
    }

    @Override
    protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
        super.applyBaseTransformations(baseModel);
        Optional<GeoBone> dressBone = this.getGeoModel().getBone("armorDress");
        Optional<GeoBone> waistBone = this.getGeoModel().getBone("armorWaist");

        if (waistBone.isPresent()) {
            ModelPart bodyPart = baseModel.body;
            RenderUtil.matchModelPartRot(bodyPart, waistBone.get());
            waistBone.get().updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);

            if (baseModel.crouching) {
                waistBone.get().updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
                waistBone.get().setRotX(-bodyPart.xRot);
            }
        }

        if (dressBone.isPresent()) {
            ModelPart bodyPart = baseModel.body;
            dressBone.get().updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);

            if (baseModel.crouching)
                dressBone.get().updatePosition(bodyPart.x, -bodyPart.y + 4.0F, bodyPart.z + 5.5F);
        }
    }
}
