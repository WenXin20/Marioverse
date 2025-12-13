package com.wenxin2.marioverse.client.models.entities.projectiles;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class LargeSnowballModel extends DefaultedEntityGeoModel<LargeSnowballProjectile> {
    public LargeSnowballModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "snowball/large_snowball"));
    }

    @Override
    public RenderType getRenderType(LargeSnowballProjectile animatable, ResourceLocation texture) {
        return RenderType.entitySolid(this.getTextureResource(animatable));
    }
}
