package com.wenxin2.marioverse.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "scale", at = @At("TAIL"))
    private void scaleEntity(T entity, PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        CompoundTag tag = entity.getPersistentData();
        if (tag.contains("marioverse:scale_height")
                && tag.contains("marioverse:scale_width")
                && tag.getFloat("marioverse:scale_height") != tag.getFloat("marioverse:base_scale_height")
                && tag.getFloat("marioverse:scale_width") != tag.getFloat("marioverse:base_scale_width")) {
            poseStack.scale(tag.getFloat("marioverse:scale_width"), tag.getFloat("marioverse:scale_height"), tag.getFloat("marioverse:scale_width"));
        }
    }
}
