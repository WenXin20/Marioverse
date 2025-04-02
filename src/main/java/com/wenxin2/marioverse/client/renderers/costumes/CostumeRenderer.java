package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.CostumeItem;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
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
    protected void applyBoneVisibilityBySlot(EquipmentSlot slot) {
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
                this.getGeoModel().getBone("armorWaist").ifPresent(bone -> bone.setHidden(false));
                this.setBoneVisible(this.rightLeg, true);
                this.setBoneVisible(this.leftLeg, true);
                break;
            case FEET:
                this.setBoneVisible(this.rightBoot, true);
                this.setBoneVisible(this.leftBoot, true);
        }
        super.applyBoneVisibilityBySlot(slot);
    }
}
