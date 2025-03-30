package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.LuigiCostumeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class LuigiCostumeRenderer extends GeoArmorRenderer<LuigiCostumeItem> {
    public LuigiCostumeRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "costume/luigi_costume")));
    }
}
