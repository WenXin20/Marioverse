package com.wenxin2.marioverse.client.renderers.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.renderers.SuperStarRenderType;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.embeddedt.embeddium.impl.gl.shader.ShaderType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SuperStarGeoLayer<T extends LivingEntity & GeoAnimatable> extends GeoRenderLayer<T> {
    public SuperStarGeoLayer(GeoEntityRenderer<T> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource,
                       @Nullable VertexConsumer buffer, float partialTicks, int packedLight, int packedOverlay) {
        ShaderInstance shader = SuperStarRenderType.SUPER_STAR_SHADER;
        if (entity instanceof AbilitiesHandler handler && handler.mv$hasSuperStar()) {
            if (shader != null) {
                float time = (entity.level().getGameTime() + partialTicks);
                shader.safeGetUniform("Time").set(time);
            }

            ResourceLocation texture = this.getTextureResource( entity);
            VertexConsumer consumer = bufferSource.getBuffer(SuperStarRenderType.superStar(texture));

            poseStack.pushPose();
                this.getRenderer().actuallyRender(poseStack, entity, bakedModel, renderType, bufferSource, consumer, true, partialTicks, 0xF000F0,
                        LivingEntityRenderer.getOverlayCoords(entity, 1.0F), -1);
            poseStack.popPose();
        }
    }
}
