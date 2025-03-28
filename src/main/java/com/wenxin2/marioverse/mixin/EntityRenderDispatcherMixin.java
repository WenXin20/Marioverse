package com.wenxin2.marioverse.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderHitbox", at = @At("HEAD"))
    private static void injectRenderHitbox(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity,
            float tickDelta, float red, float green, float blue, CallbackInfo ci) {
        AABB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());

        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            double height = attributeMap.getValue(AttributesRegistry.HEIGHT_SCALE);
            double width = attributeMap.getValue(AttributesRegistry.WIDTH_SCALE);
            double heightScale = height < 1 ? 2 : 1;
            double widthScale = width < 1 ? 1.35 : 1;
            if (height < 0.95 || width < 0.95) {
                LevelRenderer.renderLineBox(poseStack, vertexConsumer,
                        aabb.minX * widthScale, aabb.minY, aabb.minZ * widthScale,
                        aabb.maxX * widthScale, aabb.maxY * heightScale, aabb.maxZ * widthScale,
                        red, green, 0.0F, 1.0F);
            }
        }
    }
}
