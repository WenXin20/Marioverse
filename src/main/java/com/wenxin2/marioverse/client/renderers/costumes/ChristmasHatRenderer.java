package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.ChristmasHatItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ChristmasHatRenderer extends GeoArmorRenderer<ChristmasHatItem> {
    private static final DefaultedItemGeoModel<ChristmasHatItem> CHRISTMAS_HAT =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/christmas_hat"));

    public ChristmasHatRenderer() {
        super(CHRISTMAS_HAT);
    }

    @Override
    public ResourceLocation getTextureLocation(ChristmasHatItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/christmas_hat.png");
    }
}
