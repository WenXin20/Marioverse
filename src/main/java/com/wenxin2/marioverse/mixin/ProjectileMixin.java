package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.entities.BooEntity;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public class ProjectileMixin {
    @Inject(method = "canHitEntity", at = @At("HEAD"), cancellable = true)
    private void mv$skipHiddenTargets(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof PiranhaPlantEntity plant && plant.isHiding())
            cir.setReturnValue(false);
        if (target instanceof BooEntity && target.level().getBrightness(LightLayer.BLOCK,
                target.blockPosition()) < ConfigRegistry.BOO_LIGHT_SENSITIVITY.get())
            cir.setReturnValue(false);
    }
}
