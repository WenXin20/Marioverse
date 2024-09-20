package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.FireFlowerCostumeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class FireFlowerCostumeRenderer extends GeoArmorRenderer<FireFlowerCostumeItem> {
    public FireFlowerCostumeRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "costume/fire_flower_costume")));
    }
}
