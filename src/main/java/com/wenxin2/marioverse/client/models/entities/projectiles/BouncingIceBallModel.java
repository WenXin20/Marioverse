package com.wenxin2.marioverse.client.models.entities.projectiles;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BouncingIceBallModel extends DefaultedEntityGeoModel<BouncingIceBallProjectile> {
    public BouncingIceBallModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "projectile/bouncing_ice_ball"));
    }

    @Override
    public RenderType getRenderType(BouncingIceBallProjectile animatable, ResourceLocation texture) {
        return RenderType.entityCutout(getTextureResource(animatable));
    }
}
