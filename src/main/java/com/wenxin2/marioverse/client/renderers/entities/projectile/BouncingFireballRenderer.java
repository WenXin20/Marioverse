package com.wenxin2.marioverse.client.renderers.entities.projectile;

import com.wenxin2.marioverse.client.models.entities.projectiles.BouncingFireballModel;
import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BouncingFireballRenderer extends GeoEntityRenderer<BouncingFireballProjectile> {
    public BouncingFireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BouncingFireballModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
