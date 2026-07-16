package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.renderers.costumes.layers.CostumeTrimLayer;
import com.wenxin2.marioverse.items.PlasticBucketItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PlasticBucketRenderer extends GeoArmorRenderer<PlasticBucketItem> implements CostumeRendererAccess {
    private static final DefaultedItemGeoModel<PlasticBucketItem> PLASTIC_BUCKET =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/plastic_bucket"));

    public PlasticBucketRenderer() {
        super(PLASTIC_BUCKET);
        this.addRenderLayer(new CostumeTrimLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(PlasticBucketItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/plastic_bucket.png");
    }

    @Override
    public ItemStack getCurrentStack() {
        return this.currentStack;
    }

    @Override
    public EquipmentSlot getCurrentSlot() {
        return this.currentSlot;
    }
}
