package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.power_ups.DashMushroomEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DashMushroomModel extends DefaultedEntityGeoModel<DashMushroomEntity> {
    public DashMushroomModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "power_up/dash_mushroom"));
    }

    @Override
    public RenderType getRenderType(DashMushroomEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(this.getTextureResource(animatable));
    }
}