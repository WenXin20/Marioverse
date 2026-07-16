package com.wenxin2.marioverse.client.renderers.costumes;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.renderers.costumes.layers.CostumeDyeLayer;
import com.wenxin2.marioverse.client.renderers.costumes.layers.CostumeTrimLayer;
import com.wenxin2.marioverse.items.ChristmasHatItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;

public class ChristmasHatRenderer extends DyeableGeoArmorRenderer<ChristmasHatItem> implements CostumeRendererAccess {
    private static final DefaultedItemGeoModel<ChristmasHatItem> CHRISTMAS_HAT =
            new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume/christmas_hat"));

    public ChristmasHatRenderer() {
        super(CHRISTMAS_HAT);
        this.addRenderLayer(new CostumeDyeLayer<>(this));
        this.addRenderLayer(new CostumeTrimLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ChristmasHatItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/item/costume/christmas_hat.png");
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public ItemStack getCurrentStack() {
        return this.currentStack;
    }

    @Override
    public EquipmentSlot getCurrentSlot() {
        return this.currentSlot;
    }

    @Override
    protected boolean isBoneDyeable(GeoBone geoBone) {
        return true;
    }

    @NotNull
    @Override
    protected Color getColorForBone(GeoBone geoBone) {
        return Color.ofOpaque(0xFFFFFFFF);
    }
}
