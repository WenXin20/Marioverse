package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.power_ups.MegaMushroomEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MegaMushroomModel extends DefaultedEntityGeoModel<MegaMushroomEntity> {
    public MegaMushroomModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "power_up/mega_mushroom"));
    }

    @Override
    public RenderType getRenderType(MegaMushroomEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(this.getTextureResource(animatable));
    }
}