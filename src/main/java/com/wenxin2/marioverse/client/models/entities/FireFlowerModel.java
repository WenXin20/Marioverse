package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.FireFlowerEntity;
import com.wenxin2.marioverse.entities.MushroomEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FireFlowerModel extends DefaultedEntityGeoModel<FireFlowerEntity> {
    public FireFlowerModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "power_up/fire_flower"));
    }

    @Override
    public RenderType getRenderType(FireFlowerEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(getTextureResource(animatable));
    }
}
