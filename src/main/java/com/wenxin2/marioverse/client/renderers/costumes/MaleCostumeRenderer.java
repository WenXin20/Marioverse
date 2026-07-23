package com.wenxin2.marioverse.client.renderers.costumes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.renderers.costumes.layers.CostumeDyeLayer;
import com.wenxin2.marioverse.client.renderers.costumes.layers.CostumeTrimLayer;
import com.wenxin2.marioverse.client.renderers.costumes.layers.CostumeWaistLayer;
import com.wenxin2.marioverse.items.MaleCostumeItem;
import com.wenxin2.marioverse.power_up.PowerUpType;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.PowerUpTypeRegistry;
import java.util.Optional;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.RenderUtil;

public class MaleCostumeRenderer extends DyeableGeoArmorRenderer<MaleCostumeItem> implements CostumeTextureAccess {
    protected GeoBone dress = null;
    protected GeoBone waist = null;

    private static final DefaultedItemGeoModel<MaleCostumeItem> MALE_MODEL =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/male_costume"));

    public MaleCostumeRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "costume/male_costume")));
        this.addRenderLayer(new CostumeDyeLayer<>(this));
        this.addRenderLayer(new CostumeTrimLayer<>(this));
        this.addRenderLayer(new CostumeWaistLayer<>(this));
    }

    public @Nullable GeoBone getWaistBone(GeoModel<MaleCostumeItem> model) {
        return model.getBone("armorWaist").orElse(null);
    }

    public @Nullable GeoBone getDressBone(GeoModel<MaleCostumeItem> model) {
        return model.getBone("armorDress").orElse(null);
    }

    @Override
    public ResourceLocation getTextureLocation(MaleCostumeItem animatable) {
        ItemStack stack = this.currentStack;
        String layer = this.currentSlot == EquipmentSlot.LEGS ? "layer_2" : "layer_1";
        Holder<PowerUpType> type = stack.get(DataComponentRegistry.POWER_UP_TYPE.get());

        if (type != null && type.value() == PowerUpTypeRegistry.FIRE_FLOWER.value())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                    "textures/item/costume/male_fire_costume_" + layer + ".png");
        if (type != null && type.value() == PowerUpTypeRegistry.ICE_FLOWER.value())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                    "textures/item/costume/male_ice_costume_" + layer + ".png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                "textures/item/costume/male_costume_" + layer + ".png");
    }

    @Override
    public ResourceLocation getWaistTextureLocation() {
        ItemStack stack = this.currentStack;
        String layer = "layer_2";
        Holder<PowerUpType> type = stack.get(DataComponentRegistry.POWER_UP_TYPE.get());

        if (type != null && type.value() == PowerUpTypeRegistry.FIRE_FLOWER.value())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                    "textures/item/costume/male_fire_costume_" + layer + ".png");
        if (type != null && type.value() == PowerUpTypeRegistry.ICE_FLOWER.value())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                    "textures/item/costume/male_ice_costume_" + layer + ".png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                "textures/item/costume/male_costume_" + layer + ".png");
    }

    @Override
    public ResourceLocation getDressTextureLocation() {
        return this.getTextureLocation(null);
    }

    @Override
    public ResourceLocation getDressOverlayTextureLocation() {
        return this.getTextureLocation(null);
    }

    @Override
    public ResourceLocation getDressBackTextureLocation() {
        return this.getTextureLocation(null);
    }

    @Override
    public GeoModel<MaleCostumeItem> getGeoModel() {
        return MALE_MODEL;
    }

    @Override
    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        super.grabRelevantBones(bakedModel);

        if (this.lastModel != bakedModel) {
            GeoModel<MaleCostumeItem> model = this.getGeoModel();
            this.lastModel = bakedModel;
            this.dress = this.getDressBone(model);
            this.waist = this.getWaistBone(model);
        }
    }

    @Override
    public void renderChildBones(PoseStack poseStack, MaleCostumeItem animatable, GeoBone bone, RenderType renderType,
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
                return 0xFFF6343A;
            if (armorItem.getEquipmentSlot() == EquipmentSlot.CHEST)
                return 0xFFF6343A;
            if (armorItem.getEquipmentSlot() == EquipmentSlot.LEGS)
                return 0xFF325EFF;
            if (armorItem.getEquipmentSlot() == EquipmentSlot.FEET)
                return 0xFFA94535;
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

    @Override
    public int getDyeColor() {
        ItemStack stack = this.currentStack;
        DyedItemColor dyedColor = stack.get(DataComponents.DYED_COLOR);
        if (dyedColor != null)
            return 0xFF000000 | dyedColor.rgb();
        return this.getDefaultDyeColor();
    }
}