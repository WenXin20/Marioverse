package com.wenxin2.marioverse.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.renderers.SuperStarRenderType;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderLayer.class})
public abstract class RenderLayerMixin<T extends Entity, M extends EntityModel<T>> {
    @Inject(method = "coloredCutoutModelCopyLayerRender", at = @At("HEAD"))
    private static <T extends LivingEntity> void renderColoredCutoutModel(EntityModel<T> modelParent, EntityModel<T> model,
         ResourceLocation textureLocation, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
         float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTick, int color, CallbackInfo ci) {

        ShaderInstance shader = SuperStarRenderType.SUPER_STAR_SHADER;
        if (entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR) && !entity.isInvisible()) {
            if (shader != null) {
                float time = (entity.level().getGameTime() + partialTick) * 0.2F;
                shader.safeGetUniform("Time").set(time);
            }

            VertexConsumer consumer = buffer.getBuffer(SuperStarRenderType.superStar(textureLocation));

            poseStack.pushPose();
//                modelParent.copyPropertiesTo(model);
                model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
                model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                poseStack.pushPose();
                    model.renderToBuffer(poseStack, consumer, 0xF000F0,
                            LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
                poseStack.popPose();
            poseStack.popPose();
        }
    }
}
