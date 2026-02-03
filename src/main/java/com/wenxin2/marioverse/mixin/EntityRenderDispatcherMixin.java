package com.wenxin2.marioverse.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @ModifyVariable(method = "renderHitbox", at = @At("STORE"), ordinal = 0)
    private static AABB renderHitbox(AABB aabb, PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity,
                                     float tickDelta, float red, float green, float blue) {
        if (!(entity instanceof LivingEntity livingEntity))
            return aabb;

        double heightScale = livingEntity.getAttributes().getValue(AttributesRegistry.HEIGHT_SCALE);
        double widthScale  = livingEntity.getAttributes().getValue(AttributesRegistry.WIDTH_SCALE);

        double width = (1.0 / widthScale) - 1.0;
        double height = (1.0 / heightScale) - 1.0;

        return new AABB(aabb.minX - aabb.getXsize() * width / 2,
                aabb.minY,
                aabb.minZ - aabb.getZsize() * width / 2,
                aabb.maxX + aabb.getXsize() * width / 2,
                aabb.maxY + aabb.getYsize() * height,
                aabb.maxZ + aabb.getZsize() * width / 2);
    }
}
