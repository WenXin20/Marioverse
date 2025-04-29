package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.KoopaShoesItem;
import com.wenxin2.marioverse.registries.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
        ItemStack stack = this.currentStack;
        if (stack.is(ItemRegistry.GOLDEN_KOOPA_SHOES))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/golden_koopa_shoes.png");
        else if (stack.is(ItemRegistry.RED_KOOPA_SHOES))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/red_koopa_shoes.png");
        else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/green_koopa_shoes.png");
    }
}
