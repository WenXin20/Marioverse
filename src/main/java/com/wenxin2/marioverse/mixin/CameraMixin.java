package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void move(float x, float y, float z);

    @ModifyExpressionValue(method = "setup", at = @At(value = "INVOKE",
            target = "Lnet/neoforged/neoforge/client/ClientHooks;getDetachedCameraDistance" + "(Lnet/minecraft/client/Camera;ZFF)F"))
    private float marioverse$scaleCameraDistance(float originalDistance, BlockGetter level, Entity entity,
                                                 boolean detached, boolean thirdPersonReverse, float partialTick) {
        if (!(entity instanceof LivingEntity living))
            return originalDistance;

        float height = 1.0F;
        float width  = 1.0F;

        var heightScale = living.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        if (heightScale != null)
            height = (float) heightScale.getValue();

        var widthScale = living.getAttribute(AttributesRegistry.WIDTH_SCALE);
        if (widthScale != null)
            width = (float) widthScale.getValue();

        float cameraScale = Math.max(height, width);

        return originalDistance * cameraScale;
    }
}