package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MushroomModel extends DefaultedEntityGeoModel<MushroomEntity> {
    public MushroomModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "power_up/mushroom"));
    }

    @Override
    public RenderType getRenderType(MushroomEntity animatable, ResourceLocation texture) {
        return RenderType.entitySolid(getTextureResource(animatable));
    }
}
