package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.registries.AttributesRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Shadow protected float shadowRadius;
    @Unique private float mv$baseShadowRadius = -1.0F;

    @Inject(method = "getShadowRadius(Lnet/minecraft/world/entity/Entity;)F", at = @At("RETURN"), cancellable = true)
    public void getShadowRadius(T entity, CallbackInfoReturnable<Float> cir) {
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributemap = livingEntity.getAttributes();
            float widthScale = (float) attributemap.getValue(AttributesRegistry.WIDTH_SCALE);

            if (mv$baseShadowRadius < 0.0F)
                mv$baseShadowRadius = this.shadowRadius;

            if (widthScale >= 1.5)
                cir.setReturnValue(cir.getReturnValue() * widthScale / 2.25F);

            if ((Object) this instanceof GeoEntityRenderer<?>)
                this.shadowRadius = mv$baseShadowRadius * widthScale;
        }
    }
}
