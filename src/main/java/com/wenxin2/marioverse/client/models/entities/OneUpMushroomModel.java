package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.OneUpMushroomEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OneUpMushroomModel extends DefaultedEntityGeoModel<OneUpMushroomEntity> {
    public OneUpMushroomModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "power_up/one_up_mushroom"));
    }

    @Override
    public RenderType getRenderType(OneUpMushroomEntity animatable, ResourceLocation texture) {
        return RenderType.entitySolid(getTextureResource(animatable));
    }
}
