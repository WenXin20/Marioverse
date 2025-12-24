package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.PlasticBucketItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PlasticBucketRenderer extends GeoArmorRenderer<PlasticBucketItem> {
    private static final DefaultedItemGeoModel<PlasticBucketItem> PLASTIC_BUCKET =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/plastic_bucket"));

    public PlasticBucketRenderer() {
        super(PLASTIC_BUCKET);
    }

    @Override
    public ResourceLocation getTextureLocation(PlasticBucketItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/plastic_bucket.png");
    }
}
