package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.KoopaShoesItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class KoopaShoesRenderer extends GeoArmorRenderer<KoopaShoesItem> {
    private static final DefaultedItemGeoModel<KoopaShoesItem> KOOPA_SHOES =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/koopa_shoes"));

    public KoopaShoesRenderer() {
        super(KOOPA_SHOES);
    }

    @Override
    public ResourceLocation getTextureLocation(KoopaShoesItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/koopa_shoes.png");
    }
}
