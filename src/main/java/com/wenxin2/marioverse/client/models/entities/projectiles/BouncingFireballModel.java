package com.wenxin2.marioverse.client.models.entities.projectiles;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BouncingFireballModel extends DefaultedEntityGeoModel<BouncingFireballProjectile> {
    public BouncingFireballModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "projectile/bouncing_fireball"));
    }

    @Override
    public RenderType getRenderType(BouncingFireballProjectile animatable, ResourceLocation texture) {
        return RenderType.entityCutout(getTextureResource(animatable));
    }
}
