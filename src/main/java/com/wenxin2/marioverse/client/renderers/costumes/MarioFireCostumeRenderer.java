package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.MarioFireCostumeItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MarioFireCostumeRenderer extends GeoArmorRenderer<MarioFireCostumeItem> {
    public MarioFireCostumeRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "costume/mario_fire_costume")));
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
