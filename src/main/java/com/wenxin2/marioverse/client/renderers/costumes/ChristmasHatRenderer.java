package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.renderers.costumes.layers.CostumeTrimLayer;
import com.wenxin2.marioverse.items.ChristmasHatItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ChristmasHatRenderer extends GeoArmorRenderer<ChristmasHatItem> implements CostumeRendererAccess {
    private static final DefaultedItemGeoModel<ChristmasHatItem> CHRISTMAS_HAT =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/christmas_hat"));

    public ChristmasHatRenderer() {
        super(CHRISTMAS_HAT);
        this.addRenderLayer(new CostumeTrimLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ChristmasHatItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/christmas_hat_layer_1.png");
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
