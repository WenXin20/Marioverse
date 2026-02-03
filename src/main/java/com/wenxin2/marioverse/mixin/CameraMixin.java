package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void move(float x, float y, float z);

    @ModifyExpressionValue(method = "setup", at = @At(value = "INVOKE",
            target = "Lnet/neoforged/neoforge/client/ClientHooks;getDetachedCameraDistance" + "(Lnet/minecraft/client/Camera;ZFF)F"))
    private float setup(float originalDistance, BlockGetter level, Entity entity,
                        boolean detached, boolean thirdPersonReverse, float partialTick) {
        float cameraScale = 1.0F;

        if (entity instanceof LivingEntity living)
            cameraScale = Math.max(cameraScale, mv$getScaleFromAttributes(living));

        Entity vehicle = entity.getVehicle();
        if (vehicle instanceof LivingEntity livingVehicle)
            cameraScale = Math.max(cameraScale, mv$getScaleFromAttributes(livingVehicle));

        if (cameraScale <= 1.0F)
            return originalDistance;

        return originalDistance * cameraScale;
    }

    @Unique
    private static float mv$getScaleFromAttributes(LivingEntity living) {
        float height = 1.0F;
        float width = 1.0F;

        var heightScale = living.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        if (heightScale != null)
            height = (float) heightScale.getValue();

        var widthScale = living.getAttribute(AttributesRegistry.WIDTH_SCALE);
        if (widthScale != null)
            width = (float) widthScale.getValue();

        return Math.max(height, width);
    }
}