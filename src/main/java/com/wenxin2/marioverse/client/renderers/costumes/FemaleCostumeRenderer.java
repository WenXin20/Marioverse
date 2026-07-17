package com.wenxin2.marioverse.client.renderers.costumes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.renderers.costumes.layers.CostumeDyeLayer;
import com.wenxin2.marioverse.client.renderers.costumes.layers.CostumeTrimLayer;
import com.wenxin2.marioverse.items.FemaleCostumeItem;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import java.util.Optional;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.RenderUtil;

public class FemaleCostumeRenderer extends DyeableGeoArmorRenderer<FemaleCostumeItem> implements CostumeRendererAccess {
    protected GeoBone dress = null;
    protected GeoBone waist = null;

    private static final DefaultedItemGeoModel<FemaleCostumeItem> FEMALE_MODEL =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/female_costume"));

    public FemaleCostumeRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "costume/female_costume")));
        this.addRenderLayer(new CostumeDyeLayer<>(this));
        this.addRenderLayer(new CostumeTrimLayer<>(this));
    }

    public @Nullable GeoBone getWaistBone(GeoModel<FemaleCostumeItem> model) {
        return model.getBone("armorWaist").orElse(null);
    }

    public @Nullable GeoBone getDressBone(GeoModel<FemaleCostumeItem> model) {
        return model.getBone("armorDress").orElse(null);
    }

    @Override
    public ResourceLocation getTextureLocation(FemaleCostumeItem animatable) {
        ItemStack stack = this.currentStack;
        String layer = this.currentSlot == EquipmentSlot.LEGS ? "layer_2" : "layer_1";

        if (stack.getOrDefault(DataComponentRegistry.HAS_FIRE_FLOWER, false))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                    "textures/item/costume/female_fire_costume_" + layer + ".png");
        if (stack.getOrDefault(DataComponentRegistry.HAS_ICE_FLOWER, false))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                    "textures/item/costume/female_ice_costume_" + layer + ".png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                "textures/item/costume/female_costume_" + layer + ".png");
    }

    @Override
    public GeoModel<FemaleCostumeItem> getGeoModel() {
        return FEMALE_MODEL;
    }

    @Override
    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        super.grabRelevantBones(bakedModel);

        if (this.lastModel != bakedModel) {
            GeoModel<FemaleCostumeItem> model = this.getGeoModel();
            this.lastModel = bakedModel;
            this.dress = this.getDressBone(model);
            this.waist = this.getWaistBone(model);
        }
    }

    @Override
    public void renderChildBones(PoseStack poseStack, FemaleCostumeItem animatable, GeoBone bone, RenderType renderType,
                                 MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                                 int packedLight, int packedOverlay, int colour) {
        if ((bone.getName().equals("armorWaist") || bone.getName().equals("armorDress"))
                && currentSlot != EquipmentSlot.LEGS)
            return;
        super.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot slot) {
        this.setAllBonesVisible(false);
        HumanoidModel<?> model = this;
        Optional<GeoBone> dressBone = this.getGeoModel().getBone("armorDress");
        Optional<GeoBone> waistBone = this.getGeoModel().getBone("armorWaist");

        dressBone.ifPresent(geoBone -> this.setBoneVisible(geoBone, false));
        waistBone.ifPresent(geoBone -> this.setBoneVisible(geoBone, false));

        switch (currentSlot) {
            case HEAD:
                this.setBoneVisible(this.head, model.head.visible);
                break;
            case CHEST:
                this.setBoneVisible(this.body, model.body.visible);
                this.setBoneVisible(this.rightArm, model.rightArm.visible);
                this.setBoneVisible(this.leftArm, model.leftArm.visible);
                break;
            case LEGS:
                dressBone.ifPresent(geoBone -> this.setBoneVisible(geoBone, model.body.visible));
                waistBone.ifPresent(geoBone -> this.setBoneVisible(geoBone, model.body.visible));
                this.setBoneVisible(this.rightLeg, model.rightLeg.visible);
                this.setBoneVisible(this.leftLeg, model.leftLeg.visible);
                break;
            case FEET:
                this.setBoneVisible(this.rightBoot, model.rightLeg.visible);
                this.setBoneVisible(this.leftBoot, model.leftLeg.visible);
                break;
        }
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

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public ItemStack getCurrentStack() {
        return this.currentStack;
    }

    @Override
    public EquipmentSlot getCurrentSlot() {
        return this.currentSlot;
    }

    public int getDefaultDyeColor() {
        if (this.getCurrentStack().getItem() instanceof ArmorItem armorItem) {
            if (armorItem.getEquipmentSlot() == EquipmentSlot.HEAD)
                return 0xFFFF647D;
            if (armorItem.getEquipmentSlot() == EquipmentSlot.CHEST)
                return 0xFFFFC1D7;
            if (armorItem.getEquipmentSlot() == EquipmentSlot.LEGS)
                return 0xFFFFC1D7;
            if (armorItem.getEquipmentSlot() == EquipmentSlot.FEET)
                return 0xFFFFC1D7;
        }
        return 0xFFF6343A;
    }

    @Override
    protected boolean isBoneDyeable(GeoBone geoBone) {
        return true;
    }

    @NotNull
    @Override
    protected Color getColorForBone(GeoBone geoBone) {
        return Color.ofOpaque(this.getDefaultDyeColor());
    }
}